package net;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import ui.Main;
import utils.MessageSend;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    
    Main main;
    String ipAddr;
    public boolean isRunning = true;
    
    public MulticastReceiver(Main m, String ipAd) {
    	main = m;
    	ipAddr = ipAd;
    }

    @SuppressWarnings("deprecation")
	public void run() {
        try {
			socket = new MulticastSocket(4446);
			
			InetAddress group = InetAddress.getByName(ipAddr);
	        socket.joinGroup(group);
	        //new MulticastPublisher().send("Un nouvel utilisateur à rejoins le salon");
	        while (isRunning) {
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            final ByteArrayInputStream bain = new ByteArrayInputStream(packet.getData());
	            final ObjectInputStream ois = new ObjectInputStream(bain);
	            MessageSend msgSent = (MessageSend) ois.readObject();
	            if (msgSent.user.verifUser().equals(main.user.verifUser())) {
	            	main.addRecivedMsg(msgSent.toStringU());
	            } else {
	            	main.addRecivedMsg(msgSent.toString());
	            }
	            if (msgSent.toString() == "end") {
	            	isRunning = false;
	            }
	        }
	        socket.leaveGroup(group);
	        socket.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
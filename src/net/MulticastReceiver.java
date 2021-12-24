package net;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import ui.Main;
import utils.MessageSend;
import utils.User;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    
    Main main;
    String ipAddr;
    public boolean isRunning = true;
    
    public MulticastReceiver(Main m, String ipAd) {
    	main = m;
    	ipAddr = ipAd;
    	start();
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
	            if (msgSent.getType().equals("leave")) {
	            	if (!isUserSender(msgSent.getUser(), main.user)) {
	            		main.addRecivedMsg(msgSent.getUser().toString() + " a quitté la session");
	            	}
	            } else if (msgSent.getType().equals("join")) {
	            	if (!isUserSender(msgSent.getUser(), main.user)) {
	            		main.addRecivedMsg(msgSent.getUser().toString() + " a rejoint la session");
	            	}
	            } else if (msgSent.getType().equals("command")) {
	            	if (msgSent.getMsg().equals("list") && !isUserSender(msgSent.getUser(), main.user)) {
	            		new MulticastPublisher().send(new MessageSend(msgSent.getUser(), "listReply" + main.user.toString(), "command"), ipAddr);
	            	} else if (msgSent.getMsg().length() > 9 && msgSent.getMsg().substring(0, 9).equals("listReply") && isUserSender(msgSent.getUser(), main.user)) {
	            		main.addRecivedMsg(" - " + msgSent.getMsg().substring(9) + " connecté");
	            	}
	            } else {
	            	if (isUserSender(msgSent.getUser(), main.user)) {
		            	main.addRecivedMsg(msgSent.toStringU());
		            } else {
		            	main.addRecivedMsg(msgSent.toString());
		            }
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
    
    public boolean isUserSender(User sender, User main) {
    	if (sender.verifUser().equals(main.verifUser())) {
    		return true;
    	}
    	return false;
    }
}
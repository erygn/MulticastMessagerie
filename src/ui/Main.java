package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.MulticastPublisher;
import net.MulticastReceiver;
import utils.MessageSend;
import utils.User;

public class Main extends JFrame {
	
	JList<String> messages;
	DefaultListModel<String> list = new DefaultListModel<String>();
	JScrollPane scroll;
	public User user;
	String ipAddr = "230.0.0.0";
	MulticastReceiver mR;
	Main main;
	JPanel mainP;
	JPanel beginP;
	JPanel sendP;
	int id;
	
	public Main(String title) {
		super(title);
		main = this;
		id = new Random().nextInt(2048) + 10;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(700, 600));
		setMinimumSize(new Dimension(700, 120));
		
		beginP = new JPanel();
		mainP = new JPanel(new BorderLayout());
		sendP = new JPanel();
		
		JTextField pseudoChoose = new JTextField(10);
		JTextField multicastChoose = new JTextField(15);
		JButton connectMulti = new JButton("Rejoindre");
		
		JLabel mainTitle = new JLabel(title);
		JTextField pseudo = new JTextField(6);
		pseudo.setEditable(false);
		pseudo.setEnabled(false);
		JTextField message = new JTextField(12);
		JButton send = new JButton("Envoyer");
		JButton quit = new JButton("Quitter");
		multicastChoose.setText("230.0.0.0");
		pseudoChoose.setText("User");
		beginP.add(pseudoChoose);
		beginP.add(multicastChoose);
		beginP.add(connectMulti);
		
		AbstractAction connectAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pseudoChoose.getText().length() > 0 && multicastChoose.getText().length() > 0) {
					user = new User(pseudoChoose.getText(), id);
					ipAddr = multicastChoose.getText();
					mainTitle.setText(pseudoChoose.getText());
					//pseudo.setText(pseudoChoose.getText());
					connectMulticast(multicastChoose.getText());
					remove(beginP);
					add(mainP, BorderLayout.CENTER);
					add(sendP, BorderLayout.SOUTH);
					repaint();
					validate();
					try {
						new MulticastPublisher().send(new MessageSend(user, user.toString(), "join"), ipAddr);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					addRecivedMsg("Vous avez rejoint le salon " + multicastChoose.getText());
				}
			}
		};
		multicastChoose.addActionListener(connectAction);
		connectMulti.addActionListener(connectAction);
		
		add(beginP, BorderLayout.CENTER);
		
		AbstractAction sendAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (message.getText().length() > 0) {
					if (message.getText().substring(0, 1).equals("/")) {
						if (message.getText().equals("/list")) {
							try {
								addRecivedMsg("--- Liste des utilisateurs connectés ---");
								new MulticastPublisher().send(new MessageSend(user, "list", "command"), ipAddr);
								message.setText("");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else if (message.getText().equals("/leave")) {
							leaveGroup();
						} else {
							addRecivedMsg("La commande que vous tentez d'envoyer est fausse");
						}
						message.setText("");
					} else {
						try {
							new MulticastPublisher().send(new MessageSend(user, message.getText(), "message"), ipAddr);
							message.setText("");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		};
		
		AbstractAction quitAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				leaveGroup();
			}
		};
		quit.addActionListener(quitAction);
		
		send.addActionListener(sendAction);
		message.addActionListener(sendAction);
		sendP.add(mainTitle);
		sendP.add(message);
		sendP.add(send);
		sendP.add(quit);
		
		pack();
	}
	
	public void leaveGroup() {
		try {
			new MulticastPublisher().send(new MessageSend(user, user.toString(), "leave"), ipAddr);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mR.isRunning = false;
		add(beginP, BorderLayout.CENTER);
		remove(mainP);
		remove(sendP);
		mR = null;
		main.mainP.remove(scroll);
		list = null;
		messages = null;
		scroll = null;
		repaint();
		validate();
	}
	
	public void connectMulticast(String ip) {
		mR = new MulticastReceiver(this, ip);
		list = new DefaultListModel<String>();
		messages = new JList<>(list);
		scroll = new JScrollPane(messages);
		main.mainP.add(scroll, BorderLayout.CENTER);
	}

	public static void main(String[] agrv) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Main("Messagerie").setVisible(true);
			}
		});
	}
	
	public void addRecivedMsg(String msg) {
		list.addElement(msg);
		main.repaint();
		main.validate();
	}
}

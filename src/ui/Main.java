package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;

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
	
	public Main(String title) {
		super(title);
		main = this;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(700, 600));
		setMinimumSize(new Dimension(700, 120));
		
		JPanel beginP = new JPanel();
		mainP = new JPanel(new BorderLayout());
		JPanel sendP = new JPanel();
		
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
					user = new User(pseudoChoose.getText());
					ipAddr = multicastChoose.getText();
					pseudo.setText(pseudoChoose.getText());
					connectMulticast(multicastChoose.getText());
					remove(beginP);
					add(mainP, BorderLayout.CENTER);
					add(sendP, BorderLayout.SOUTH);
					repaint();
					validate();
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
				if (pseudo.getText().length() > 0 && message.getText().length() > 0) {
					try {
						new MulticastPublisher().send(new MessageSend(user, message.getText()), ipAddr);
						message.setText("");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		};
		
		AbstractAction quitAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mR.isRunning = false;
				add(beginP, BorderLayout.CENTER);
				remove(mainP);
				remove(sendP);
				repaint();
				validate();
			}
		};
		quit.addActionListener(quitAction);
		
		send.addActionListener(sendAction);
		message.addActionListener(sendAction);
		sendP.add(pseudo);
		sendP.add(message);
		sendP.add(send);
		sendP.add(quit);
		
		pack();
	}
	
	public void connectMulticast(String ip) {
		mR = new MulticastReceiver(this, ip);
		mR.start();
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

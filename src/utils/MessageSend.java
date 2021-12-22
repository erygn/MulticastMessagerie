package utils;

import java.io.Serializable;

public class MessageSend implements Serializable {
	
	private  static  final  long serialVersionUID =  1350092881346723535L;
	
	public User user;
	String message;
	int stamp;
	
	public MessageSend(User us, String msg) {
		user = us;
		message = msg;
		stamp = 0;
	}
	
	@Override
	public String toString() {
		return user + " - " + message;
	}

	public String toStringU() {
		return user + " (vous) - " + message;
	}
}

package utils;

import java.io.Serializable;

public class MessageSend implements Serializable {
	
	private  static  final  long serialVersionUID =  1350092881346723535L;
	
	User user;
	String message;
	int stamp;
	String msgType;
	
	public MessageSend(User us, String msg, String type) {
		user = us;
		message = msg;
		stamp = 0;
		msgType = type;
	}
	
	public String getType() {
		return msgType;
	}
	
	public String getMsg() {
		return message;
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public String toString() {
		return user + " - " + message;
	}

	public String toStringU() {
		return user + " (vous) - " + message;
	}
}

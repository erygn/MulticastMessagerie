package utils;

import java.io.Serializable;
import java.util.Random;

public class User implements Serializable {
	
	private  static  final  long serialVersionUID =  1350092881346723535L;
	
	String user;
	int uid;
	
	public User(String us) {
		user = us;
		uid = new Random().nextInt(1024);
	}
	
	@Override
	public String toString() {
		return user;
	}
	
	public String verifUser() {
		return user + uid;
	}
}

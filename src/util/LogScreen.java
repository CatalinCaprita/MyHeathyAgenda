package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import spaces.Interactive;

public class LogScreen implements Interactive{
	private static LogScreen self;
	private static final String OPTIONS = "1.Sign in | 2.Register | 3.Back";
	private static final int COMMAND_SIZE = 3;
	private static String username;
	private static String password;
	private static String  email;
	private LogScreen() {
		System.out.println("First off, let us see some credentials.");
	}
	public static LogScreen call() {
		self = (self == null)? new LogScreen() : self;
		return self;
	}
	@Override
	public void showOpts() {
		System.out.println("Login Screen.\n" + OPTIONS);
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
	}
	@Override 
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ CSVLoader.recordAction(new TimeStamp("signIn"));return signIn();}
		case 2:{ CSVLoader.recordAction(new TimeStamp("registered new user"));return register();}
		default:{CSVLoader.recordAction(new TimeStamp("exit"));return -2;}
		}
	}
	public int signIn() {
		username = InputHandler.listenString("Username:");
		password = InputHandler.listenString("Password:");
		
		return checkCredentials();
	}
	public int register() {
		username = InputHandler.listenString("Please Select how you want us to call you:");
		String confirmPassword;
		boolean correct;
		do{
			password = InputHandler.listenString("Please type in your password:");
		confirmPassword = InputHandler.listenString("Confirm Password:");
		correct = password.equals(confirmPassword);
		if(!correct) {
			System.out.println("Passwords do not match!\n");
			}
		}while(!correct);
		email = InputHandler.listenString("E-Mail:");
		return addUser();
		
	}
	public static String getUsername() {
		return username;
	}
	public static String getPassword() {
		return password;
	}
	public static String getEmail() {
		return email;
	}
	private  int checkCredentials() {
		int count = 0;
		try{
			count = (int)Files.readAllLines(Paths.get("./user_info/user_credentials.csv")).stream().filter(stringInfo -> {
			String[] parts = stringInfo.split(",");
			return parts[0].equals(username) && parts[1].equals(password);}).count();
		}catch(IOException e) {
			System.out.println("Invalid Credentials");
			return -1;
		}
		if(count == 0) {
			System.out.println("Invalid Credenials.");
			return -1;
		}
		return -2;
	
	}
	private int addUser() {
		String newInfo = username + "," + password+ "," + email + "\r\n";
		BufferedWriter usersWriter = null;
			try {
				 usersWriter = new BufferedWriter(new FileWriter("./user_info/user_credentials.csv",true));
			}catch(IOException e) {
				e.printStackTrace();
			}
		try{
			usersWriter.append(newInfo);
			usersWriter.flush();
			usersWriter.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return -2;
	}
}

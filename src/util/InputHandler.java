package util;

import java.util.Scanner;

import food.NegativeMacroException;

public class InputHandler{
	private static Scanner in; 
	private static InputHandler self = new InputHandler();
	private InputHandler() {
		in = new Scanner(System.in);
	}
	public static  int listenInt(String inviteMessage,int treshold) {
		int  result = -1;
		System.out.println(inviteMessage);
		while(true) {
		try {
			result = Integer.parseInt(in.nextLine());
			if(result  < treshold)
				throw  new NegativeMacroException();
		}catch(NegativeMacroException e) {
			System.out.println(e);
		}catch(NumberFormatException e){
			System.out.println("Not an index.");
		}finally {
			if(result >= treshold)
				break;
			}
		}
		return result;
	}
	public static  double listenDouble(String inviteMessage,double treshold) {
		double result = -1;
		System.out.println(inviteMessage);
		while(true) {
		try {
			result = Double.parseDouble(in.nextLine());
			if(result < treshold)
				throw  new NegativeMacroException();
		}catch(NegativeMacroException e) {
			System.out.println(e);
		}catch(NumberFormatException e){
			System.out.println("Not an index.");
		}finally {
			if(result >= treshold)
				break;
			}
		}
		return result;
	}
	public static String listenString(String inviteMessage) {
		System.out.println(inviteMessage);
		boolean parsed = false;
		String name = "...";
		while(!parsed) {
		if(in.hasNextLine()) {
			name = in.nextLine();
			parsed = true;
		}
		else
			in.next();
		}
		return name;
	}
}

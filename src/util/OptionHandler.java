package util;

import java.util.LinkedList;
import java.util.Scanner;

import spaces.Interactive;

public class OptionHandler{
	private static OptionHandler selfRefference = new OptionHandler();
	private static LinkedList<Interactive> targetStack = new LinkedList<Interactive>();
	private static Interactive current;
	private OptionHandler() {}
	public static OptionHandler call() {
		return selfRefference;
	}
	public static void attach(Interactive newTarget) {
		if(current == null)
			current = newTarget;
		else {
		targetStack.addLast(current);
		current = newTarget;
		}
	}
	public static void detach() {
		current = targetStack.pollLast();
	}
	public static void showOpts() {
		current.showOpts();
	}
	public static int action(int actionId) {
		return current.action(actionId);
	}
	public static int listen(){
		int response = 0;
	while(true) {
		showOpts();
		Scanner in = new Scanner(System.in);
		System.out.println("Please Select One of the available commands");
		try {
				int actionId = Integer.parseInt(in.nextLine());
				if(actionId > current.size())
					throw new InvalidCommandException();
				response = action(actionId);
				if(response == -1)
					throw new ErrorOnCommandException();
			
		}catch(NumberFormatException e) {
			System.out.println(e.getMessage());			
		}catch(InvalidCommandException e) {
			System.out.println(e.getMessage());
		}catch(ErrorOnCommandException e){
			System.out.println(e.getMessage());
		}finally {
			if(response == -2)
				break;
			}
		}
	return response;
	}
	public static Interactive currentTarget() {
		return current;
	}
}

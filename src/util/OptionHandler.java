package util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import GUI.AppFrame;
import spaces.Interactive;

public class OptionHandler{
	private static OptionHandler selfRefference = new OptionHandler();
	private static LinkedList<Interactive> targetStack = new LinkedList<Interactive>();
	private static File fout = new File("./" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "LOGS.csv");
	private static InvocationHandler auditLogger = new InvocationHandler() {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			try(PrintWriter writer = new PrintWriter(new FileWriter(fout,true))){
				if(!fout.exists())
					fout.createNewFile();
				if(!("showOpts".equals(method.getName()))) {
				if("action".equalsIgnoreCase(method.getName()))
					writer.println(current.getActionName((Integer)args[0]) + " at : " + LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
				else
					writer.println(method.getName()+ " at : " + LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
				writer.flush();
				}
				Object result = null;
				try{
					result = method.invoke(current, args);
					return result;
				}catch(Exception e) {
					return null;
				}
			}catch(IOException e) {
				
			}
			return null;
		}
		
	};
	
	
	private static Interactive proxy = Interactive.class.cast(Proxy.newProxyInstance(spaces.App.class.getClassLoader(), 
			new Class<?>[]{Interactive.class},
			auditLogger));
	private static Interactive current;
	private static int buttonActionID = 0;
	private static boolean canListen = false;
	private static int actionResult = -4;
	private static ActionListener buttonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized(OptionHandler.class) {
				buttonActionID = Integer.parseInt(e.getActionCommand());
				System.out.println("Obtained access." + buttonActionID);
				/*
				actionResult = action(buttonActionID);
				if(actionResult == -2 || actionResult == -3)
					detach();*/
			}
		}
		
	};
	
	private OptionHandler() {}
	public static OptionHandler call() {
		return selfRefference;
	}
	
	
	public static void attach(Interactive newTarget) {
		if(current == null) {
			current = newTarget;
			AppFrame.getInstance(null).getOptionsPanel().addOptionsGrid(newTarget, 0);
		}
		else {
		targetStack.addLast(current);
		AppFrame.getInstance(null).getOptionsPanel().addOptionsGrid(newTarget, targetStack.size() - 1);
		current = newTarget;
		}
		AppFrame.getInstance(null).getOptionsPanel().swapOptionsGrid(targetStack.size() == 0 ? 0 : targetStack.size() - 1);
		//showOpts();
	}
	public static void detach() {
		current = targetStack.pollLast();
	}
	public static void showOpts() {
		proxy.showOpts();
	}
	public static int action(int actionId) {
		return proxy.action(actionId);
	}
	public static int listen(){
		int response = 0;
		
		while(true) {
			showOpts();
			synchronized(OptionHandler.class) {
				//if(buttonActionID > 0) {			
				try {
						buttonActionID = InputHandler.listenInt("Please select one of the available options", 0);
						if(buttonActionID > proxy.size())
							throw new InvalidCommandException();
						int dummy = buttonActionID;
						buttonActionID = 0;
						response = action(dummy);
						if(response == -1)
							throw new ErrorOnCommandException();
						
				}catch(NumberFormatException e) {
					System.out.println(e.getMessage());			
				}catch(InvalidCommandException e) {
					System.out.println(e.getMessage());
				}catch(ErrorOnCommandException e){
					System.out.println(e.getMessage());
				}finally {
					if(response <= -2)
					break;
					}
				}
				//}
			}
		return response;
		}
		
	public static Interactive currentTarget() {
		return current;
	}
	public static ActionListener getActionListener() {
		return buttonListener;
	}
}

package spaces;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.javadoc.Parameter;

import GUI.AppFrame;
import db.DBManager;
import food.Food;
import oracle.net.aso.g;
import user.User;
import util.CSVLoader;
import util.LogScreen;
import util.OptionHandler;
import util.UserFilesManager;

public class App {
	private static User loggedUser; //found by UserName
	private static Session currentSession;
	private static App self;
	private static LogScreen log;
	private static boolean loginSuccess;
	
	private  App() {
		System.out.println("Welcome to Your Health Agenda. And you are?");
	}
	public static App init() {
		self = (self == null )? new App() : self;
		return self;
	}
	public static void main (String[] args) {
		init();
		
		OptionHandler.call();
		LogScreen.call();
		String username,password; 
		CSVLoader.call();
		/*
		 try {
	            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	        } catch (UnsupportedLookAndFeelException ex) {
	            ex.printStackTrace();
	        } catch (IllegalAccessException ex) {
	            ex.printStackTrace();
	        } catch (InstantiationException ex) {
	            ex.printStackTrace();
	        } catch (ClassNotFoundException ex) {
	            ex.printStackTrace();
	        }
	        /* Turn off metal's use of bold fonts */
	        UIManager.put("swing.boldMetal", Boolean.FALSE);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AppFrame.getInstance("My Healthy Agenda");
			}
		});
		OptionHandler.attach(LogScreen.call());
		while(true){
			int response = OptionHandler.listen();
			loginSuccess = (response == -3);
			if(response == -2 || response == -3 ) {
				OptionHandler.detach();
				break;
			}
		}
		if(loginSuccess) {
			OptionHandler.attach(new Session(new User(LogScreen.getUsername(),LogScreen.getPassword(),LogScreen.getEmail())));
			while(true){
				int response = OptionHandler.listen();
				if(response < -1) {
					OptionHandler.detach();
					break;
				}
			}
		}
		else {
			System.exit(0);
			System.out.println("Have a good day, stranger.");
		}
		System.out.println("Have a good day," + LogScreen.getUsername());
		DBManager.close();
	}
	
}

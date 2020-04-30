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

import com.sun.javadoc.Parameter;

import food.Food;
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
		log = LogScreen.call();
		String username,password; 
		CSVLoader.call();
		OptionHandler.attach(log);
		while(true){
			int response = OptionHandler.listen();
			loginSuccess = (response != -1);
			if(response == -2) {
				OptionHandler.detach();
				break;
			}
		}
		if(loginSuccess) {
			loggedUser = new User(log.getUsername(),log.getPassword(),log.getEmail());
			OptionHandler.attach(new Session(loggedUser));
			while(true){
				int response = OptionHandler.listen();
				if(response == -2) {
					OptionHandler.detach();
					break;
				}
			}
		}
		System.out.println("Have a good day," + LogScreen.getUsername());
	}
	
}

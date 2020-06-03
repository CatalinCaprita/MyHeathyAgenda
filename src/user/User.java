package user;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import spaces.recipes.Recipe;
import spaces.recipes.RecipeBook;
import util.InputHandler;
import util.UserFilesManager;

public class User {
	private String username;
	private String password;
	private String email;
	private final String directoryPath;
	private  Goals personalGoals = new Goals();
	public User(String username, String password,String email) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.directoryPath = "./user_info/"+username;
		
	}
	public String getUsername() {
		return username;
	}
	public String getDirectory() {
		return this.directoryPath;
	}
	public String getRecipesDirectory() {
		return this.directoryPath + "/recipes";
	}
	public String getProfileDirectory() {
		return this.directoryPath + "/profile";
	}
	public String getDiariesDirectory() {
		return this.directoryPath + "/diaries";
	}
	public Goals getGoals() {
		return personalGoals;
	}
	public String getPassword() {
		return password;
	}
	public String getEmail() {
		return email;
	}
	public void setGoals(Goals loaded) {
		this.personalGoals = loaded;
	}
	public boolean createFiles() {
		File dump = new File(directoryPath);
		//Create user folder
		if(!dump.exists()) {
			System.out.println((dump.mkdir())? "We've Added You to Our DataBase" : "Database Registration failed");
		try{
			Files.createDirectory(Paths.get(directoryPath + "/recipes"));
			Files.createDirectory(Paths.get(directoryPath + "/diaries"));
			
			/* Files that are created if the CSV FILE MANAGEMENT system is run 
			Files.createFile(Paths.get(directoryPath + "/profile/personal.csv"));
			Files.createFile(Paths.get(directoryPath + "/foods.csv"));
			Files.createDirectory(Paths.get(directoryPath + "/profile"));
			Files.createFile(Paths.get(directoryPath + "/profile/goals.csv"));
			 */
			
			//Goals Will Be configured for certain if the user is first registring because the Files do not exist yet
			return true;
		}catch(FileAlreadyExistsException e) {
			return false;
		}catch(IOException e){
		}
		}
		return false;
		
	}
	
	
}

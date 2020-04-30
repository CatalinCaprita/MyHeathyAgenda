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
	private final Goals personalGoals = new Goals();
	public User(String username, String password,String email) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.directoryPath = "./user_info/"+username;
		createFiles();
		
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
	public Goals getGoals() {
		return personalGoals;
	}
	public String getPassword() {
		return password;
	}
	private void createFiles() {
		File dump = new File(directoryPath);
		//Create user folder
		if(!dump.exists()) {
			System.out.println((dump.mkdir())? "We've Added You to Our DataBase" : "Database Registration failed");
		try{
			Files.createDirectory(Paths.get(directoryPath + "/recipes"));
			Files.createDirectory(Paths.get(directoryPath + "/profile"));
			Files.createFile(Paths.get(directoryPath + "/profile/goals.csv"));
			Files.createFile(Paths.get(directoryPath + "/profile/personal.csv"));
			Files.createFile(Paths.get(directoryPath + "/foods.csv"));
			
			
			//Goals Will Be configured for certain if the user is first registring because the Files do not exist yet
			configureGoals();
		}catch(FileAlreadyExistsException e) {
		}catch(IOException e){
		}
		}
		
	}
	
	public void configureGoals() {
		System.out.println("Let Us Configure Your Goals. We will need some basic informations.");
		double weight = InputHandler.listenDouble("What is your current weight (kgs)?" , 0);
		int age = InputHandler.listenInt("What is your age (years)?" , 0);
		double height = InputHandler.listenDouble("What is your current height (cms)?" , 0);
		int gender = InputHandler.listenInt("What is your gender ? 1.Male | 2.Female?" , 1);
		double goalWeight = InputHandler.listenDouble("What is your goal weight (kgs)?" , 0);
		System.out.println("1.Sedentary (Spend most of the day sitting, working a desk job, little to no exercise.)\n "
				+ "2.Light activity ( Spend a Good Part Of The Day On Your Feet (Teacher, Real Estate)\n"
				+ "3.Active ( Spend A Good Part Of The Day Doing Physical Activites (Waitress, UPS Driver and exercising at most 3 times / week\n"
				+ "4.Very Active ( Spend A Good Part Of The Day Doing Heavy Physical Activites (Brick Layer, Mover) and exercising more than 3 times / week\n" );
		int activity = InputHandler.listenInt("Select your activity level" ,1);
		int TDEE,caloriesPerDay,targetActivity;
		switch(gender) {
		//Total Daily Energy Expenditure for Males
		case 1:{
			TDEE =(int)(66.47 + ( 13.75 * weight) + ( 5.003 * height) - ( 6.755 * age) * (1 + activity / 10));
			break;
		}
		//For Female
		case 2:{
			TDEE = (int)(655.1 + ( 9.563 * weight ) + ( 1.85 * height) - ( 4.676 * age ) * (1 + activity / 10));
			break;
		}
		default:
			TDEE = 2200;
		}
		
		System.out.println("Great! So, you Total daily Energy Expenditure is " + Integer.toString(TDEE) + "kcal");
		int targetAct = InputHandler.listenInt("Now we will need to know what is your Goal? 1.Lose Weight | 2.Build Muscle Mass | 3.Maintain Current Weight?" , 1);
		personalGoals.updateAll(weight,age,height,gender,activity,targetAct,goalWeight);
		System.out.println(this.personalGoals);
		UserFilesManager.attach(this);
		UserFilesManager.saveGoals();
		UserFilesManager.saveAccountInfo();
	}
	
}

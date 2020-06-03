package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import food.Food;
import spaces.DailyFoodDiary;
import spaces.recipes.Recipe;
import spaces.recipes.RecipeBook;
import user.Goals;
import user.User;

public class UserFilesManager {
	private static User proxied;
	private static UserFilesManager self;
	private UserFilesManager() {
		
	}
	public static UserFilesManager call() {
		self = self == null ? new UserFilesManager() :self;
		return self;
	}
	public static void attach(User toProxy) {
		proxied = toProxy;
	}
	public static void saveGoals() {
		CSVLoader.writeSingleEntry(proxied.getProfileDirectory() + "/goals.csv",proxied.getGoals());
	}
	public static Goals loadGoals() {
		List<Goals> allGoals = CSVLoader.read(proxied.getProfileDirectory()+ "/goals.csv", Goals.class);
		return allGoals.size() == 0 ? null : allGoals.get(allGoals.size() - 1);
	}
	public static void saveAccountInfo() {
		CSVLoader.writeSingleEntry(proxied.getProfileDirectory() + "/account.csv", proxied);
	}
	public static void saveDiary(DailyFoodDiary target) {
		try {
			File dFile = new File(proxied.getDiariesDirectory() + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".out");
			if(!dFile.exists())
				dFile.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dFile));
			out.writeObject(target);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DailyFoodDiary loadDiary() {
		try {
			File dFile = new File(proxied.getDiariesDirectory() + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".out");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(dFile));
			return (DailyFoodDiary)in.readObject();
		}catch(IOException | ClassNotFoundException e) {
			//e.printStackTrace();
			System.out.println("Starting a new Diary");
			return null;
		}
		
	}
	public static void saveRecipe(Recipe toSave) {
		String saveFile = null;
		try {
			saveFile = Files.createFile(Paths.get(proxied.getRecipesDirectory() + "/" + toSave.getName() + "_" + toSave.getServings()+ ".csv")).toString();
		}catch(FileAlreadyExistsException e) {
			System.out.println("You already have a saved recipe called" + toSave.getName());
			int answer = InputHandler.listenInt("Do you wish to OVERWRITE it ?(1.Yes | 0.No)", 0);
			if(answer == 1)
				saveFile =  proxied.getRecipesDirectory() + "/" + toSave.getName() + "_" + toSave.getServings()+ ".csv";
		}catch(IOException e) {
			System.out.println( e + ". " + e.getCause());
			saveFile = null;
		}
		if(saveFile != null) {
			CSVLoader.write(saveFile, toSave.getIngredientsList());
		}
		else
			System.out.println("Save Unsuccessful.");
	}
	public static Recipe loadRecipe(String recipeFile) {
		if(recipeFile == null) {
			printAvailableRecipes();
			String name = InputHandler.listenString("What is the Name of The Recipe?");
			recipeFile = getRecipeFile(name);
			
		}
		int servings = Integer.parseInt(recipeFile.substring(recipeFile.indexOf('_') + 1,recipeFile.indexOf('.')));
		List<Food> ingredients = CSVLoader.read(proxied.getRecipesDirectory() + "/" + recipeFile, Food.class);
		return new Recipe(recipeFile.substring(0,recipeFile.indexOf('_')),ingredients,servings);
	}

	public static RecipeBook loadRecipeBook() {
		List<Recipe> all = new ArrayList<Recipe>();
		try {
			File recDir = new File(proxied.getRecipesDirectory());
			if(recDir.isDirectory()) {
				all = Arrays.stream(recDir.list())
						.map(file ->loadRecipe(file)).collect(Collectors.toList());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new RecipeBook(all);
	}
	
	
	public static void saveFood(Food item) {
		CSVLoader.writeSingleEntry(proxied.getDirectory() + "/foods.csv",item);
	}
	public static Food loadSavedFood(String name) {
		return CSVLoader.read(proxied.getDirectory() + "/foods.csv", Food.class).
				stream().filter( food -> food.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}
	public static void printSavedFoods() {
		 CSVLoader.read(proxied.getDirectory() + "/foods.csv", Food.class).
			stream().forEach((food) -> System.out.println(food));
	}
	public static void printAvailableRecipes() {
		try {
			File dir = new File(proxied.getRecipesDirectory());
			if(dir.isDirectory()) {
				Arrays.stream(dir.list()).forEach(file -> System.out.println(file.substring(0,file.indexOf('_'))));
			}
		}catch(Exception e) {
			System.out.println( e + " : " + e.getCause());
		}
	}
	private static String getRecipeFile(String name) {
		try {
			File parentDir = new File(proxied.getRecipesDirectory());
			if(parentDir.isDirectory())
				return Arrays.stream(parentDir.list())
						.filter(fileName -> fileName.substring(0,fileName.indexOf('_')).equalsIgnoreCase(name))
						.findFirst()
						.orElse(null);
		}catch(Exception e) {
			System.out.println( e + " : " + e.getCause());
			return null;
		}
		return null;
	}
}

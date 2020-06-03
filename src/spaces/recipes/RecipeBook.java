package spaces.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JPanel;

import db.DBProxy;
import db.DBService;
import food.Carbohydrate;
import food.Fat;
import food.Food;
import food.NegativeMacroException;
import food.Protein;
import spaces.DailyFoodDiary;
import spaces.Interactive;
import user.User;
import util.CSVLoader;
import util.InputHandler;
import util.OptionHandler;
import util.TimeStamp;
import util.UserFilesManager;

public class RecipeBook implements Interactive{
	private Map<String,Recipe> recipes = new HashMap<String,Recipe>();
	private List<String> keys = new ArrayList<String>();
	private static final String OPTIONS = "1.Show Currently Saved Recipes | 2.Create Recipe | 3.Delete Recipe | 4.Edit Recipe  | 5.Add Recipe to Today's Diary  | 6.Back";
	private static final int COMMAND_SIZE = 6;
	private DailyFoodDiary logging;
	private User owner;
	private DBService recipeService = DBProxy.create("recipes");
	public RecipeBook() {}
	
	public RecipeBook(List<Recipe> r) {
		for(Recipe recipe : r) {
			recipes.putIfAbsent(recipe.getName(),recipe);
			keys.add(recipe.getName());
		}
	}
	@Override
	public void showOpts() {
		System.out.println("You are now browsing your Recipes.\n" + OPTIONS);
	}
	@Override
	public String getOpts() {
		return OPTIONS;
	}
	@Override 
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ System.out.println(this); return 0;}
		case 2:{ return addRecipe();}
		case 3:{ return removeRecipe();}
		case 4:{ return editRecipe(null,false);}
		case 5:{ return addToDiary();}
		default: return -2;
		}
	}
	@Override
	public String getActionName(int actionId) {
		switch(actionId) {
		case 1:{ return "showRecipes()";}
		case 2:{ return "addRecipe()";}
		case 3:{ return "removeRecipe()";}
		case 4:{ return "editRecipe(null)";}
		case 5:{ return "addToDiary()";}
		default: return "-2";
		}
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
	}
	@Override
	public JPanel createPanel() {
		return null;
	}
	public String toString() {
		if(recipes.isEmpty())
			return "\n No Recipes Added Yet.";
		StringBuilder str = new StringBuilder();
		for(Recipe r : recipes.values()) {
			str.append(r.toString());
			str.append("\n");
		}
		return str.toString();
	}
	public int addRecipe() {
		String name = InputHandler.listenString("What is the name of the recipe?");
		int servings = InputHandler.listenInt("How many servings does it yield?", 1);
		Recipe log = new Recipe(name,servings);
		keys.add(name);
		recipes.put(name,log);
		
		return editRecipe(name,true);
	}
	public int editRecipe(String lookup,boolean first) {
		if(lookup == null) {
			listItems();
			int key = InputHandler.listenInt("What Recipe do you wish to edit? Enter the number.", 1);
			
			lookup = keys.get(key - 1);
			
		}
		if(!recipes.containsKey(lookup)) {
			System.out.println("Could not find any recipe with that name");
			return -1;
		}
		Recipe ref = recipes.get(lookup);
		System.out.println("Before Update:" + ref);
		if(first) {
			recipeService.add(new String[] {ref.getName(), 
					owner.getUsername(),
					Double.toString(ref.getServings()),
					Double.toString(ref.getCaloriesPerServing()),
					Double.toString(ref.getMacroPerServing(0)),
					Double.toString(ref.getMacroPerServing(1)),
					Double.toString(ref.getMacroPerServing(2))
					});
		}
		OptionHandler.attach(ref);
		while(true){
			int response = OptionHandler.listen();
			if(response == -2){
				OptionHandler.detach();
				break;
			}
		}
		System.out.println("After Update:" + ref);
		int answer = InputHandler.listenInt("Do you want to save changes for this recipe in your recipe Book?(1.Yes | 0.No)", 0);
		if(answer == 1) {
			/*
			 * Method used to save the Recipe in the recipes directory
			 * UserFilesManager.saveRecipe(ref);
			 */
				recipeService.update(new String[] {ref.getName(), 
						Double.toString(ref.getServings()),
						Double.toString(ref.getCaloriesPerServing()),
						Double.toString(ref.getMacroPerServing(0)),
						Double.toString(ref.getMacroPerServing(1)),
						Double.toString(ref.getMacroPerServing(2))
						});
			}
		return 0;
	}
	public int removeRecipe() {
		listItems();
		int key = InputHandler.listenInt("What Recipe do you wish to remove? Enter the number.",1);
		if(!recipes.containsKey(keys.get(key - 1))) {
			System.out.println("Could not find any recipe with that name");
			return -1;
		}
		recipeService.delete(keys.get(key - 1));
		recipes.remove(keys.get(key - 1));
		keys.remove(key - 1);
		
		return 0;
	}
	public int addToDiary() {
		int i = 1;
		listItems();
		int key = InputHandler.listenInt("What Recipe do you wish to add to the diary? Enter the number.", 1);
		if(!recipes.containsKey(keys.get(key - 1))) {
			System.out.println("Could not find any recipe with that name");
			return -1;
		}
		Recipe target = recipes.get(keys.get(key - 1));
		System.out.println();
		double servings = InputHandler.listenDouble("How many Servings of " + keys.get(key - 1) +" to Add?", 1);
		logging.showMealNames();
		int key2 = InputHandler.listenInt("\nTo what meal?",0) - 1;
		if(logging.getMeal(key2) != null) {
			double carbs = target.getMacroPerServing(0) * servings;
			double proteins = target.getMacroPerServing(1) * servings;
			double fats = target.getMacroPerServing(2) * servings ;
			logging.getMeal(key2).addFood(new Food(keys.get(key -1),carbs,proteins,fats,false));
		}
		return 0;
		
		
	}
	public void link(DailyFoodDiary target) {
		this.logging = target;
	}
	public void setOwner(User link) {
		this.owner = link;
	}
	private void listItems() {
		System.out.println("Choose from avaiable recipes : \n");
		Integer i = 1;
		for(String key : keys) {
			System.out.println((i) + "." + key);
			i++;
		}
		
	}

}

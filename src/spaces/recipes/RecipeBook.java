package spaces.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import food.Carbohydrate;
import food.Fat;
import food.Food;
import food.NegativeMacroException;
import food.Protein;
import spaces.DailyFoodDiary;
import spaces.Interactive;
import util.InputHandler;
import util.OptionHandler;

public class RecipeBook implements Interactive{
	private Map<String,Recipe> recipes = new HashMap<String,Recipe>();
	private List<String> keys = new ArrayList<String>();
	private static final String OPTIONS = "1.Show Currently Saved Recipes | 2.Create Recipe | 3.Delete Recipe | 4.Edit Recipe  | 5.Add Recipe to Today's Diary  | 6.Back";
	private static final int COMMAND_SIZE = 6;
	private DailyFoodDiary logging;
	public RecipeBook() {}
	@Override
	public void showOpts() {
		System.out.println("You are now browsing your Recipes.\n" + OPTIONS);
	}
	@Override 
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ System.out.println(this); return 0;}
		case 2:{ return addRecipe();}
		case 3:{ return removeRecipe();}
		case 4:{ return editRecipe(null);}
		case 5:{ return addToDiary();}
		default: return -2;
		}
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
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
		
		return editRecipe(name);
	}
	public int editRecipe(String lookup) {
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
		OptionHandler.attach(ref);
		while(true){
			int response = OptionHandler.listen();
			if(response == -2){
				OptionHandler.detach();
				return 0;
			}
		}
	}
	public int removeRecipe() {
		listItems();
		int key = InputHandler.listenInt("What Recipe do you wish to remove? Enter the number.",1);
		if(!recipes.containsKey(keys.get(key - 1))) {
			System.out.println("Could not find any recipe with that name");
			return -1;
		}
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
		String targetMeal = InputHandler.listenString("To what meal?");
		if(logging.getMeal(targetMeal) != null) {
			double carbs = target.getMacroPerServing(0) * servings;
			double proteins = target.getMacroPerServing(1) * servings;
			double fats = target.getMacroPerServing(2) * servings ;
			logging.getMeal(targetMeal).addExistingFood(new Food(keys.get(key -1),carbs,proteins,fats));
		}
		return 0;
		
		
	}
	public void link(DailyFoodDiary target) {
		this.logging = target;
	}
	private void listItems() {
		System.out.println("Choose from avaiable recipes : \n");
		int i = 0;
		for(String s : recipes.keySet()) {
			System.out.println(i + "."+ s);
			i++;
		}
	}

}

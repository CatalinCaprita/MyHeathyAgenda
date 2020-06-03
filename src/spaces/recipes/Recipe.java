package spaces.recipes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import db.DBProxy;
import db.DBService;
import food.Food;
import food.MacroNutrient;
import food.Meal;
import food.NegativeMacroException;
import spaces.DailyFoodDiary;
import spaces.Interactive;
import util.CSVLoader;
import util.InputHandler;
import util.OptionHandler;
import util.TimeStamp;

public class Recipe extends Meal{
	private int servings;
	private static final int COMMAND_SIZE = 7;
	private static final String OPTIONS = "1.Add Ingredient | 2.Edit Ingredient | 3.Remove Ingredient | 4.Edit Serving Size | 5.Show Total Macronutrient Split | 6.Show Macronutrient split per serving size | 7.Back";
	private static final DecimalFormat form = new DecimalFormat("#.00");
	private final DBService recipeService = DBProxy.create("recipes");
	public Recipe(String description) {
		super(description);
	}
	public Recipe(String description, int servings) {
		super(description);
		this.servings = servings;
	}
	public Recipe() {
		super();
	}
	public Recipe(String description, List<Food> ingredients,int servings) {
		super(description);
		for(Food food : ingredients) {
			this.foods.put(food.getName(),food);
			prettyPrint.add(food.getName());
			for(int i=0 ; i < totalMacros.length; i++) {
				totalMacros[i].addQuantity(food.getMacro(i));
			}
		}
		this.servings = servings;
		this.totalCalories = computeCalories();
	}
	@Override
	public void showOpts() {
		System.out.println(this.description + ".\n" + OPTIONS);
	}
	@Override 
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ return addFood(null);}
		case 2:{ return editFood();}
		case 3:{ return removeFood();}
		case 4:{ return editServings();}
		case 5:{ System.out.println(totalSplit()); return 0;}
		case 6:{ System.out.println(servingSplit()); return 0;}
		default: return -2;
		}
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
	}
	@Override
	public String getActionName(int actionId) {
		switch(actionId) {
		case 1:{ return "addFood()";}
		case 2:{ return "removeFood()";}
		case 3:{ return "editFood()";}
		case 4:{ return " System.out.println(totalSplit())";}
		default: return "-2";
		}
	}
	
	@Override
	public int addFood(Food item) {
		int exitCode = super.addFood(item);
		if(exitCode == -1)
			return exitCode;
		item = foods.get(prettyPrint.get(prettyPrint.size() - 1)); //get last added item
		recipeService.add(new String[] {this.description,item.getName(),Double.toString(item.getQuantity())});
		return 0;
	}
	@Override
	public int removeFood() {
		listItems();
		int key = InputHandler.listenInt("\nWhat Food do you want to Remove? Press 0 to go Back.", 1);
		if(!foods.containsKey(prettyPrint.get(key - 1))) {
			System.out.println("No such food found.");
			return -1;
		}
		if( key == 0)
			return 0;
		Food removal = foods.get(prettyPrint.get(key - 1));
		for(int i=0 ; i< totalMacros.length; i++) {
			totalMacros[i].addQuantity(-removal.getMacro(i)); 
		}
		totalCalories = computeCalories();
		recipeService.delete(removal.getName());
		foods.remove(prettyPrint.get(key - 1));
		System.out.println(prettyPrint.get(key - 1) + " removed form " + this.description);
		prettyPrint.remove(key - 1);
		return 0;		
	}
	@Override
	public int editFood() {
		listItems();
		int key  = InputHandler.listenInt("\nWhat food do you want to edit? Enter the number.", 1);
		Food edit = foods.get(prettyPrint.get(key - 1));
		for(int i=0 ; i< totalMacros.length; i++) {
			totalMacros[i].addQuantity(-edit.getMacro(i)); 
		}
		updateNeeded = true;
		OptionHandler.attach(edit);
		while(true) {
			int response = OptionHandler.listen();
			if(response == -2) {
				OptionHandler.detach();
				break;
			}
		}
		for(int i=0 ; i< totalMacros.length; i++) {
			totalMacros[i].addQuantity(edit.getMacro(i)); 
		}
		totalCalories = computeCalories();
		recipeService.update(new String[] {this.description,edit.getName(),Double.toString(edit.getQuantity())});
		return 0;
	}
	public String servingSplit() {
		StringBuilder str = new StringBuilder();
		totalCalories = updateNeeded ? computeCalories()  : totalCalories;
		double servingCalories = totalCalories / servings;
		str.append("\nTotal Calories ( Per Serving ):" + form.format(servingCalories)+"\n");
		str.append("Carbohydrates : " + form.format(totalMacros[0].getQuantity() / servings) + "("+ 
					form.format(totalMacros[0].computeEnergy() / servings * 100 / servingCalories)  + "%)" + "\n");
		str.append("Proteins: " + form.format(totalMacros[1].getQuantity() / servings) + "("+ 
				form.format(totalMacros[1].computeEnergy() / servings * 100 / servingCalories)  + "%)" + "\n");
		str.append("Fats : " + form.format(totalMacros[2].getQuantity() / servings) + "("+ 
				form.format(totalMacros[2].computeEnergy() / servings * 100 / servingCalories)  + "%)" + "\n");
		return str.toString();
		
	}
	public int editServings() {
		int key = InputHandler.listenInt("How many Servings does the recipe make?",1);
		servings =  key;
		return 0;
	}
	//Brief Description of the Recipe. When in the RecipeBook, the user could opt ot see the whole list of ingredients
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.description);str.append(" yields : ");str.append(servings);
		str.append(" Per Serving : ");str.append(totalCalories);
		str.append(" | Carbohydrates : " + form.format(totalMacros[0].getQuantity())); 
		str.append(" | Proteins: " + form.format(totalMacros[1].getQuantity()));
		str.append(" | Fats : " + form.format(totalMacros[2].getQuantity()));
		return str.toString();	
	}
	public double  getMacroPerServing(int index) {
		return totalMacros[index].getQuantity() / servings;
	}
	public String getName() {
		return this.description;
	}
	public List<Food> getIngredientsList(){
		return new LinkedList<Food>(this.foods.values());
	}
	public int getServings() {
		return this.servings;
	}
	public double getCaloriesPerServing() {
		totalCalories = totalCalories == 0 ? computeCalories() : totalCalories;
		return totalCalories / servings;
	}
	public Collection<Food> getIngredients(){
		return this.foods.values();
	}
}

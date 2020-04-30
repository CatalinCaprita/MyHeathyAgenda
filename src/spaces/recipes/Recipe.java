package spaces.recipes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
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
	private static final String OPTIONS = "1.Add Ingredient | 2.Edit Ingredient | 3.Remove Ingredient | 4.Edit Serving Size | 5.Show Total Macronutrient Spli | 6.Show Macronutrient split per serving size | 7.Back";
	private static final DecimalFormat form = new DecimalFormat("#.00");
	public Recipe(String description) {
		super(description);
	}
	public Recipe(String description, int servings) {
		super(description);
		this.servings = servings;
	}
	public Recipe() {
		
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
	}
	@Override
	public void showOpts() {
		System.out.println(this.description + ".\n" + OPTIONS);
	}
	@Override 
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ CSVLoader.recordAction(new TimeStamp("Added Ingredient to Recipe"));return addFood(null);}
		case 2:{ CSVLoader.recordAction(new TimeStamp("Edited Ingredient in Recipe"));return editFood();}
		case 3:{ CSVLoader.recordAction(new TimeStamp("Removed Ingredient from Recipe"));return removeFood();}
		case 4:{ CSVLoader.recordAction(new TimeStamp("Edited Number of Servings"));return editServings();}
		case 5:{ CSVLoader.recordAction(new TimeStamp("Printed Total Nutritional Values"));System.out.println(totalSplit()); return 0;}
		case 6:{ CSVLoader.recordAction(new TimeStamp("Printed Nutritional Values per Serving"));System.out.println(servingSplit()); return 0;}
		default: return -2;
		}
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
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
		str.append(" Per Serving : | Carbohydrates : " + form.format(totalMacros[0].getQuantity())); 
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
}

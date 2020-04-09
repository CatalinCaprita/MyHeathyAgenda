package spaces.recipes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import food.Food;
import food.MacroNutrient;
import food.Meal;
import food.NegativeMacroException;
import spaces.DailyFoodDiary;
import spaces.Interactive;
import util.InputHandler;
import util.OptionHandler;

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
	@Override
	public void showOpts() {
		System.out.println(this.description + ".\n" + OPTIONS);
	}
	@Override 
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ return addFood();}
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
	//Brief Description of the Recipe. When in the RecipeBook, the user could opt ot see thh whole list of ingredients
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.description);str.append(" yields : ");str.append(servings);
		str.append("Carbohydrates : " + form.format(totalMacros[0].getQuantity())); 
		str.append("	|	Proteins: " + form.format(totalMacros[1].getQuantity()));
		str.append("	|	Fats : " + form.format(totalMacros[2].getQuantity()));
		return str.toString();	
	}
	public double  getMacroPerServing(int index) {
		return totalMacros[index].getQuantity() / servings;
	}
	
}

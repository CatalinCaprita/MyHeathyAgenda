package food;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import spaces.Interactive;
import util.CSVLoader;
import util.InputHandler;
import util.OptionHandler;
import util.TimeStamp;
import util.UserFilesManager;



/***
 * Meal Class Is composed of a various number of Foods. Each individual Food object has its own Carbohydrate/Protein/Fat split
 * Implements internally basic functionaliies for the user:
 *  - add food for a meal
 *  - remove food from a meal
 *  - see the total calories for a meal
 *  - see each amount of any of the MacroNutrients
 *
 *
 */
public class Meal implements Interactive {
	protected Map<String,Food> foods = new HashMap<String,Food>();
	protected List<String> prettyPrint = new ArrayList<String>();
	protected String description; //Could be whatever the user chooses it to name it but i will default 4 meals/day : Breakfast, Lunch, Snack, Dinner
	protected static final String[] macro_names = {"Carbohydrates","Proteins","Fats"};
	protected MacroNutrient[] totalMacros = new MacroNutrient[3];
	private static final String OPTIONS ="1.Add New Food | 2.Remove Food | 3.Edit Food | 4.Show Macronutrient Split | 5.Back";
	private static final int COMMAND_SIZE = 5; //reserved one for adding an existing Food ,as this is not part of the user input interaction cycle
	private static final DecimalFormat form = new DecimalFormat("#.00");
	protected double totalCalories = 0;
	protected boolean updateNeeded = true;
	public Meal(String description){
		this.description = description;
		totalMacros[0] = new Carbohydrate();
		totalMacros[1] = new Protein();
		totalMacros[2] = new Fat();
		updateNeeded = true; 
	}
	public Meal() {
		totalMacros[0] = new Carbohydrate();
		totalMacros[1] = new Protein();
		totalMacros[2] = new Fat();
		updateNeeded = true;
	}

	@Override
	public void showOpts() {
		System.out.println(this.description + ".\n" + OPTIONS);
	}
	@Override 
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ CSVLoader.recordAction(new TimeStamp("Added Food to Meal"));return addFood(null);}
		case 2:{ CSVLoader.recordAction(new TimeStamp("Removed Food from Meal"));return removeFood();}
		case 3:{ CSVLoader.recordAction(new TimeStamp("Edited Food from Meal"));return editFood();}
		case 4:{ CSVLoader.recordAction(new TimeStamp("Printed Total Nutritional Values"));System.out.println(totalSplit());return 0;}
		default: return -2;
		}
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
	}
	public int addFood(Food item){
		boolean fileLoaded = false;
		if(item == null) {
			int answer = InputHandler.listenInt("Do you want to load from saved foods?(1.Yes | 0.No)",0);
			if(answer == 1) {
				UserFilesManager.printSavedFoods();
				String name = InputHandler.listenString("What is the name of the food?");
				item = UserFilesManager.loadSavedFood(name);
				if(item == null) {
					System.out.println("Could not find a saved food with that name.");
					return -1;
				}
				item.editQuantity();
				fileLoaded = true;
			}
			else{
			String name = InputHandler.listenString("What is the name of the food?");
			System.out.println("Do you know the Macro-Nutrient Values for it? Check the Nutritional Value on the Back!\n Note: If a Macro-Nutrient is omitted, we will assume it has 0 grams of it!");
			double [] macroVals = new double[3];
			for(int i=0 ;i < macro_names.length; i++) {
				macroVals[i] = InputHandler.listenDouble("How many " + macro_names[i] + "? (per 100 grams )", 0);
				}
			item = new Food(name,macroVals[0],macroVals[1],macroVals[2],true);
			item.editQuantity();
			}
		}
		foods.put(item.getName(),item);
		prettyPrint.add(item.getName());
		totalMacros[0].quantity += item.carbs.quantity; 
		totalMacros[1].quantity += item.proteins.quantity; 
		totalMacros[2].quantity += item.fats.quantity; 
		if(!fileLoaded) {
			int answer = InputHandler.listenInt("Do you want to save this food for later?(1.Yes | 0.No)", 0);
			if(answer == 1) {
				UserFilesManager.saveFood(new Food(item.getName(),item.getMacroPerHundread(0),item.getMacroPerHundread(1),item.getMacroPerHundread(2),true));
			}
		}
		updateNeeded = true;
		return 0;
	}
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
		totalMacros[0].quantity -= removal.carbs.quantity; 
		totalMacros[1].quantity -= removal.proteins.quantity; 
		totalMacros[2].quantity -= removal.fats.quantity; 
		foods.remove(prettyPrint.get(key - 1));
		System.out.println(prettyPrint.get(key - 1) + " removed form " + this.description);
		prettyPrint.remove(key - 1);
		updateNeeded = true;
		return 0;		
	}
	
	public int editFood() {
		listItems();
		int key = InputHandler.listenInt("\nWhat food do you want to edit? Enter the number.", 1);
		Food editTarget= foods.get(prettyPrint.get(key - 1));
		totalMacros[0].quantity -= editTarget.carbs.quantity;
		totalMacros[1].quantity -= editTarget.proteins.quantity;
		totalMacros[2].quantity -= editTarget.fats.quantity;
		updateNeeded = true;
		OptionHandler.attach(editTarget);
		while(true) {
			int response = OptionHandler.listen();
			if(response == -2) {
				OptionHandler.detach();
				break;
			}
		}
		totalMacros[0].quantity += editTarget.carbs.quantity;
		totalMacros[1].quantity += editTarget.proteins.quantity;
		totalMacros[2].quantity += editTarget.fats.quantity;
		return 0;
	}
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.description);str.append(":\n");
		if(prettyPrint.isEmpty()) {
			str.append("No item Added Yet.");
			return str.toString();
		}
		for(String foodName : foods.keySet()) {
			str.append(foods.get(foodName).toString());
		}
		str.append(totalSplit());
		return str.toString();
	}
	public String totalSplit() {
		StringBuilder str = new StringBuilder();
		
		totalCalories = updateNeeded ? computeCalories() : totalCalories;
		updateNeeded = true;
		str.append("\nTotal Calories:" + form.format(totalCalories)+"\n");
		str.append("Carbohydrates : " + form.format(totalMacros[0].quantity) + "("+ 
					form.format(totalMacros[0].computeEnergy() / totalCalories * 100)  + "%)" + "\n");
		str.append("Proteins: " + form.format(totalMacros[1].quantity) + "("+ 
				form.format(totalMacros[1].computeEnergy() / totalCalories * 100)  + "%)" + "\n");
		str.append("Fats : " + form.format(totalMacros[2].quantity) + "("+ 
				form.format(totalMacros[2].computeEnergy() / totalCalories * 100)  + "%)" + "\n");
		return str.toString();
	}
	protected double computeCalories() {
		double total = 0;
		for(MacroNutrient m : totalMacros) {
			total += m.computeEnergy();
		}
		return total;
	}
	public double getTotalMacro(int index) {
		return totalMacros[index].quantity;
	}
	public void addExistingFood(Food addition) {
		totalMacros[0].quantity += addition.carbs.quantity;
		totalMacros[1].quantity += addition.proteins.quantity;
		totalMacros[2].quantity += addition.fats.quantity;
		foods.put(addition.getName(), addition);
		prettyPrint.add(addition.getName());
	}
	private void listItems() {
		System.out.println("Foods in " + this.description + ":");
		int i = 1;
		for(String foodName : prettyPrint) {
			System.out.print(i + "." + foodName +" ");
			i++;
		}
	}
	
}

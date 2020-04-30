package spaces;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import food.*;
import spaces.recipes.RecipeBook;
import util.CSVLoader;
import util.ErrorOnCommandException;
import util.InputHandler;
import util.InvalidCommandException;
import util.OptionHandler;
import util.TimeStamp;
public class DailyFoodDiary implements Interactive{
	private Map<String,Meal> meals = new HashMap<String,Meal>();
	private List<String> personalNotes = new ArrayList<String>();
	private List<String>mealNames = new ArrayList<String>();
	private LocalDateTime logTime = LocalDateTime.now();
	private StringBuilder logInfo = new StringBuilder();
	private static final String OPTIONS = "1.Show total Macronutrient Split | "+ 
										"2.Show Meals | 3.Edit a Meal | 4.Add Personal Notes | "+
										" | 5.Show Notes | 6.Back";
	private static final int COMMAND_SIZE = 6;
	private double totalCalories = 0;
	private boolean updateNeeded = true;
	private MacroNutrient[] totalMacros = new MacroNutrient[3];
	private RecipeBook link;
	public DailyFoodDiary() {
		logInfo.append("Diary from :");
		logInfo.append(logTime);
		meals.put("Breakfast",new Meal("Breakfast"));
		meals.put("Lunch",new Meal("Lunch"));
		meals.put("Dinner",new Meal("Dinner"));
		meals.put("Snack",new Meal("Snack"));
		totalMacros[0] = new Carbohydrate();
		totalMacros[1] = new Protein();
		totalMacros[2] = new Fat();
	}
	@Override
	public void showOpts() {
		System.out.println("Editing diary from " + logInfo.toString() + "\n" +OPTIONS);
	}
	@Override
	public int action(int actionId) {
		switch(actionId) {
		case 1:{CSVLoader.recordAction(new TimeStamp("Printed Total Calories in Food Diary"));showMacroSplit(); return 1;}
		case 2:{CSVLoader.recordAction(new TimeStamp("Printed All Meals from current Diary"));System.out.println(this.toString()); return 1;}
		case 3:{CSVLoader.recordAction(new TimeStamp("Entered Editing Meal"));return editMeal(null);}
		case 4:{CSVLoader.recordAction(new TimeStamp("Added Note to Personal Notes"));return addNote();}
		case 5:{CSVLoader.recordAction(new TimeStamp("Printed Personal Notes"));return showNotes();}
		default : return -2;
		}
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
	}
	public int  editMeal(String lookup) {
		if(lookup ==  null) {
			showMealNames();
		int key = InputHandler.listenInt("Please select the meal that you wish to edit", 0) - 1;
		int i = 0;
		for(String name : meals.keySet()) {
			if( i == key) {
				lookup = name;
				break;
				}
			i++;
			}
		}
		if(!meals.containsKey(lookup)) {
			System.out.println("No '" + lookup + "' found in the diary.");
			return -1;
			}
		//Meal Found, Moving deeper into the option hierarchy;
		OptionHandler.attach(meals.get(lookup));
		while(true) {
			int response = OptionHandler.listen();
			if(response == -2) {
				updateNeeded = true;
				OptionHandler.detach();
				return 0;
			}
			else
				updateNeeded = true;
			}
		}
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(logInfo); str.append("\n");
		for(String meal : meals.keySet()) {
			str.append(meals.get(meal).toString());
			str.append("\n------------------------\n");
		}
		return str.toString();
	}
	public double computeCalories() {
		double total = 0;
		for(String meal : meals.keySet()) {
			for(int i = 0; i <3; i++)
				totalMacros[i].addQuantity(meals.get(meal).getTotalMacro(i));
		}
		for(MacroNutrient macro : totalMacros) {
			total += macro.computeEnergy();
		}
		return total;
	}
	public void showMacroSplit() {
		totalCalories = (updateNeeded == true )? computeCalories() : totalCalories;
		StringBuilder str = new StringBuilder();
		DecimalFormat form = new DecimalFormat("#.00");
		totalCalories = computeCalories();
		str.append("\nTotal Calories:" + form.format(totalCalories)+"\n");
		str.append("Carbohydrates : " + form.format(totalMacros[0].getQuantity()) + "("+ 
					form.format(totalMacros[0].computeEnergy()/ totalCalories * 100)  + "%)" + "\n");
		str.append("Proteins: " + form.format(totalMacros[1].getQuantity()) + "("+ 
				form.format(totalMacros[1].computeEnergy() / totalCalories * 100)  + "%)" + "\n");
		str.append("Fats : " + form.format(totalMacros[2].getQuantity()) + "("+ 
				form.format(totalMacros[2].computeEnergy()/ totalCalories * 100)  + "%)" + "\n");
		System.out.println(str.toString());
	}
	public int addNote() {
		System.out.println("What note do you wish to add?: ");
		Scanner in = new Scanner(System.in);
		try {
			String message = in.nextLine();
			personalNotes.add(message);
			return 0;
		}catch(NoSuchElementException e) {
			return -1;
		}
	}
	public int showNotes() {
		System.out.println(logInfo.toString() + "\nNotes: ");
		for(String note : personalNotes) {
			System.out.println("-" + note);
		}
		return 0;
	}
	public void link(RecipeBook connection) {
		this.link = connection;
	}
	public RecipeBook accessRecipes() {
		return link;
	}
	public Meal getMeal(int key) {
		int i = 0;
		String lookup = null;
		for(String name : meals.keySet()) {
			if( i == key) {
				lookup = name;
				break;
				}
			i++;
		}
		if(!meals.containsKey(lookup))
			return null;
		return meals.get(lookup);
	}
	public void showMealNames() {
		int i = 1;
		for(String name : meals.keySet()) {
			System.out.print(i + "."+ name + " ");
			i++;
		}
	}
}

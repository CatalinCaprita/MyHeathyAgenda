package food;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.swing.JPanel;

import spaces.Interactive;
import util.CSVLoader;
import util.InputHandler;
import util.TimeStamp;

//For the moment, only working in grams
//Using Composition, as Every type of Food HAS-An amount of C/P/F and no  Macronutrient can exist on its own
public class Food implements Interactive,Serializable{
	private double quantity;
	private static final String OPTIONS = "1.Change Macro Distribution | 2.Change Qauntity | 3.Back";
	private static final int COMMAND_SIZE = 3;
	 //Delegate objects of each type used to compute the total calories and the C/F/P split
	Carbohydrate carbs;
	Protein proteins;
	Fat fats;
	private String name = "No Name Provided";
	public Food() {
		
	}
	public Food(String name,double quantity) {
		this.name = name;
		this.quantity = quantity;
	}
	public Food(String name,double carbs, double proteins, double fats,boolean perHundreadValues) {
		this.name  = name;
		this.carbs = new Carbohydrate(carbs);
		this.proteins = new Protein(proteins);
		this.fats = new Fat(fats);
		quantity = perHundreadValues ? 100 : this.carbs.quantity + this.proteins.quantity + this.fats.quantity;
	}
	@Override
	public int action(int actionId) {
		switch(actionId) {
		case 1:{ return editMacro();}
		case 2:{ return editQuantity(-1);}
		default: return -2;
		}
	}
	@Override 
	public String getActionName(int actionId) {
		switch(actionId) {
		case 1:{ return "editMacro()";}
		case 2:{ return "editQuantity()";}
		default: return "-2";
		}
	}
	@Override
	public void showOpts() {
		System.out.println("What to do with " + name + "?\n" + OPTIONS);
	}
	@Override
	public String getOpts() {
		return OPTIONS;
	}
	@Override
	public int size() {
		return COMMAND_SIZE;
	}
	public int editMacro() {
		int target =  InputHandler.listenInt("What Macronutrient to edit? 1.Carbohydrates | 2.Proteins | 3.Fats.", 1);
		double amount = InputHandler.listenDouble("What is the amount for the food you selected? (per 100 grams)", 0);
		setMacro(target - 1,amount);
		return 0;
	}
	public int editQuantity(double newQuantity) {
		if(newQuantity < 0)
			newQuantity = InputHandler.listenDouble("How many grams of this food do you want to log?",0);
		carbs.quantity = carbs.quantity * newQuantity / quantity;
		proteins.quantity = proteins.quantity * newQuantity / quantity;
		fats.quantity = fats.quantity * newQuantity / quantity;
		quantity = newQuantity;
		return 0;
	}
	public void setMacro(int type,double amountPerHundread) {
		switch(type) {
		case 0:{
			carbs = new Carbohydrate(amountPerHundread * (quantity/ 100));
			break;
		}
		case 1:{
			proteins = new Protein(amountPerHundread * (quantity/ 100));
			break;
		}
		case 2:{
			fats = new Fat(amountPerHundread * (quantity/ 100));
			break;
		}
		
		}
	}
	public double getMacroPerHundread(int type) {
		switch(type) {
		case 0:{
			return carbs.quantity * 100 / quantity;
		}
		case 1:{
			return proteins.quantity * 100 / quantity;
		}
		case 2:{
			return fats.quantity * 100 / quantity;
		}
		default:
			return 0.0;
		}
	}
	public double getMacro(int type) {
		switch(type) {
		case 0:{
			return carbs.quantity;
		}
		case 1:{
			return proteins.quantity;
		}
		case 2:{
			return fats.quantity;
		}
		default:
			return 0.0;
		}
	}
	public double totalCalories() {
		return carbs.computeEnergy() + proteins.computeEnergy() + fats.computeEnergy();
	}
	public String toString() {
		StringBuilder str = new StringBuilder();
		DecimalFormat form = new DecimalFormat("#.00");
		str.append(name);
		str.append("\nTotal Calories:" + form.format(totalCalories())+" | ");
		str.append("Carbs : " + form.format(carbs.quantity) + " | ");
		str.append("Fats : " + form.format(fats.quantity)+ " | ");
		str.append("Protein : " + form.format(proteins.quantity) + "\n");
		return str.toString();
	}
	public String getName() {
		return this.name;
	}
	public double getQuantity() {
		return this.quantity;
	}
	@Override
	public JPanel createPanel() {
		// TODO Auto-generated method stub
		return null;
	}
}

package user;

import food.Carbohydrate;
import food.Fat;
import food.MacroNutrient;
import food.Protein;

public class Goals {
	private Carbohydrate carbs;
	private Protein proteins;
	private Fat fats;
	private int TDEE;
	private int caloriesPerDay;
	private int activityGoal;
	private double targetWeight;
	private int workoutsPerWeek = 4;
	private int minutesPerWorkout = 60;
	private double weight;
	private int age;
	private double height;
	private int gender;
	private int activLevel;
	private int activity;

	public Goals() {
		
	}
	public void updateAll(double weight,int age,double height,int gender,int activityLevel, int activityGoal,double targetWeight) {
		this.weight = weight;
		this.height = height;
		this.age =age;
		this.gender = gender;
		this.activity = activityLevel;
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
		this.activityGoal = activityGoal;
		this.targetWeight = targetWeight;
		
		switch(activityGoal) {
		case 1: {
			caloriesPerDay = (int)(TDEE * 0.8);
			break;
		}
		case 2: {
			caloriesPerDay = (int)(TDEE * 1.2);
			break;
		}
		default: {
			caloriesPerDay = (int)(TDEE);
			break;
		}
		
		}
		carbs = new Carbohydrate( 45 / 100 * caloriesPerDay  / 4);
		proteins = new Protein(45 / 100 * caloriesPerDay  / 4);
		fats = new Fat(45 / 100 * caloriesPerDay  / 9);
		
	}
	public void updateAll(Goals that) {
		if(that != null) {
		 this.carbs = that.carbs;
		 this.proteins = that.proteins;
		 this.fats = that.fats;
		 this.TDEE = that.TDEE;
		 this.caloriesPerDay = that.caloriesPerDay;
		 this.activityGoal = that.activityGoal;
		 double targetWeight = that.targetWeight;
		 this.workoutsPerWeek = that.workoutsPerWeek;
		 this.minutesPerWorkout = that.minutesPerWorkout;
		 this.weight = that.weight;
		 this.age = that.age;
		 this.height = that.height;
		 this.gender = that.gender;
		 this.activLevel = that.activLevel;
		 this.activity = that.activity;
		}
	}
	public String showWorkoutGoals() {
		return workoutsPerWeek + " workouts per week where each workout lasts at least " + minutesPerWorkout + " minutes";
	}
	public String showNutritionGoals() {
		StringBuilder str = new StringBuilder();
		str.append("Current Macronutrient Goals for a day: \n");
		str.append(carbs.getQuantity());str.append("g Carbohydrates | ");
		str.append(proteins.getQuantity());str.append("g Proteins | ");
		str.append(fats.getQuantity());str.append("g Fats\n");
		return str.toString();
	}
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Calories per day: ");
		str.append(this.caloriesPerDay);str.append('\n');
		str.append("Height: " + this.height+ "\n");
		str.append("Weight: " + this.weight+ "\n");
		str.append("Age: " + this.age+ "\n");
		str.append("Activity Level: " + this.activity+ "\n");
		str.append("Goal Weight: " + this.targetWeight+ "\n");
		str.append(showNutritionGoals());
		str.append("\n");
		str.append(showWorkoutGoals());
		return str.toString();
		
	}
}

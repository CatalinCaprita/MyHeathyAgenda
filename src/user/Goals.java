package user;

import java.sql.ResultSet;
import java.sql.SQLException;

import food.Carbohydrate;
import food.Fat;
import food.MacroNutrient;
import food.Protein;
import util.InputHandler;

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
	public Goals(String[] qRes) {
		this.height = Double.parseDouble(qRes[0]);
		this.age = Integer.parseInt(qRes[1]);
		this.gender = qRes[2].equalsIgnoreCase("male") ? 1 : 2;
		this.activLevel = Integer.parseInt(qRes[3]);
		this.targetWeight = Double.parseDouble(qRes[4]);
		this.workoutsPerWeek = Integer.parseInt(qRes[5]);
		this.minutesPerWorkout= Integer.parseInt(qRes[6]);
		this.activityGoal = Integer.parseInt(qRes[7]);
		this.caloriesPerDay = Integer.parseInt(qRes[8]);
		this.carbs = new Carbohydrate(Double.parseDouble(qRes[9]));
		this.proteins = new Protein(Double.parseDouble(qRes[10]));
		this.fats= new Fat(Double.parseDouble(qRes[11]));
		this.weight = Double.parseDouble(qRes[12]);
	
	}
	public void updateAll(double weight,int age,double height,int gender,int activityLevel, int activityGoal,double targetWeight) {
		this.weight = weight;
		this.height = height;
		this.age =age;
		this.gender = gender;
		this.activLevel = activityLevel;
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
		carbs = new Carbohydrate((double)((45 / 100.00) * caloriesPerDay / 4.00));
		proteins = new Protein((double)((25 / 100.00)* caloriesPerDay  / 4.00));
		fats = new Fat((double)((30 / 100.00) * caloriesPerDay  / 9.00));
		
	}
	public void updateIntarnal(int modTarget, String newValue) {
		/**
		 * The method assumes that some field in the caller Goals object has updated a field independently
		 */
		switch(modTarget) {
		case 1: { this.height = Double.parseDouble(newValue);break;}
		case 2: { this.weight = Double.parseDouble(newValue);break;}
		case 3: { this.age = Integer.parseInt(newValue);break;}
		case 5: { this.activLevel = Integer.parseInt(newValue);break;}
		case 6: { this.targetWeight = Double.parseDouble(newValue);break;}
		case 7: { this.workoutsPerWeek = Integer.parseInt(newValue);break;}
		case 8: { this.minutesPerWorkout = Integer.parseInt(newValue);break;}
		default: return ;
		}
		switch(gender) {
		case 1:{
			TDEE =(int)(66.47 + ( 13.75 * weight) + ( 5.003 * height) - ( 6.755 * age) * (1 + activLevel / 10));
			break;
		}
		//For Female
		case 2:{
			TDEE = (int)(655.1 + ( 9.563 * weight ) + ( 1.85 * height) - ( 4.676 * age ) * (1 + activLevel / 10));
			break;
		}
		default:
			TDEE = 2200;
		}
		
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
		carbs = new Carbohydrate((double)((45 / 100.00) * caloriesPerDay / 4.00));
		proteins = new Protein((double)((25 / 100.00)* caloriesPerDay  / 4.00));
		fats = new Fat((double)((30 / 100.00) * caloriesPerDay  / 9.00));
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
		return workoutsPerWeek + " workouts per week\n8." + minutesPerWorkout + " minutes per workout\n";
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
		str.append("Your general information: \n");
		str.append("1.Height: " + this.height+ "(cms)\n");
		str.append("2.Weight: " + this.weight+ "(kgs)\n");
		str.append("3.Age: " + this.age+ "(years)\n");
		str.append("4.Gender: " + this.gender+ "\n");
		str.append("5.Activity Level: " + this.activLevel+ "\n");
		str.append("6.Goal Weight: " + this.targetWeight+ "\n7.");
		str.append(showWorkoutGoals());
		str.append("Your Goals based on your information:\nCalories per day: ");
		str.append(this.caloriesPerDay);str.append('\n');
		str.append(showNutritionGoals());
		str.append("\n");
		
		return str.toString();	
	}
	public String[] exportAll(String username, String email) {
		String[] db = new String[15];
		db[0] = username;
		db[1] = email;
		 db[2] = Double.toString(this.height);
		 db[3] = Integer.toString(this.age);
		 db[4] = this.gender == 1 ? "Male" : "Female";
		 db[5] = Integer.toString(this.activLevel);
		 db[6] = Double.toString(this.targetWeight);
		 db[7] = Double.toString(this.workoutsPerWeek);
		 db[8] = Double.toString(this.minutesPerWorkout);
		 db[9] = Integer.toString(this.activityGoal);
		 db[10] = Integer.toString(this.caloriesPerDay);
		 db[11] = this.carbs.toString();
		 db[12] = this.proteins.toString();
		 db[13] = this.fats.toString();
		 db[14] = Double.toString(this.weight);
		 return db;
	}
	public String[] exportCaloriesAndMacros(String username,String email) {
		String[] db = new String[10];
		db[0] = username;
		db[1] = email;
		db[2] = "CALORIES_PER_DAY";
		db[3] = Integer.toString(this.caloriesPerDay);
		db[4] = "CARBS_PER_DAY";
		db[5] = this.carbs.toString();
		db[6] = "PROT_PER_DAY";
		db[7] = this.proteins.toString();
		db[8] = "FATS_PER_DAY";
		db[9] = this.fats.toString();
		return db;
	}
}

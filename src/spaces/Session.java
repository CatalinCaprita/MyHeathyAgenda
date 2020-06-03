package spaces;

import javax.swing.JPanel;

import db.DBProxy;
import db.DBService;
import spaces.recipes.Recipe;
import spaces.recipes.RecipeBook;
import user.Goals;
import user.User;
import util.CSVLoader;
import util.ErrorOnCommandException;
import util.InputHandler;
import util.InvalidCommandException;
import util.OptionHandler;
import util.TimeStamp;
import util.UserFilesManager;
/***
 * The session class is responsible for managing user experience once the a certain User has been authenticated within the archives
 * @author Nicusor-Catalin Caprita
 *
 */

public class Session implements Interactive{
	private static final String OPTIONS = "1.Take a look at today's diary | 2.Browse Recipes | 3.View Personal Goals and Stats  | 4.Update Goals & Stats | 5.Remove Account | 6.Exit";
	private static final int SIZE = 6;
	private DailyFoodDiary today ;
	private RecipeBook recipes;
	private User user;
	private static DBService userService = DBProxy.create("user");
	private static DBService goalsService = DBProxy.create("goals");
	private static DBService recipesService = DBProxy.create("recipes");
	public Session(User user) {
		System.out.println("Hello," + user.getUsername());
		this.user = user;
		UserFilesManager.call();
		UserFilesManager.attach(user);
		today = UserFilesManager.loadDiary();
		if(today == null)
			today = new DailyFoodDiary(true);
		recipes = new RecipeBook(recipesService.readMulti(user.getUsername(),Recipe.class));
		recipes.setOwner(this.user);
		today.link(recipes);
		
		if(user.createFiles())
			configureGoals();
		else
			user.setGoals((Goals)goalsService.readOnce(new String[] {user.getUsername()}));
		recipes.link(today);
	}
	public DailyFoodDiary accessDiary() {
		return today;
	}
	@Override
	public void showOpts() {
		System.out.println(OPTIONS);
	}
	@Override
	public String getOpts() {
		return OPTIONS;
	}
	@Override
	public int action(int actionId) {
		switch(actionId){
		case 1:{return editDiary();}
		case 2:{return editRecipeBook();}
		case 3:{System.out.println(user.getGoals()); return 0;}
		case 4:{return updateInfo();}
		case 5:{return removeAccount();}
		default :{
				UserFilesManager.saveDiary(today);return -2;}
		}
	}
	@Override
	public String getActionName(int actionId) {
		switch(actionId){
		case 1:{return "editDiary()";}
		case 2:{return "editRecipeBook()";}
		case 3:{return "System.out.println(user.getGoals())";}
		case 4:{return "user.configureGoals()"; }
		case 5:{return "removing account";}
		default :{
				return "UserFilesManager.saveDiary(today)";}
		}
	}
	@Override
	public int size() {
		return SIZE;
	}
	@Override
	public JPanel createPanel() {
		return null;
	}
	public int editDiary() {
		OptionHandler.attach(today);
		while(true) {
			int response = OptionHandler.listen();
			if(response == -2) {
				OptionHandler.detach();
				return 0;
			}
		}
		
	}
	public int editRecipeBook() {
		OptionHandler.attach(recipes);
		while(true) {
			int response = OptionHandler.listen();
			if(response == -2) {
				OptionHandler.detach();
				return 0;
			}
		}
	}
	private int updateInfo(){
		System.out.println(user.getGoals());
		System.out.println("Your account information:");
		System.out.println("9." + user.getUsername());
		System.out.println("10." + user.getEmail());
		
		int modTarget = InputHandler.listenInt("What info do you wish to update?(Enter the number).",0);
		String [] oldNewUser = new String[3];
		oldNewUser[2] = user.getUsername();
		switch(modTarget) {
		case 1: { oldNewUser[0] = "HEIGHT"; oldNewUser[1] = Integer.toString(InputHandler.listenInt("Please enter the new value: " ,100));break;}
		case 2: { oldNewUser[0] = "WEIGHT"; oldNewUser[1] = Double.toString(InputHandler.listenDouble("Please enter the new value: " ,50));break;}
		case 3: { oldNewUser[0] = "AGE"; oldNewUser[1] = Integer.toString(InputHandler.listenInt("Please enter the new value: " ,13));break;}
		case 5: { oldNewUser[0] = "ACTIVITY_ID"; oldNewUser[1] = Integer.toString(InputHandler.listenInt("Please enter the new value: " ,0));break;}
		case 6: { oldNewUser[0] = "GOAL_WEIGHT"; oldNewUser[1] = Integer.toString(InputHandler.listenInt("Please enter the new value: " ,40));break;}
		case 7: { oldNewUser[0] = "WO_PER_WEEK"; oldNewUser[1] = Integer.toString(InputHandler.listenInt("Please enter the new value: " ,0));break;}
		case 8: { oldNewUser[0] = "MIN_PER_WO"; oldNewUser[1] = Integer.toString(InputHandler.listenInt("Please enter the new value: " ,0));break;}
		case 9: { oldNewUser[0] = "USERNAME"; oldNewUser[1] = InputHandler.listenString("How Should we call you?");break;}
		case 10: { oldNewUser[0] = "EMAIL"; oldNewUser[1] = InputHandler.listenString("What is the new email address ?");break;}
		default: return -1;
		}
		goalsService.update(oldNewUser);
		System.out.println("Your Information Has been Updated. Recalculating calories...");
		user.getGoals().updateIntarnal(modTarget, oldNewUser[1]);
		goalsService.update(user.getGoals().exportCaloriesAndMacros(user.getUsername(),user.getEmail()));
		System.out.println(user.getGoals());
		return 0;
		
	}
	private int removeAccount() {
		int answer = InputHandler.listenInt("Are you sure you want to delete your account" + user.getUsername() + " ? 1.Yes 0.No", -1);
		if(answer == 1) {
			userService.delete(user.getUsername());
			return 0;
		}
		return 0;
	}
	public void configureGoals() {
		System.out.println("Let Us Configure Your Goals. We will need some basic informations.");
		
		double weight = InputHandler.listenDouble("What is your current weight (kgs)?" , 0);
		int age = InputHandler.listenInt("What is your age (years)?" , 0);
		
		double height = InputHandler.listenDouble("What is your current height (cms)?" , 100);
	
		int gender = InputHandler.listenInt("What is your gender ? 1.Male | 2.Female?" , 1);
		
		double goalWeight = InputHandler.listenDouble("What is your goal weight (kgs)?" , 0);
		System.out.println("1.Sedentary (Spend most of the day sitting, working a desk job, little to no exercise.)\n"
				+ "2.Light activity ( Spend a Good Part Of The Day On Your Feet (Teacher, Real Estate)\n"
				+ "3.Active ( Spend A Good Part Of The Day Doing Physical Activites (Waitress, UPS Driver and exercising at most 3 times / week\n"
				+ "4.Very Active ( Spend A Good Part Of The Day Doing Heavy Physical Activites (Brick Layer, Mover) and exercising more than 3 times / week\n" );
		int activity = InputHandler.listenInt("Select your activity level" ,1);
		int TDEE,caloriesPerDay,targetActivity;
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
		
		System.out.println("Great! So, you Total daily Energy Expenditure is " + Integer.toString(TDEE) + "kcal");
		int targetAct = InputHandler.listenInt("Now we will need to know what is your Goal? 1.Lose Weight | 2.Build Muscle Mass | 3.Maintain Current Weight?" , 1);
		user.getGoals().updateAll(weight,age,height,gender,activity,targetAct,goalWeight);
		System.out.println(user.getGoals());
		goalsService.add(user.getGoals().exportAll(user.getUsername(),user.getEmail()));
		
		
		/*
		 Methods used for saving locally via CSV files
		UserFilesManager.call();
		UserFilesManager.attach(user);
		UserFilesManager.saveGoals();
		*/
	}
	
	
}

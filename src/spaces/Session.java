package spaces;

import spaces.recipes.RecipeBook;
import util.ErrorOnCommandException;
import util.InvalidCommandException;
import util.OptionHandler;
/***
 * For this stage , the Session object represents the entire user experience,as a FoodDiary and a RecipeBook are created on startup
 * @author Nicusor-Catalin Caprita
 *
 */

public class Session implements Interactive{
	private static final String OPTIONS = "1.Take a look at today's diary | 2.Browse Recipes | 3.Exit";
	private static final int SIZE = 3;
	private static DailyFoodDiary today = new DailyFoodDiary();
	private static RecipeBook recipes = new RecipeBook() ;
	public Session() {
		today.link(recipes);
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
	public int action(int actionId) {
		switch(actionId){
		case 1:{return editDiary();}
		case 2:{return editRecipeBook();}
		default : return -2;
		}
	}
	@Override
	public int size() {
		return SIZE;
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
	public static void main(String[] args) {
		System.out.println("Hello Again.");
		OptionHandler.call();
		Session sess = new Session();
		OptionHandler.attach(sess);
		while(true){
			int response = OptionHandler.listen();
			if(response == -2)
				break;
		}
		System.out.println("Have a food day!");
	}
	
}

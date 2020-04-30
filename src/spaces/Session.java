package spaces;

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
	private static final String OPTIONS = "1.Take a look at today's diary | 2.Browse Recipes | 3.View Personal Goals and Stats  | 4.Update Goals & Stats | 5.Exit";
	private static final int SIZE = 5;
	private DailyFoodDiary today = new DailyFoodDiary();
	private RecipeBook recipes;
	private User user;
	public Session(User user) {
		today.link(recipes);
		System.out.println("Hello," + user.getUsername());
		this.user = user;
		UserFilesManager.call();
		UserFilesManager.attach(user);
		recipes = UserFilesManager.loadRecipeBook();
		user.getGoals().updateAll(UserFilesManager.loadGoals());
		
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
		case 1:{CSVLoader.recordAction(new TimeStamp("edited Diary"));return editDiary();}
		case 2:{CSVLoader.recordAction(new TimeStamp("editRecipeBook"));return editRecipeBook();}
		case 3:{CSVLoader.recordAction(new TimeStamp("show personal goals"));System.out.println(user.getGoals()); return 0;}
		case 4:{CSVLoader.recordAction(new TimeStamp("updated user goals"));user.configureGoals(); return 0;}
		default :{CSVLoader.recordAction(new TimeStamp("exit app"));return -2;}
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

	
	
}

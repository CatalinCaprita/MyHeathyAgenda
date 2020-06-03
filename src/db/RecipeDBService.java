package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import food.Food;
import spaces.recipes.Recipe;

public class RecipeDBService implements DBService {
	private PreparedStatement addRecipe;
	private PreparedStatement getOwnedRecipes;
	private PreparedStatement updateRecipe;
	private PreparedStatement updateIngredient;
	private PreparedStatement getIngredients;
	private PreparedStatement addIngredient;
	private PreparedStatement getFood;
	private PreparedStatement removeRecipe;
	private PreparedStatement removeIngredient;	
	private Connection con;
	public RecipeDBService() {
		try {
			con = DBManager.getConnection();
			ResultSet rs = con.getMetaData().getTables(null, null, "RECIPES_CCN", new String[] {"TABLE"});
			if(rs.next() == false) {
				con.createStatement().execute("CREATE TABLE RECIPES_CCN(\r\n" + 
						"						RECIPE_NAME VARCHAR2(20) CONSTRAINT REC_NAME_PK PRIMARY KEY,\r\n" + 
						"						OWNER VARCHAR2(20) Constraint REC_OWNER_NN not null,\r\n" + 
						"						SERVINGS NUMBER(3,0) CONSTRAINT REC_SERV_POSITIVE CHECK(SERVINGS > 0),\r\n" + 
						"						CALORIES_PER_SERVING NUMBER(4,0) CONSTRAINT REC_CALS_NN NOT NULL,\r\n" + 
						"						CARBS_PER_SERVING NUMBER(4,0) CONSTRAINT REC_CARBS_NN NOT NULL,\r\n" + 
						"						PROTEIN_PER_SERVING NUMBER(4,0) CONSTRAINT REC_PROT_NN NOT NULL,\r\n" + 
						"						FATS_PER_SERVING NUMBER(4,0) CONSTRAINT REC_FAT_NN NOT NULL)");
				con.createStatement().execute("ALTER TABLE RECIPES_CCN\r\n" + 
						"add CONSTRAINT REC_OWNER_FK FOREIGN KEY(OWNER) REFERENCES USER_PROFILE_CCN(USERNAME)\r\n" + 
						"on delete cascade" );
				}
			addRecipe = con.prepareStatement("INSERT INTO RECIPES_CCN(recipe_name,owner,servings,calories_per_serving,carbs_per_serving,protein_per_serving,fats_per_serving) values(?,?,?,?,?,?,?)");
			addIngredient = con.prepareStatement("INSERT INTO RECIPE_INGREDIENTS_CCN(RECIPE_NAME, INGREDIENT,QUANTITY) values(?,?,?)");
			getOwnedRecipes = con.prepareStatement("SELECT recipe_name FROM RECIPES_CCN WHERE OWNER = ?");
			getIngredients = con.prepareStatement("SELECT REC.RECIPE_NAME,REC.SERVINGS,FOOD_NAME,F.CARBS_PER_100,F.PROTEIN_PER_100,F.FAT_PER_100,RI.QUANTITY \r\n" + 
					"FROM FOODS_CCN F JOIN RECIPE_INGREDIENTS_CCN RI ON (F.FOOD_NAME = RI.INGREDIENT) \r\n" + 
					"JOIN RECIPES_CCN REC ON (REC.RECIPE_NAME = RI.RECIPE_NAME) \r\n" + 
					"WHERE rec.recipe_name = ?");
			getFood = con.prepareStatement("SELECT * FROM FOODS_CCN WHERE FOOD_NAME = ?");
			removeRecipe = con.prepareStatement("DELETE FROM RECIPES_CCN WHERE RECIPE_NAME = ?");
			removeIngredient = con.prepareStatement("DELETE FROM RECIPE_INGREDIENTS_CCN WHERE INGREDIENT = ?");
			updateIngredient = con.prepareStatement("UPDATE RECIPE_INGREDIENTS_CCN SET QUANTITY = ? WHERE RECIPE_NAME = ? and INGREDIENT = ?");
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void add(String[] values) {
		/**
		 * Inserts a new recipe with the specified nutritional info into the RECIPES_CCN table
		 * and sets the appropriate author
		 * @param values - String array of elements recipe_name, followed by the nutritional information
		 */
		try {
			boolean ingredient = (values.length == 3);
			for(int i=0 ; i< values.length; i++) {
				if(ingredient)
					addIngredient.setString(i+1, values[i]);
				else
					addRecipe.setString(i+1, values[i]);
			}
			if(ingredient)
				addIngredient.executeUpdate();
			else
				addRecipe.executeUpdate();
			con.commit();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object readOnce(String[] values) {
		/**
		 * Method to create a Recipe Object based off its name or author in the RECIPES_CCN table
		 * The Table will be scanned for all the ingredients that feature in the recipe with this name
		 * @param values - the keys with which to query the database, recipe_name
		 * @return a new Recipe object if the recipe is found, null otherwise
		 */
		try {
			getIngredients.setString(1, values[0]);
			ResultSet rs = getIngredients.executeQuery();
			List<Food> ingredients = new ArrayList<Food>();
			String recipeName = null;
			int servings = -1;
			while(rs.next()) {
				Food ingredient = new Food(rs.getString(3),Double.parseDouble(rs.getString(4)),Double.parseDouble(rs.getString(5)),Double.parseDouble(rs.getString(6)),true);
				ingredient.editQuantity(Double.parseDouble(rs.getString(7)));
				ingredients.add(ingredient);
				recipeName = recipeName == null ? rs.getString(1) : recipeName;
				servings = servings < 0 ? Integer.parseInt(rs.getString(2)) : servings; 
			}
			return new Recipe(recipeName,ingredients,servings);
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <T> List<T> readMulti(String l,Class<T> type) {
		/**
		 * Method to create a list of Recipe objects  based off its name or author in the RECIPES_CCN table
		 * @param values - the keys with which to query the database, either a username, or username and recipe
		 * @return a new Recipe object if the recipe is found, null otherwise
		 */
		List<T> res = new ArrayList<T>();
		try {
			getOwnedRecipes.setString(1,l);
			ResultSet recipes = getOwnedRecipes.executeQuery();
			String[] lookup = new String[1];
			while(recipes.next()) {
				//For each recipe name, create a Recipe Object and add it to the list
				lookup[0] = recipes.getString(1);
				T obj = type.cast((readOnce(lookup)));
				res.add(obj);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public void update(String[] oldNew) {
		/**
		 * Updates a certain recipe in both the RECIPES_CCN store table and the associative one
		 * @param oldNew - a String array containing the data to be updated
		 * If updating an ingredient in a certain recipe oldNew has the form {recipeName,ingredientName,quantity},
		 * where OPTION is either UPDATE,DELETE,ADD
		 * 
		 */
		try {
			boolean ingredient = (oldNew.length == 3);
			if(ingredient) {
				System.out.println(Arrays.toString(oldNew));
				updateIngredient.setString(1, oldNew[2]);
				updateIngredient.setString(2, oldNew[0]);
				updateIngredient.setString(3, oldNew[1]);
				updateIngredient.executeUpdate();
			}
			else {
				String field = null;
			for(int i=0 ; i< oldNew.length; i++) {
				switch(i) {
				case 0: {field = "RECIPE_NAME";break;}
				case 1: {field = "SERVINGS";break;}
				case 2: {field = "CALORIES_PER_SERVING";break;}
				case 3: {field = "CARBS_PER_SERVING";break;}
				case 4: {field = "PROTEIN_PER_SERVING";break;}
				case 5: {field = "FATS_PER_SERVING";break;}
				}
				updateRecipe = con.prepareStatement("UPDATE  RECIPES_CCN SET " + field + " = ? WHERE RECIPE_NAME = ?");
				updateRecipe.setString(1, oldNew[i]);
				updateRecipe.setString(2, oldNew[0]);
				updateRecipe.executeUpdate();
				}
			}
			con.commit();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void delete(String lookUp) {
		/**
		 * Removes the recipe from the database.
		 * @param lookUp - a String denoting the name of the recipe
		 */
		try {
			removeRecipe.setString(1, lookUp);
			removeRecipe.executeUpdate();
			System.out.println(lookUp);
			removeIngredient.setString(1, lookUp);
			removeIngredient.executeUpdate();
			con.commit();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		
	}

}

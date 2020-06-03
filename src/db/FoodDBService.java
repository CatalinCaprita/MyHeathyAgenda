package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import food.Food;

public class FoodDBService implements DBService{
	private PreparedStatement addFood;
	private PreparedStatement updatePersonalFood;
	private PreparedStatement getFood;
	private PreparedStatement removePersonalFood;
	private Connection con;
	public FoodDBService() {
		try {
			con = DBManager.getConnection();
			ResultSet rs = con.getMetaData().getTables(null, null, "FOODS_CCN", new String[] {"TABLE"});
			if(rs.next() == false) {
				con.createStatement().execute("CREATE TABLE FOODS_CCN(FOOD_NAME VARCHAR2(20) CONSTRAINT FOODS_PK_NAME PRIMARY KEY,CARBS_PER_100 NUMBER(6,2) CONSTRAINT FOODS_CARBS_NN NOT NULL,PROTEIN_PER_100 NUMBER(6,2) CONSTRAINT FOODS_PROT_NN NOT NULL,FAT_PER_100 NUMBER(6,2) CONSTRAINT FOODS_FAT_NN NOT NULL)");
				}
			addFood = con.prepareStatement("INSERT INTO FOODS_CCN(food_name,carbs_per_100,protein_per_100,fat_per_100) values(?,?,?,?)");
			getFood = con.prepareStatement("SELECT * FROM FOODS_CCN WHERE lower(FOOD_NAME) like '%'||lower(?)||'%'");
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void add(String[] values) {
		/**
		 * Inserts a new type of food with the specified nutritional info into the FOODS_CCN table
		 * @param values - String array of elements food_name, followed by the nutritional information
		 */
		try {
			for(int i=0 ; i< values.length; i++) {
				addFood.setString(i+1, values[i]);
			}
			addFood.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Object readOnce(String[] values) {
		/**
		 * Exctracts nutritional information about a food looked up by its name in the FOOD_CCN table.
		 * @param values - String array wiht one item, {food_name} the primary key in the FOODS_CCN table
		 * @return a Food object with the querried info if the food is found in the DB, or null otherwise.
		 */
		try {
			getFood.setString(1, values[0]);
			ResultSet rs = getFood.executeQuery();
			if(rs.next() == false)
				return null;
			return new Food(rs.getString(1),Double.parseDouble(rs.getString(2)),Double.parseDouble(rs.getString(3)),Double.parseDouble(rs.getString(4)),true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <T> List<T> readMulti(String l, Class<T>type) {
		return null;
	}

	@Override
	public void update(String[] oldNew) {
	}

	@Override
	public void delete(String lookUp) {
		// TODO Auto-generated method stub
		
	}
	

}

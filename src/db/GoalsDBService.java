package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import user.Goals;
import util.InputHandler;

public class GoalsDBService implements DBService {
	private PreparedStatement addGoals;
	private PreparedStatement updateGoals;
	private PreparedStatement getGoals;
	private PreparedStatement updateItem;
	private static int uid = 1;
	private static Connection con;
	public GoalsDBService() {
		try {
			con = DBManager.getConnection();
			addGoals = con.prepareStatement("INSERT INTO USER_PROFILE_CCN(username,email,height,age,gender,activity_id,goal_weight,wo_per_week,mins_per_wo,physique_goal,calories_per_day,carbs_per_day,prot_per_day,fats_per_day,weight)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			getGoals = con.prepareStatement("SELECT * FROM USER_PROFILE_CCN WHERE USERNAME = ?");
			
		} catch (SQLException e) {
			System.out.println(e + " " +e.getMessage());
		}
	}
	@Override
	public void add(String[] values) {
		/**Adds in bulk all the basic information for a user's profile, as a result of the configuration query in the Session Class,.i.e these values
		 * are first set whenever a new user regiseter an account, and then can update any field.
		 * @param A String[] array of values for the follwing 15 fields: 
		 * username, email, height,
		 * weight, age, gender,
		 * activity_id, goal_weight, wo_per_week, mins_per_wo, physique_goal
		 * calories_per_day, carbs_per_day, prot_per_day, fats_per_day
		 */
		int i = 0;
		try {
			for(i=0 ; i < values.length; i++)
				addGoals.setString(i+1, values[i]);
			addGoals.executeUpdate();
			
		}catch(SQLException e) {
			System.out.println(i);
			e.printStackTrace();
		}
		
	}

	@Override
	public Object readOnce(String[] values) {
		/**
		 * Reads all the values for a configuration of goals 
		 * @param a single String that identifies the user by its "username"
		 *@return an object to be Cast to a Goals Object upon use
		 */

		String[] qRes = new String[13];
		try {
			getGoals.setString(1, values[0]);
			ResultSet rs = getGoals.executeQuery();
			while(rs.next()) {
				for(int i=0; i < qRes.length; i++) {
					qRes[i] = rs.getString(i + 3);
				}
			}

			return new Goals(qRes);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public <T> List<T> readMulti(String l,Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(String[] oldNew) {
		/**Updates fields in the USER_PROFILE table according to the information given
		 * @param oldNew a String array with either length == 3 in the form {oldItem,newItem,identifier}
		 * or length == 6 in the form { caloriesPerDay,carbsPerDay, proteinPerDay,fatsPerDAy, identifier} that is a result
		 * of a user changing information about them and recalculation of the daily values.
		 */
		
		if(oldNew.length == 3) {
			int decision = InputHandler.listenInt("Are you sure you want to change " + oldNew[0] +"? Doing so would recalculate all your goals. 1.Yes 0.No",-1);	
			if(decision == 1) {
				try {
					
					updateItem = con.prepareStatement("UPDATE USER_PROFILE_CCN SET "+ oldNew[0] +"= ? where USERNAME = ?");
					updateItem.setString(1, oldNew[1]);
					updateItem.setString(2, oldNew[2]);
					System.out.println(updateItem.executeUpdate());
				}catch(SQLException e) {
					e.printStackTrace();
					}
				}
			}
		else if(oldNew.length == 10) {
			try {
				
				for(int i=2 ; i < oldNew.length - 1;i+=2) {
					updateItem = con.prepareStatement("UPDATE USER_PROFILE_CCN SET "+ oldNew[i] +"= ? where USERNAME = ?");
					updateItem.setString(1, oldNew[i + 1]);
					updateItem.setString(2,oldNew[0]);
					updateItem.executeUpdate();
				}
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
		

	@Override
	public void delete(String lookUp) {
		
	}

}

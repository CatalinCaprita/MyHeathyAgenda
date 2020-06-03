package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import user.User;
import util.PasswordStorer;

public class UserDBService implements DBService{
	private PreparedStatement addUser;
	private PreparedStatement logCheck;
	private PreparedStatement updateInfo;
	private PreparedStatement deleteStmt;
	private static int uid = 1;
	private static Connection con;
	
	public UserDBService() {
		try {
			con = DBManager.getConnection();
			addUser = con.prepareStatement("INSERT INTO USERS_CCN VALUES(?,?)");
			logCheck = con.prepareStatement("SELECT ENCR_PASS FROM USERS_CCN WHERE USERNAME = ?");
			updateInfo = con.prepareStatement("UPDATE USERS_CCN SET ? = ? where USERNAME = ?");
			deleteStmt = con.prepareStatement("DELETE FROM USERS_CCN WHERE USERNAME = ? ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void add(String[] values) {
		/***
		 *	Inserts a new user into the USERS table
		 * @param: an array of strings in the form 'username', 'password', 'email'
		 *  
		 */
		try {
			addUser.setString(1, values[0]);
			addUser.setString(2,values[1]);
			addUser.executeUpdate();
			System.out.println("We have added you to our DataBase.");
		}catch(SQLException e) {
			e.printStackTrace();
		}
			
	}

	@Override
	public Object readOnce(String[] values) {
		/***
		 * Method that scans the DB for the valid values
		 * Primarily used for Login process as it is lookup up username and password
		 *@param : values - an array of Strings in the for username, password
		 *@return : An Object if the Result Set is not empty or null otherwise
		 */
		
		try {
			logCheck.setString(1,values[0]);
			ResultSet rs = logCheck.executeQuery();
			if(rs.next() == false)
				return null;
			if(PasswordStorer.decrypt(rs.getString(1)).equals(values[1]))
				return new Object();
			return null;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public <T> List<T> readMulti(String l,Class<T> type){
		List<T> resList = new ArrayList<T>();
		return resList;
	}
	@Override
	public void update(String[] oldNew) {
		/***
		 * Updates information into the USERS TABLE 
		 * 
		 * @param: String[] array in the form  {oldItem, newItem,username} where 
		 * Item can be either password or username
		 * 
		 */
		try {
			updateInfo.setString(1,oldNew[0]);
			updateInfo.setString(2,oldNew[1]);
			updateInfo.setString(3,oldNew[2]);
			updateInfo.executeUpdate();
		}catch(SQLException e) {
			System.out.println(e + " " +e.getMessage());
		}
		
		
	}

	@Override
	public void delete(String lookup) {
		/**
		 * Deletes an entry from the USER Table, aka removes the account
		 * @param lookup - A String denoting the username
		 */
		try {
			deleteStmt.setString(1, lookup);
			deleteStmt.executeUpdate();
		}catch(SQLException e) {
			System.out.println(e + " " +e.getMessage());
		}
		
	}
	
}

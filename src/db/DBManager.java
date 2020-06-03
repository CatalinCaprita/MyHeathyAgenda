package db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
	private static Map<String,Object> services = new HashMap<>();
	private static DBManager instance = new DBManager();
	private static Connection con;
	private DBManager() {
		try{
			con = DriverManager.getConnection("jdbc:oracle:thin:@193.226.51.37:1521:o11g","grupa41","bazededate");
		services.putIfAbsent("user", new UserDBService());
		services.putIfAbsent("food", new FoodDBService());
		services.putIfAbsent("goals", new GoalsDBService());
		services.putIfAbsent("recipes", new RecipeDBService());
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public static  DBManager getInstance() {
		if(instance == null) {
			synchronized(DBManager.class) {
				synchronized(instance) {
					if(instance == null)
						instance = new DBManager();
				}
			}
		}
		return instance;
	}
	public static Object process(String serviceType, Method method,Object[] args ) {
		if(services.get(serviceType) == null) {
			System.out.println("No such service recorded!");
			return null;
		}
		try {
			return method.invoke(services.get(serviceType),args);
		}catch(NullPointerException e) {
			
		}catch (SecurityException e) {
	
			e.printStackTrace();
		} catch (IllegalAccessException e) {
		
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	public static PreparedStatement setStatement(String statement) throws SQLException{
			return con.prepareStatement(statement);
	}
	public static Connection getConnection() {
		return con;
	}
	public static void close() {
		try {
		
			con.close();
			if(con.isClosed())
				System.out.println("Connection closed.");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public static DBService getService(String serviceType) {
		return (DBService)services.get(serviceType);
	}
}

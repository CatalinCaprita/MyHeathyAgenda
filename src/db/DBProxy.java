package db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import spaces.App;

public class DBProxy {
	/***

	 * @param serviceType String object indicating which service is required from the DBManager class,one of the following:\n
	 * 	 - user to make use of the UserDBManager implementation of the DBService interface\r\n
	 	 - food to make use of the FoodDBManager implmenetation of the DBService\r\n
	 	 - goals to make use of the GoalsDBManager implmenetation of the DBService\r\n
		 - recipes to make use of the RecipeDBManager implmenetation of the DBService\r\n
	 * @return a dynamic Proxy creted via the newProxyInstance method, that will implement the DBService interface
	 ***/
	public static DBService create(String serviceType) {
		InvocationHandler invocationHandler = new InvocationHandler() {
			public Object invoke(Object instance, Method method, Object[] args) {
				Object result = DBManager.process(serviceType,method,args);
				return result;
			}
		};
		return DBService.class.cast(Proxy.newProxyInstance(App.class.getClassLoader(),
				new Class<?>[] {DBService.class},
				invocationHandler));
	}
}

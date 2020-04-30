package util;

import food.Carbohydrate;

public class GoalsCalculator {
	private GoalsCalculator() {
		
	}
	private static GoalsCalculator self = new GoalsCalculator();
	public static GoalsCalculator call() { 
		self = self == null ? new GoalsCalculator() : self;
		return self;
	}
}

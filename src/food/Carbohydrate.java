package food;

public class Carbohydrate extends MacroNutrient{
	private static final int CALORIC_VALUE = 4;
	public Carbohydrate() {}
	public Carbohydrate(double quantity) {
		super(quantity);
	}
	public Carbohydrate(String quantity) {
		super(Double.parseDouble(quantity));
	}
	public int getCaloricValue(){
		return CALORIC_VALUE;
	}
	public double computeEnergy() {
		return CALORIC_VALUE * quantity;
	}

}

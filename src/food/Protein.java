package food;

public class Protein extends MacroNutrient {
	private static final int CALORIC_VALUE = 4;
	public Protein() {}
	public Protein(double quantity) {
		super(quantity);
	}
	public  int getCaloricValue(){
		return CALORIC_VALUE;
	}
	public double computeEnergy() {
		return CALORIC_VALUE * quantity;
	}

}

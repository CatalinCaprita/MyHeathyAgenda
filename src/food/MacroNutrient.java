package food;

public abstract class MacroNutrient {
	double quantity;
	protected String brief;
	public MacroNutrient() {}
	public MacroNutrient(double quantity) {
		this.quantity = quantity;
	}
	public void addQuantity(double amount) {
		quantity += amount;
	}
	public double getQuantity() {
		return quantity;
	}
	public String toString() {
		return Double.toString(this.quantity);
	}
	public abstract int getCaloricValue();
	public abstract double computeEnergy();
	
}

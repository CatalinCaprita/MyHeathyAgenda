package food;

public class Fat extends MacroNutrient{
	private double[] split = new double[4];
	private static final int CALORIC_VALUE = 9;
	public Fat() {}
	public Fat(double quantity) {
		super(quantity);
	}
	public  int getCaloricValue(){
		return CALORIC_VALUE;
	}
	public double computeEnergy() {
		return quantity * CALORIC_VALUE;
	}
	public double[] getTypePercentages() {
		return split;
	}
	public void setPercentage(FatType t,double percentage) {
		switch(t){
		case OMEGA_3:{
			split[0] = percentage;
			break;
		}
		case OMEGA_6:{
			split[1] = percentage;
			break;
		}case TRANS:{
			split[2] = percentage;
			break;
		}case SATURATED:{
			split[3] = percentage;
			break;
		}
		}
	}
				
}

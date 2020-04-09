package food;

public class NegativeMacroException extends Exception {
	public String getMessage() {
		return "No one I know can eat negative grams of something!";
	}
}

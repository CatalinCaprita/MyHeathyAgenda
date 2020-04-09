package util;

public class ErrorOnCommandException extends Exception {
	public String getMessage() {
		return "\nCommand Unsuccessful.";
	}
}

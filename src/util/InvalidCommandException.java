package util;

public class InvalidCommandException extends Exception{
	public String getMessage() {
		return "\nPlease select a valid command.";
	}
}

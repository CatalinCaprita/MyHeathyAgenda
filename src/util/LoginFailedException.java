package util;

public class LoginFailedException extends Exception{
	public String getMessage() {
		return "Invalid Credetials.\n";
	}
}

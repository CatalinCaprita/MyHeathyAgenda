package util;

import java.time.LocalDateTime;
import java.util.Date;

public class TimeStamp {
	private String actionName;
	private LocalDateTime date;
	public TimeStamp(String action) {
		this.actionName = action;
		date = LocalDateTime.now();
	}
}

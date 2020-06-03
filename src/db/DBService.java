package db;

import java.io.Serializable;
import java.util.List;

public interface DBService extends Serializable{
	public void add(String[] values);
	public Object readOnce(String[] lookup);
	public <T> List<T> readMulti(String value,Class<T> type);
	public void update(String[] oldNew);
	public void delete(String lookUp);
}

package spaces;

import javax.swing.JPanel;

public interface Interactive {
	public int action(int actionId);
	public void showOpts();
	public int size();
	public String getActionName(int actionId);
	public String getOpts();
	public JPanel createPanel();

}

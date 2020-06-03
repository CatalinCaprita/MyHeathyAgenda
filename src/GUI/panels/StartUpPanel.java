package GUI.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StartUpPanel extends JPanel {
	private JTextField text = new JTextField();
	private JLabel titleLabel;
	private JButton singIn;
	private JButton register;//= new JButton("Register");
	private JButton exit;// = new JButton("Exit");
	
	public StartUpPanel() {
		super();
		 GridBagLayout gridBagLayout = new GridBagLayout();
	        gridBagLayout.columnWidths = new int[]{100};
	        gridBagLayout.rowHeights =new int[]{1};
	        gridBagLayout.columnWeights = new double[]{0};
	        gridBagLayout.rowWeights = new double[]{10};
	        setLayout(gridBagLayout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		//Will Only need  a single column 
		 titleLabel = new JLabel("Welcome to your Healthy Agenda!");
		 gbc.gridx = 0;
		 gbc.gridy = 0;
		 gbc.ipadx = 100;
		 gbc.anchor = GridBagConstraints.CENTER;
		 add(titleLabel,gbc);
		 /*
		 singIn = new JButton("Sign-in");
		 gbc.gridx = 0;
		 gbc.gridy = 1;
		 gbc.weighty = 1;
		 gbc.anchor = GridBagConstraints.CENTER;
		
		 add(singIn,gbc);
		 
		 register = new JButton("Register");
		 gbc.gridx = 0;
		 gbc.gridy = 2;
		 gbc.anchor = GridBagConstraints.CENTER;
		 add(register,gbc);
		 
		 exit = new JButton("Exit");
		 gbc.gridx = 0;
		 gbc.gridy = 3;
		 gbc.anchor = GridBagConstraints.CENTER;
		 add(exit,gbc);
		 */
		 
		
	}
	

}

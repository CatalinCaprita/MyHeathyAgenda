package GUI.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class LogPanel extends JPanel{
	private GridBagConstraints gbc;
	private GridBagLayout gbl;
	private JLabel jlb;
	public LogPanel() {
		super();
		Dimension size = this.getPreferredSize();
		size.width = 250;
		this.setPreferredSize(size);
		gbc = new GridBagConstraints();
			gbl = new GridBagLayout();
			gbl = new GridBagLayout();
			gbl.rowHeights = new int[3];
		    Arrays.fill(gbl.rowHeights,5);
		    gbl.rowWeights = new double[3];
		    Arrays.fill(gbl.rowWeights,3);
		    gbl.columnWidths= new int[2];
		    Arrays.fill(gbl.columnWidths,5);
		    setLayout(gbl);
		    
		    JLabel jlb = new JLabel("We are going to need your credentials");
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_END;
			add(jlb,gbc);
			
			JLabel userLabel = new JLabel("Username:");
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.LINE_END;
			add(userLabel,gbc);
			
			JTextField userText = new JTextField(20);
		    gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.LINE_END;
			add(userText,gbc);
			
			JLabel passLabel = new JLabel("Password:");
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.LINE_END;
			add(passLabel,gbc);
			
			JPasswordField passField = new JPasswordField(20);
		    gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.LINE_END;
			add(passField,gbc);
			setBorder(new LineBorder(Color.DARK_GRAY,2));
			setBackground(Color.BLUE);
		
	}
	
}

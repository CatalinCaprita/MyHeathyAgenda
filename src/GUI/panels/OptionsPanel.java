package GUI.panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import spaces.Interactive;
import util.OptionHandler;

public class OptionsPanel extends JPanel{
	private GridBagConstraints gbc;
	private GridBagLayout gbl;
	private JLabel jlb;
	public OptionsPanel() {
		setLayout(new CardLayout());
		gbc = new GridBagConstraints();
		gbl = new GridBagLayout();
		gbl = new GridBagLayout();
		gbl.rowHeights = new int[8];
	    Arrays.fill(gbl.rowHeights,5);
	    gbl.rowWeights = new double[7];
	    Arrays.fill(gbl.rowWeights,3);
		gbc = new GridBagConstraints();
		setBorder(new LineBorder(Color.DARK_GRAY,2));
		setBackground(Color.BLUE);
		
	}
	public void addOptionsGrid(Interactive optionsHolder,int depth) {
		String[] options = optionsHolder.getOpts().split("\\| ");
		JPanel optGrid = new JPanel(gbl);
		int lastRow = 1;
		for(int i=0 ; i< options.length; i++) {
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.gridx = 0;
			gbc.gridy = lastRow++;
			gbc.weighty = 2;
			JButton optBtn = new JButton(options[i]);
			optBtn.setActionCommand(options[i].substring(0,1));
			optBtn.addActionListener(OptionHandler.getActionListener());
			optGrid.add(optBtn,gbc);
		}
		optGrid.setBackground(Color.BLUE);
		add(optGrid,Integer.toString(depth));
	}
	public void swapOptionsGrid(int depth) {
		CardLayout cl = (CardLayout)this.getLayout();
		cl.show(this,Integer.toString(depth));
	}
}

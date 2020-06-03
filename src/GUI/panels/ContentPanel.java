package GUI.panels;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JPanel;

import spaces.Interactive;

public class ContentPanel extends JPanel{
	private GridBagConstraints gbc;
	private GridBagLayout gbl;
	public ContentPanel() {
		setLayout(new CardLayout());
		gbc = new GridBagConstraints();
		gbl = new GridBagLayout();
		gbl = new GridBagLayout();
		gbl.rowHeights = new int[1];
	    Arrays.fill(gbl.rowHeights,5);
	    gbl.rowWeights = new double[7];
	    Arrays.fill(gbl.rowWeights,10);
		gbc = new GridBagConstraints();
	}
	public void addContentPanel(Interactive interf, int depth) {
		add(interf.createPanel(),Integer.toString(depth));
	}
	public void swapContent(int depth) {
		CardLayout cl = (CardLayout)this.getLayout();
		cl.show(this,Integer.toString
				(depth));
	}
}

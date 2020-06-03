package GUI;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import GUI.panels.OptionsPanel;
import GUI.panels.StartUpPanel;
import db.DBManager;
import spaces.recipes.Recipe;

public class AppFrame extends JFrame{
	private static JPanel contentCards = new JPanel(new CardLayout());
	private static OptionsPanel options; 
	private JPanel content = new JPanel();
	private static GridBagConstraints gbc;
	private static GridBagLayout gbl;
	private static AppFrame instance;
	private AppFrame(String title) {
		super(title);
		Container c = this.getContentPane();
		
		gbl = new GridBagLayout();
		 gbl.columnWeights = new double[]{12, 2};
	     gbl.columnWidths = new int[]{2, 4};
	     gbl.rowHeights = new int[1];
	     Arrays.fill(gbl.rowHeights,100);
	     gbl.rowWeights = new double[2];
	     Arrays.fill(gbl.rowWeights,100);
		c.setLayout(gbl);
		
		gbc = new GridBagConstraints();
		options = new OptionsPanel();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		StartUpPanel sup = new StartUpPanel();
		c.add(options,gbc);
		contentCards.add(new StartUpPanel(),"startUp");
		gbc.gridx = 2;
		gbc.gridy = 0;
		c.add(contentCards,gbc);
		this.setSize(256,512);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(true);
		this.setVisible(true);
	}
	public static AppFrame getInstance(String title) {
		if(instance == null) {
			synchronized(AppFrame.class) {
				createInstanceIfNull();
			}
		}
		return instance;
	}
	public static void createInstanceIfNull() {
			if(instance == null)
				instance = new AppFrame("My Healthy Agenda");
	}
	public void swapPanels(Container pane, String id) {
		CardLayout cl = (CardLayout)pane.getLayout();
		System.out.println("Swapping contents" + id);
		cl.show(pane, id);
	}
	public OptionsPanel getOptionsPanel() {
		return options;
	}
}

package timeclock.gui;

import static org.junit.Assert.*;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.junit.Test;

public class HoursEditingPanelTest {
	
	private JFrame mainFrame;
	private HoursEditingPanel hoursEditingPanel;

	public HoursEditingPanelTest() {
		mainFrame = new JFrame("Timeclock");
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setLocation(200, 200);
		mainFrame.setSize(600, 400);
		
		hoursEditingPanel = new HoursEditingPanel();
		
		mainFrame.add(hoursEditingPanel);
		
		mainFrame.setVisible(true);
	}
	
	@Test
	public void testGetSliderPosFromTime() {
//		assertEquals(0, HoursEditingPanel.getSliderPosFromTime(5.0));
//		assertEquals(60, HoursEditingPanel.getSliderPosFromTime(11.0));
//		assertEquals(180, HoursEditingPanel.getSliderPosFromTime(23.0));
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignore) {}
		new HoursEditingPanelTest();
	}
}

package timeclock.gui;

public class HoursEditingPanelThread extends Thread {
	
	private HoursEditingPanel hoursEditingPanel;
	
	@Override
	public void start() {
		hoursEditingPanel = new HoursEditingPanel();
	}
	
	public HoursEditingPanel getHoursEditingPanel() {
		return hoursEditingPanel;
	}
}

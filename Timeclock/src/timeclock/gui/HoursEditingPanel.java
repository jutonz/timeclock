package timeclock.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import timeclock.core.Timespan;
import timeclock.core.Workday;

public class HoursEditingPanel extends JPanel implements ActionListener {
	
	public static final int CANCEL_RESULT = 0;
	public static final int UPDATE_RESULT = 1;
	public static final int NO_RESULT = 2;
	
	private JButton addButton;
	private JButton backButton;
	private JPanel buttonPanel;
	private JPanel timespansPanel;
	LinkedList<TimespanContainer> timespans;
	private Workday workday;
	
	private ActionListener actionListener;
	
	public HoursEditingPanel(Workday w) {
		timespans = new LinkedList<TimespanContainer>();
		setWorkday(w);
	}
	
	public HoursEditingPanel() {
		this(null);
	}
	
	public void setWorkday(Workday w) {	
		// Remove all entries from previously viewed workdays
		removeAll();
		timespans.clear();
		if (w == null) return;
		workday = w;
		setLayout(new BorderLayout());
		
		// Add all timespans from the workday
		timespansPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx   = 0;
		c.gridy   = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill    = GridBagConstraints.BOTH;
		LinkedList<Timespan> schedule = w.getSchedule();
		if (schedule.size() == 0)
			schedule.add(new Timespan());
		int i = 1;
		for (Timespan tspan : schedule) {
			TimespanContainer cont = new TimespanContainer(tspan);
			if (schedule.size() > 1)
				cont.setBorderTitle("Shift " + i++);
			timespans.add(cont);
			c.gridy++;
			timespansPanel.add(cont, c);
		}
		
		// Create navigation buttons
		buttonPanel = new JPanel(new FlowLayout());
		addButton = new JButton("Update Schedule");
		addButton.addActionListener(this);

		backButton = new JButton("Cancel");
		backButton.addActionListener(this);
		buttonPanel.add(addButton);
		buttonPanel.add(backButton);

		// Add panels to main container
		JScrollPane timespanScrollPane = new JScrollPane(timespansPanel);
		add(timespanScrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public Workday getWorkday() {
		return workday;
	}


	public void addActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		ActionEvent ev;
		
		
		if (src == addButton) 
		{
			// Update workday with new timespans
			workday.clearSchedule();
			for (TimespanContainer c : timespans)
				workday.addHours(c.getTimespan());
			ev = new ActionEvent(this, UPDATE_RESULT, null);
		} 
		
		
		else if (src == backButton) 
		{
			ev = new ActionEvent(this, CANCEL_RESULT, null);
		} 
		
		
		else 			
		{
			ev = new ActionEvent(this, NO_RESULT, null);
		}
		
		
		
		// Signal to GUI
		actionListener.actionPerformed(ev);
		
	}
	
	private class TimespanContainer extends JPanel implements ChangeListener { 
		
		private Hashtable<Integer, JLabel> sliderLabelTable;
		private JPanel startSliderPanel;
		private JSlider startSlider;
		private JPanel endSliderPanel;
		private JSlider endSlider;
		private JLabel deleteButton;
		
		private Timespan timespan;
		
		
		public TimespanContainer(Timespan tspan) {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			setBorder(BorderFactory.createTitledBorder("Shift" ));
			
			// Create the delete button
			deleteButton = new JLabel("X");
			
			// Create the start time slider
			startSliderPanel = new JPanel(new BorderLayout());
			startSliderPanel.setBorder(BorderFactory.createTitledBorder("Start Time"));

			startSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, 20);
			startSlider.setName("startSlider");
			startSlider.addChangeListener(this);
			startSlider.setLabelTable(createSliderLabelTable());
			startSlider.setMajorTickSpacing(10);
			startSlider.setMinorTickSpacing(5);
			startSlider.setPaintTicks(true);
			startSlider.setPaintLabels(true);
			startSlider.setSnapToTicks(true);

			startSliderPanel.add(startSlider, BorderLayout.CENTER);

			// Create the end time slider
			endSliderPanel = new JPanel(new BorderLayout());
			endSliderPanel.setBorder(BorderFactory.createTitledBorder("End Time"));

			endSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, 20);
			endSlider.setName("endSlider");
			endSlider.addChangeListener(this);
			endSlider.setLabelTable(sliderLabelTable);
			endSlider.setMajorTickSpacing(10);
			endSlider.setMinorTickSpacing(5);
			endSlider.setPaintTicks(true);
			endSlider.setPaintLabels(true);
			endSlider.setSnapToTicks(true);

			endSliderPanel.add(endSlider, BorderLayout.CENTER);
			
			// Set up constraints and add components to the main container
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.EAST;
			
			add(deleteButton, c);

			c.anchor = GridBagConstraints.CENTER;
			c.gridy++;
			add(startSliderPanel, c);

			c.gridy++;
			add(endSliderPanel, c);
			
			// Instantiate fields with data from the workday
			setTimespan(tspan);
		}
		
		public void setBorderTitle(String newTitle) {
			setBorder(BorderFactory.createTitledBorder(newTitle));
		}

		
		public TimespanContainer() {
			this(null);
		}
		
		/**
		 * Instantiates the editing panel with data contained
		 * in the specified workday.
		 */
		public void setTimespan(Timespan t) {
			if (t == null)
				return;
			int startVal = getSliderPosFromTime(t.getStart());
			int endVal   = getSliderPosFromTime(t.getEnd());
			startSlider.setValue(startVal);
			endSlider.setValue(endVal);
		}
		
		public Timespan getTimespan() {
			double start = getDoubleTimeFromSlider(startSlider.getValue());
			double end   = getDoubleTimeFromSlider(endSlider.getValue());
			return new Timespan(start, end);
		}
		
		private double getDoubleTimeFromSlider(int sliderPos) {
			double ret = 5;
			for ( ; sliderPos > 0; sliderPos -= 5, ret += 0.5);
			return ret;
		}
		
		private String getTimeFromSliderPos(int pos) {
			String ret = "";
			int hour;
			int minute;		
			if (pos < 80) {
				// If before 1:00 
				hour = 5 + (pos / 10);
				minute = (pos % 10 == 0) ? 0 : 30;
			} else {
				// If after 1:00
				hour = (pos - 70) / 10;
				minute = (pos % 10 == 0) ? 0 : 30;
			}
			ret += hour + ":";
			ret += (minute == 0) ? "00" : "30";
			return ret;
		}
		
		public int getSliderPosFromTime(double time) {
			int ret = 0;
			time -= 5; // Because the earliest time is 5:00 AM
			for ( ; time > 0; time -= 0.5, ret += 5);
			return ret;
		}
		
		public void stateChanged(ChangeEvent e) {
//			JSlider source = (JSlider) e.getSource();
//			JLabel posLabel;
//			if (source.getName().equals("startSlider")) {
//				source   = startSlider;
//				posLabel = startSliderPositionLabel;
//			} else {
//				source   = endSlider;
//				posLabel = endSliderPositionLabel;
//			}
//
//			int pos = (int) source.getValue();
//			String time = getTimeFromSliderPos(pos);
//			posLabel.setText(time);
			
			JSlider source = (JSlider) e.getSource();
			JPanel panel;
			String prefix;
			if (source.getName().equals("startSlider")) {
				prefix = "Start time: ";
				panel = startSliderPanel;
			} else {
				prefix = "End time: ";
				panel = endSliderPanel;
			}
			int pos = (int) source.getValue();
			panel.setBorder(BorderFactory.createTitledBorder(prefix + getTimeFromSliderPos(pos)));
		}
		
		private Hashtable<Integer, JLabel> createSliderLabelTable() {
			sliderLabelTable = new Hashtable<Integer, JLabel>();
			sliderLabelTable.put(0,   new JLabel("5:00"));
			sliderLabelTable.put(20,  new JLabel("7:00"));
			sliderLabelTable.put(40,  new JLabel("9:00"));
			sliderLabelTable.put(60,  new JLabel("11:00"));
			sliderLabelTable.put(80,  new JLabel("1:00"));
			sliderLabelTable.put(100, new JLabel("3:00"));
			sliderLabelTable.put(120, new JLabel("5:00"));
			sliderLabelTable.put(140, new JLabel("7:00"));
			sliderLabelTable.put(160, new JLabel("9:00"));
			sliderLabelTable.put(180, new JLabel("11:00"));
			return sliderLabelTable;
		}
	}

}

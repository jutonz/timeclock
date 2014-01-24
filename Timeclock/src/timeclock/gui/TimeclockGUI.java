package timeclock.gui;

import timeclock.core.SimpleDate;
import timeclock.core.Timeclock;
import timeclock.core.Timespan;
import timeclock.core.Workday;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class TimeclockGUI implements TableModelListener, ActionListener {
	
	/** Serves as the program's functional controller */
	private Timeclock timeclock;
	
	/** The main frame; houses all frames for the entire program */
	private JFrame mainFrame;
	/** Houses the components of the program associated with displaying scheduled hours and switching between pay periods */
	private JPanel payPeriodPanel;
	/** Contains both a JTable of scheduled hours and a JPanel for editing said hours. Both are stored in a CardLayout */
	private JPanel payDisplayPanel;
	/** Panel within payPeriodPanel specifically tasked with displaying scheduled hours */
		private JPanel hoursGridPanel;
			/** Table in which scheduled hours are displayed */
			private JTable hoursTable;
			/** Models the data displayed in hoursTable */
			private DefaultTableModel hoursTableModel;
			private static final int DATE_COLUMN = 0;
			private static final int DAY_COLUMN = 1;
			private static final int START_COLUMN = 2;
			private static final int END_COLUMN = 3;
			private static final int HOURS_COLUMN = 4;
		private HoursEditingPanel hoursEditingPanel;
	/** Houses components associated with switching between pay periods */
	private JPanel payPeriodSwitchingPanel;
		/** Pressed to indicate that the program should display information for the previous pay period. */
		private JButton previousPayPeriodButton;
		/** Indicates the current pay period. Should be updated when switching between periods. */
		private JLabel payPeriodLabel;
		/** Pressed to indicate that the program should display information for the next pay period. */
		private JButton nextPayPeriodButton;
	/** Houses the components of the program associated with displaying information related to compensation */
	private JPanel payPanel;
		private JLabel weekOneHoursWorkedLabel;
		private JLabel weekOneMarkoutLabel;
		private JButton weekOneAddMarkoutButton;
		private JButton weekOneAddTipsButton;
		private JLabel weekOneTipsLabel;
		private JLabel weekTwoHoursWorkedLabel;
		private JLabel weekTwoMarkoutLabel;
		private JButton weekTwoAddMarkoutButton;
		private JButton weekTwoAddTipsButton;
		private JLabel weekTwoTipsLabel;
		private JLabel totalHoursWorkedLabel;
		private JLabel grossPayLabel;
	
	public TimeclockGUI() {
		timeclock = new Timeclock();
		
		mainFrame = new JFrame("Timeclock");
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setLocation(200, 200);
		mainFrame.setSize(600, 400);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveAndClose();
			}
		});
		
			payPeriodPanel = new JPanel(new BorderLayout());
			
				payDisplayPanel = new JPanel(new CardLayout());
				
					hoursGridPanel = new JPanel(new BorderLayout());
					hoursGridPanel.setBorder(BorderFactory.createTitledBorder("Scheduled Hours"));
						
						createHoursTable();
						JScrollPane sp = new JScrollPane(hoursTable);
						sp.setBorder(BorderFactory.createEmptyBorder());
						hoursGridPanel.add(sp, BorderLayout.CENTER);
						
				payDisplayPanel.add(hoursGridPanel, "gridPanel");
				
					hoursEditingPanel = new HoursEditingPanel();
					hoursEditingPanel.addActionListener(this);
						
				payDisplayPanel.add(hoursEditingPanel, "editingPanel");
				
			payPeriodPanel.add(payDisplayPanel, BorderLayout.CENTER);
			
				payPeriodSwitchingPanel = new JPanel();
				
					previousPayPeriodButton = new JButton("<");
					previousPayPeriodButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							moveToPreviousPayPeriod();
						}
					});
					
				payPeriodSwitchingPanel.add(previousPayPeriodButton);
					
					payPeriodLabel = new JLabel("Pay Period Placeholder");
					
				payPeriodSwitchingPanel.add(payPeriodLabel);
				
					nextPayPeriodButton = new JButton(">");
					nextPayPeriodButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							moveToNextPayPeriod();
						}
					});
					
				payPeriodSwitchingPanel.add(nextPayPeriodButton);
				
			payPeriodPanel.add(payPeriodSwitchingPanel, BorderLayout.SOUTH);				
			
		mainFrame.add(payPeriodPanel, BorderLayout.CENTER);
		
			payPanel = new JPanel(new GridBagLayout());
			
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				
				JPanel weekOnePanel = new JPanel(new GridBagLayout());
					weekOnePanel.setBorder(BorderFactory.createTitledBorder("Week 1"));
					GridBagConstraints c1 = new GridBagConstraints();
					String weekOneHoursWorked = "xx";
					
					weekOneHoursWorkedLabel= new JLabel("Hours Worked: " + weekOneHoursWorked);
					c1.gridx = 0;
					c1.gridy = 0;
				weekOnePanel.add(weekOneHoursWorkedLabel, c1);
					
					weekOneAddMarkoutButton = new JButton("Add Markout");
					weekOneAddMarkoutButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							doAddMarkout(1);
						}
					});
					c1.gridx = 0;
					c1.gridy++;
				weekOnePanel.add(weekOneAddMarkoutButton, c1);
				
					weekOneMarkoutLabel = new JLabel("Markout: ");
					weekOneMarkoutLabel.setVisible(false);
					c1.gridx = 0;
					c1.gridy++;
				weekOnePanel.add(weekOneMarkoutLabel, c1);
				
					weekTwoTipsLabel = new JLabel("Tips: $0");
					c1.gridx = 0;
					c1.gridy++;
				weekOnePanel.add(weekTwoTipsLabel, c1);
					
				c.gridx = 0;
				c.gridy = 0;
			payPanel.add(weekOnePanel, c);
			
				JPanel weekTwoPanel = new JPanel(new GridBagLayout());
					weekTwoPanel.setBorder(BorderFactory.createTitledBorder("Week 2"));
					GridBagConstraints c2 = new GridBagConstraints();
				
					weekTwoHoursWorkedLabel = new JLabel("Hours Worked: 00");
					c2.gridx = 0;
					c2.gridy = 0;
				weekTwoPanel.add(weekTwoHoursWorkedLabel, c2);
				
					weekTwoAddMarkoutButton = new JButton("Add Markout");
					weekTwoAddMarkoutButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							doAddMarkout(2);
						}
					});
					c2.gridx = 0;
					c2.gridy++;
				weekTwoPanel.add(weekTwoAddMarkoutButton, c2);
				
					weekTwoMarkoutLabel = new JLabel("Markout: ");
					weekTwoMarkoutLabel.setVisible(false);
					c2.gridx = 0;
					c2.gridy++;
				weekTwoPanel.add(weekTwoMarkoutLabel, c2);
				
					weekTwoTipsLabel = new JLabel("Tips: $0");
					c2.gridx = 0;
					c2.gridy++;
				weekTwoPanel.add(weekTwoTipsLabel, c2);
			
				c.gridx = 0;
				c.gridy = 1;
			payPanel.add(weekTwoPanel, c);
			
				grossPayLabel = new JLabel("Gross Pay: $0");
			
				c.gridx = 0;
				c.gridy = 2;
			payPanel.add(grossPayLabel, c);
			
				JPanel filler = new JPanel(new BorderLayout());
				
				c.gridx = 0;
				c.gridy++;
				c.fill = GridBagConstraints.VERTICAL;
				c.weighty = 1;
			payPanel.add(filler, c);	
			
		mainFrame.add(payPanel, BorderLayout.EAST);
		
		loadTimeclockData();
		
		mainFrame.setVisible(true);
	}
	
	private void createHoursTable() {
		String[] headers = {"Date", "Day", "Start", "End", "Hours" };
		hoursTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		hoursTableModel.setColumnIdentifiers(headers);
		hoursTableModel.addTableModelListener(this);
		hoursTable = new JTable();
		hoursTable.setModel(hoursTableModel);
		hoursTable.setShowGrid(false);
		hoursTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					tableDoubleClicked(e);
			}
		});
		//hoursTable.setBackground(hoursPanel.getBackground());
	}
	
	private void loadTimeclockData() {
		// Remove listener to avoid tracking automated table changes
		hoursTableModel.removeTableModelListener(this);
		hoursTableModel.setRowCount(0);
		LinkedList<Workday> workdays = timeclock.getWorkdays();
		for (int i = 0; i < workdays.size(); i++) {
			Workday w = workdays.get(i);
			if (w.hasMultipleShifts()) { 
				LinkedList<Timespan> shifts = w.getSchedule();
				hoursTableModel.addRow(new Object[] {w.getDateString(), w.getDayOfWeek(), w.getStartFormatted(), w.getEndFormatted(), shifts.getFirst().getLength()});
				for (int j = 1; j < shifts.size(); j++)
					hoursTableModel.addRow(new Object[] {"", "", shifts.get(j).getStartFormatted(), shifts.get(j).getEndFormatted(), shifts.get(j).getLength()});
			} else if (w.hasShifts()) {
				hoursTableModel.addRow(new Object[] {w.getDateString(), w.getDayOfWeek(), w.getStartFormatted(), w.getEndFormatted(), w.getHours()});
			} else  {
				hoursTableModel.addRow(new Object[] {w.getDateString(), w.getDayOfWeek(), "", "", ""});
			}
			
		}
		grossPayLabel.setText("Gross Pay: $" + timeclock.getGrossPay());
		weekOneHoursWorkedLabel.setText("Hours Worked: " + timeclock.getWeekOneHoursWorked());
		weekTwoHoursWorkedLabel.setText("Hours Worked: " + timeclock.getWeekTwoHoursWorked());
		payPeriodLabel.setText(timeclock.getCurrentStartString() + " to " + timeclock.getCurrentEndString()); 
		
		String wk1Markout = timeclock.getWeekOneMarkout();
		if (wk1Markout != null) {
			weekOneMarkoutLabel.setText("Markout: " + wk1Markout);
			weekOneMarkoutLabel.setVisible(true);
			weekOneAddMarkoutButton.setVisible(false);
		} else {
			weekOneMarkoutLabel.setVisible(false);
			weekOneAddMarkoutButton.setVisible(true);
		}
		
		String wk2Markout = timeclock.getWeekTwoMarkout();
		if (wk2Markout != null) {
			weekTwoMarkoutLabel.setText("Markout: " + wk2Markout);
			weekTwoMarkoutLabel.setVisible(true);
			weekTwoAddMarkoutButton.setVisible(false);
		}
		else {
			weekTwoMarkoutLabel.setVisible(false);
			weekTwoAddMarkoutButton.setVisible(true);
		}
		//Re-add listener (future changed are assumed to have been made by the user)
		hoursTableModel.addTableModelListener(this);
	}
	
	private void doAddMarkout(int forWeek) {
		String markout = JOptionPane.showInputDialog("Enter a markout: ");
		if (markout == null || markout.equals(""))
			return;
		
		JLabel label;
		JButton button;
		switch (forWeek) {
		case 1:
			label   = weekOneMarkoutLabel;
			button  = weekOneAddMarkoutButton;
			timeclock.setWeekOneMarkout(markout);
			break;
		case 2:
			label   = weekTwoMarkoutLabel;
			button  = weekTwoAddMarkoutButton;
			timeclock.setWeekTwoMarkout(markout);
			break;
		default:
			return;
		}
		
		label.setText("Markout: " + markout);
		label.setVisible(true);
		button.setVisible(false);
	}
	
	private void moveToNextPayPeriod() {
		timeclock.moveToNextPayPeriod();
		loadTimeclockData();
	}
	
	private void moveToPreviousPayPeriod() {
		timeclock.moveToPreviousPayPeriod();
		loadTimeclockData();
	}
	
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel) e.getSource();
        String columnName = model.getColumnName(column);
        if (!columnName.equals("Start") && !columnName.equals("End"))
        	return;
        String data = model.getValueAt(row, column).toString();
        if (!Timespan.isValidTime(data)) {
        	model.removeTableModelListener(this);
        	model.setValueAt("", row, column);
        	model.addTableModelListener(this);
        	return;
        }
        
        //Check if full timespan has been entered
        String otherData;
        if (column == START_COLUMN)
        	otherData = model.getValueAt(row, END_COLUMN).toString();
        else
        	otherData = model.getValueAt(row, START_COLUMN).toString();
        if (otherData.equals(""))
        	return;
        
        //Create Timespan object and calculate length
        double dataDouble = Double.parseDouble(data);
        double otherDouble = Double.parseDouble(otherData);
        double start = Math.min(dataDouble, otherDouble);
        double end = Math.max(dataDouble, otherDouble);
        Timespan timespan = new Timespan(start, end);
        model.removeTableModelListener(this);
        model.setValueAt(timespan.getLength(), row, HOURS_COLUMN);
        model.setValueAt(timespan.getStartFormatted(), row, START_COLUMN);
        model.setValueAt(timespan.getEndFormatted(), row, END_COLUMN);
        model.addTableModelListener(this); 
        
        //Add or update information in the associated Workday object
        //TODO: For now, assume data is a new entry
        System.out.println(model.getValueAt(row, DATE_COLUMN).toString());
        SimpleDate dateOfChange = new SimpleDate(model.getValueAt(row, DATE_COLUMN).toString());
        Workday updatedWorkday = new Workday(dateOfChange);
        updatedWorkday.addHours(timespan);
        timeclock.getCurrentPayPeriod().updateWorkday(updatedWorkday);
	}
	
	
	/** 
	 * This method is invoked when the user double clicks on the table,
	 * which signifies that she wishes to edit the details of a Workday.
	 * The GUI responds by switching away from the HoursTableManager and to the
	 * HoursEditingPanel, which will be displayed until the user finishes 
	 * entering data. The GUI is informed of this by way of an ActionEvent,
	 * which is handled in the actionPerformed method, below.
	 * @param e
	 */
	public void tableDoubleClicked(MouseEvent e) {
		Object src = e.getSource();
		if (src == hoursTable) {
			// Tell the editing panel which workday has been selected		
			int selectedRow = hoursTable.getSelectedRow();
			Object selectedTime = null;
			while (selectedTime == null || selectedTime.toString().equals(""))
				selectedTime = hoursTableModel.getValueAt(selectedRow--, DATE_COLUMN);
			SimpleDate date = new SimpleDate(selectedTime.toString());
			Workday w = timeclock.getWorkdayFromDate(date);
			hoursEditingPanel.setWorkday(w);
			
			CardLayout layout = (CardLayout) payDisplayPanel.getLayout();
			layout.removeLayoutComponent(hoursEditingPanel);
			layout.addLayoutComponent(hoursEditingPanel, "editingPanel");
			layout.show(payDisplayPanel, "editingPanel");
		} 
	}
	
	/**
	 * This method is invoked by the HoursEditingPanel to inform
	 * the GUI that the user has finished entering input. The GUI
	 * is to respond by switching away from the HoursEditingPanel
	 * and by processing data entered. 
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == hoursEditingPanel) {
			switch (e.getID()) {
			case HoursEditingPanel.UPDATE_RESULT:
				// Update the workday with the new information
				Workday updatedWorkday = hoursEditingPanel.getWorkday();
				timeclock.getCurrentPayPeriod().updateWorkday(updatedWorkday);
				loadTimeclockData();
				break;
			case HoursEditingPanel.CANCEL_RESULT:
				// Do stuff
				break;
			}
			CardLayout layout = (CardLayout) payDisplayPanel.getLayout();
			layout.show(payDisplayPanel, "gridPanel");
		}
	}

	
	private void saveAndClose() {
		timeclock.saveToFile();
		
		//For each PayPeriod, determine if changes have been made and, if so, save them
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignore) {}
		new TimeclockGUI();
	}

}

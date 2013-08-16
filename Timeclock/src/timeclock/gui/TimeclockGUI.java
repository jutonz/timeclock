package timeclock.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import timeclock.core.PayPeriod;
import timeclock.core.SimpleDate;
import timeclock.core.Timeclock;
import timeclock.core.Timespan;
import timeclock.core.Workday;

public class TimeclockGUI implements TableModelListener, ChangeListener {
	
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
		private JPanel hoursEditingPanel;
			private Hashtable<Integer, JLabel> sliderLabelTable;
			private JSlider startSlider;
			private JLabel startSliderPositionLabel;
			private JSlider endSlider;
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
				
					hoursEditingPanel = new JPanel(new BorderLayout());
					hoursEditingPanel.setBorder(BorderFactory.createTitledBorder("Edit Scheduled Hours"));
					
						createSliderLabelTable();
					
						startSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, 20);
						startSlider.setName("startSlider");
						startSlider.addChangeListener(this);
						startSlider.setLabelTable(sliderLabelTable);
						startSlider.setMajorTickSpacing(10);
						startSlider.setMinorTickSpacing(5);
						startSlider.setPaintTicks(true);
						startSlider.setPaintLabels(true);
						startSlider.setSnapToTicks(true);
					
					hoursEditingPanel.add(startSlider, BorderLayout.CENTER);
						
						startSliderPositionLabel = new JLabel("0:00");
						
					hoursEditingPanel.add(startSliderPositionLabel, BorderLayout.NORTH);
						
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
//				JPanel breakdownPanel = new JPanel();
//					grossPay = "0";
//					String[][] data = new String[][] {
////							{"Gross Pay:", "$xxx"},
////							{"Federal Income:", "$xxx"},
////							{"State Income:", "$xxx"},
////							{"Social Security:", "$xxx"},
////							{"Medicare:", "$xxx"},
//							{"Gross Pay:", "$" + grossPay}
//						};
//					String[] headers = {"", ""};
//					JTable taxTable = new JTable(data, headers);
//					taxTable.setShowGrid(false);
//					taxTable.getTableHeader().setVisible(false);
//					taxTable.setBackground(payPanel.getBackground());
//					DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//					rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
//					taxTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
//					taxTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//						private static final long serialVersionUID = 1L;
//						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//							JLabel parent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//							if (row == table.getRowCount() - 1) 
//								parent.setFont(parent.getFont().deriveFont(Font.BOLD)); 
//							return parent;  
//						}
//					});
//					
//				breakdownPanel.add(taxTable);
//					
//				c.gridx = 0;
//				c.gridy = 2;
//			payPanel.add(breakdownPanel, c);
//			
//				String netPay = "$xxxx";
//				
//				JLabel netPayLabel = new JLabel("Net Pay: " + netPay);
//				Font defaultFont = netPayLabel.getFont();
//				netPayLabel.setFont(new Font(defaultFont.getName(), defaultFont.getStyle(), 14));
//				
//				c.gridx = 0;
//				c.gridy = 3;
//			payPanel.add(netPayLabel, c);
//		
//			payPanel = new JPanel(new GridBagLayout());
//			payPanel.setBorder(BorderFactory.createTitledBorder("Pay Breakdown"));
//			
//				GridBagConstraints c = new GridBagConstraints();
//			
//				String[][] data = new String[][] {
//						{"Week 1:", "xx hours"},
//						{"Week 2:", "xx hours"},
//						{"Total:" , "xx hours"},
//						{""       , ""}					
//				};
//				String[] headers = new String[] { "", "" };
//				JTable weeklyHoursTable = new JTable(data, headers);
//				weeklyHoursTable.setShowGrid(false);
//				weeklyHoursTable.getTableHeader().setVisible(false);
//				weeklyHoursTable.setBackground(payPanel.getBackground());
//				DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//				rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
//				weeklyHoursTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
//			
//				c.gridx = 0;
//				c.gridy = 0;
//			payPanel.add(weeklyHoursTable, c);
//			
//				data = new String[][] {
//					{"Gross Pay:", "$xxx"},
//					{"Federal Income:", "$xxx"},
//					{"State Income:", "$xxx"},
//					{"Social Security:", "$xxx"},
//					{"Medicare:", "$xxx"}		
//				};
//				JTable taxTable = new JTable(data, headers);
//				taxTable.setShowGrid(false);
//				taxTable.getTableHeader().setVisible(false);
//				taxTable.setBackground(payPanel.getBackground());
//				taxTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
//				
//				c.gridx = 0;
//				c.gridy = 1;
//			payPanel.add(taxTable, c);			
			
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
	
	private void createSliderLabelTable() {
		sliderLabelTable = new Hashtable<Integer, JLabel>();
		sliderLabelTable.put(0, new JLabel("5:00"));
		sliderLabelTable.put(20, new JLabel("7:00"));
		sliderLabelTable.put(40, new JLabel("9:00"));
		sliderLabelTable.put(60, new JLabel("11:00"));
		sliderLabelTable.put(80, new JLabel("1:00"));
		sliderLabelTable.put(100, new JLabel("3:00"));
		sliderLabelTable.put(120, new JLabel("5:00"));
		sliderLabelTable.put(140, new JLabel("7:00"));
		sliderLabelTable.put(160, new JLabel("9:00"));
		sliderLabelTable.put(180, new JLabel("11:00"));
	}
	
	private void loadTimeclockData() {
		//Remove listener to avoid tracking automated table changes
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
	
	public void tableDoubleClicked(MouseEvent e) {
		CardLayout layout = (CardLayout) payDisplayPanel.getLayout();
		layout.show(payDisplayPanel, "editingPanel");
		String s = hoursTableModel.getValueAt(hoursTable.getSelectedRow(), DAY_COLUMN).toString();
	}
	
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (source.getName().equals("startSlider"))
			source = startSlider;
		if (!source.getValueIsAdjusting()) {
			int pos = (int) source.getValue();
			String time = getTimeFromSliderPos(pos);
			startSliderPositionLabel.setText(time);
		}
	}
	
	public String getTimeFromSliderPos(int pos) {
		String ret = "";
		int hour;
		int minute;		
		if (pos < 80) {
			//If before 1:00 
			hour = 5 + (pos / 10);
			minute = (pos % 10 == 0) ? 0 : 30;
		} else {
			//If after 1:00
			hour = (pos - 70) / 10;
			minute = (pos % 10 == 0) ? 0 : 30;
		}
		ret += hour + ":";
		ret += (minute == 0) ? "00" : "30";
		return ret;
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

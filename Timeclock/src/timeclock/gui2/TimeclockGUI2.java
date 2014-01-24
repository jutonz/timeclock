package timeclock.gui2;

import timeclock.core.Timeclock;
import timeclock.gui2.hoursTable.HoursTableManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

/**
 * Author: Justin Toniazzo
 * Date:   24 January 2014
 */
public class TimeclockGUI2 {

    private Timeclock timeclock;
    private HoursTableManager hoursTableManager;

    private JFrame frame;
    private JPanel panel1;
    private JPanel infoPanel;
    private JPanel week1Panel;
    private JPanel week2Panel;
    private JButton addMarkoutWeek1Button;
    private JPanel displayPanel;
    private JPanel scheduledHoursPanel;
    private JButton previousPayPeriodButton;
    private JPanel navigationPanel;
    private JButton nextPayPeriodButton;
    private JLabel week2HoursWorkedLabel;
    private JButton addMarkoutWeek2Button1;
    private JTable hoursTable;
    private JLabel grossPayLabel;
    private JLabel week1HoursWorkedLabel;
    private JLabel payPeriodLabel;

    public TimeclockGUI2() {
        frame = new JFrame("TimeclockGUI2");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveFrameData();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                loadFrameData();
            }
        });

        // Load model data.
        loadTimeclockData();

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Saves information about the frame (such as
     * current position, size, etc) to file so that
     * it may be preserved between sessions.
     */
    public void saveFrameData() {
        // Create preferences getter.
        Preferences prefs = Preferences.userNodeForPackage(getClass());

        // Save window location.
        prefs.putDouble(FrameData.Location.X.NAME, frame.getLocation().getX());
        prefs.putDouble(FrameData.Location.Y.NAME, frame.getLocation().getY());

        // Save window size.
        prefs.put(FrameData.Size.Height.NAME, frame.getWidth() + "");
        prefs.put(FrameData.Size.Width.NAME, frame.getHeight() + "");
    }

    /**
     * Loads information about the frame's position,
     * size, etc from file so that it is preserved
     * between sessions.
     */
    public void loadFrameData() {
        // Create preferences getter.
        Preferences prefs = Preferences.userNodeForPackage(getClass());

        // Load window location.
        int x = (int) prefs.getDouble(FrameData.Location.X.NAME, FrameData.Location.X.DEFAULT);
        int y = (int) prefs.getDouble(FrameData.Location.Y.NAME, FrameData.Location.Y.DEFAULT);
        frame.setLocation(x, y);
    }

    private void loadTimeclockData() {
        grossPayLabel.setText("Gross Pay: $" + timeclock.getGrossPay());
        week1HoursWorkedLabel.setText("Hours Worked: " + timeclock.getWeekOneHoursWorked());
        week2HoursWorkedLabel.setText("Hours Worked: " + timeclock.getWeekTwoHoursWorked());
        payPeriodLabel.setText(timeclock.getCurrentStartString() + " to " + timeclock.getCurrentEndString());
    }

    private void createUIComponents() {
        // Load the model.
        timeclock = new Timeclock();
        hoursTableManager = new HoursTableManager();

        // Create the tables.
        hoursTableManager.populateWith(timeclock.getWorkdays());
        hoursTable = hoursTableManager.get();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}
        new TimeclockGUI2();
    }


}

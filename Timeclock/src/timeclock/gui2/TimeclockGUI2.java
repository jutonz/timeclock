package timeclock.gui2;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

/**
 * Author: Justin Toniazzo
 * Date:   24 January 2014
 */
public class TimeclockGUI2 {
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
    private JLabel week1HoursWorkedLabel;
    private JButton addMarkoutWeek2Button1;
    private JTable hoursTable;

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
        prefs.put(FrameData.Location.X.NAME, frame.getLocation().getX() + "");
        prefs.put(FrameData.Location.Y.NAME, frame.getLocation().getY() + "");

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
        int x = prefs.getInt(FrameData.Location.X.NAME, FrameData.Location.X.DEFAULT);
        int y = prefs.getInt(FrameData.Location.Y.NAME, FrameData.Location.Y.DEFAULT);
        frame.setLocation(x, y);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}
        new TimeclockGUI2();
    }


}

package timeclock.gui2.hoursTable;

import timeclock.core.Timespan;
import timeclock.core.Workday;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Justin Toniazzo
 * Date:   24 January 2014
 */
public class HoursTableManager {

    public static final String[] HEADERS = {"Date", "Day", "Start", "End", "Hours" };

    private JTable hoursTable;
    private DefaultTableModel hoursTableModel;

    public HoursTableManager() {
        init();
    }

    private void init() {
        hoursTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        hoursTableModel.setColumnIdentifiers(HEADERS);
//        hoursTableModel.addTableModelListener(this);
        hoursTable = new JTable();
        hoursTable.setModel(hoursTableModel);
        hoursTable.setShowGrid(false);
        hoursTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
//                    tableDoubleClicked(e);
                }
            }
        });
    }

    public JTable get() {
        return hoursTable;
    }

    public DefaultTableModel getModel() {
        return hoursTableModel;
    }

    public void populateWith(List<Workday> workdays) {
        for (Workday w : workdays) {
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
    }
}

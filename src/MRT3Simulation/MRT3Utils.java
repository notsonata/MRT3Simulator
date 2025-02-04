package MRT3Simulation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

public class MRT3Utils {

	public static String getValidatedTimeInput(JFrame frame, String currentTime, String title, String prompt) {
	    String timePattern = "^(?:[01]?[0-9]|2[0-3]):[0-5][0-9]$"; // Regular expression for HH:MM format
	    String time;

	    while (true) {
	        // Show the input dialog with the specified title and prompt
	        time = JOptionPane.showInputDialog(frame, prompt, title, JOptionPane.PLAIN_MESSAGE);

	        // If the user clicks Cancel, return null
	        if (time == null) {
	            return null;
	        }

	        // Validate the time format
	        if (time.matches(timePattern)) {
	            return time; // Return the valid time
	        } else {
	            // Show an error message for invalid input
	            JOptionPane.showMessageDialog(frame, "Invalid time format. Please enter in HH:MM format (00:00 - 23:59).", 
	                                          "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

    public static void filterTable(JTable table, DefaultTableModel tableModel, String query) {
        // Ensure the table has a RowSorter		
        if (table.getRowSorter() == null) {
            table.setRowSorter(new TableRowSorter<>(tableModel));
        }

        // Use a regex to match either arrivals or departures
        String regex = "(?i)" + Pattern.quote(query); // Case-insensitive search
        RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter(regex, 0, 1); // Filter by Arrivals (column 0) and Departures (column 1)
        ((TableRowSorter<DefaultTableModel>) table.getRowSorter()).setRowFilter(rowFilter);
    }

    public static void sortTable(JTable table, DefaultTableModel tableModel, String sortBy, String order) {
        // Ensure the table has a RowSorter
        if (table.getRowSorter() == null) {
            table.setRowSorter(new TableRowSorter<>(tableModel));
        }

        DefaultRowSorter<DefaultTableModel, ?> sorter = (DefaultRowSorter<DefaultTableModel, ?>) table.getRowSorter();
        sorter.setSortKeys(null); // Clear existing sort keys

        if (!sortBy.equals("Clear Sort")) {
            int columnIndex = sortBy.equals("Arrival Time") ? 3 : 4; // Column index for arrival or departure time
            SortOrder sortOrder = order.equals("Ascending") ? SortOrder.ASCENDING : SortOrder.DESCENDING;
            sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(columnIndex, sortOrder)));
        }
    }
}

package MRT3Simulation;

import javax.swing.*;
import java.awt.*;


public class MRT3SimulationApp {

    private MRT3DatabaseHandler dbHandler;
    private MRT3TableModel tableModel;
    private JTable table;
    private int currentIndex = 0; // For traversing schedules

    public MRT3SimulationApp() {
        dbHandler = new MRT3DatabaseHandler();
        dbHandler.createTable();

        tableModel = new MRT3TableModel();
        dbHandler.loadDataFromDatabase(tableModel);
    }

    public static void main(String[] args) {
        MRT3SimulationApp app = new MRT3SimulationApp();

        // Create the main frame
        JFrame frame = new JFrame("MRT-3 Train Scheduling Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setMinimumSize(new Dimension(800, 400)); 
        
        // Use BorderLayout for dynamic resizing
        frame.setLayout(new BorderLayout());

        // Create a panel to hold the table
        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        // Create a JTable with the table model
        app.table = new JTable(app.tableModel);

        // Disable column reordering
        app.table.getTableHeader().setReorderingAllowed(false);

        // Disable column format
        app.table.getTableHeader().setResizingAllowed(false);

        // Add the table to a scroll pane (to enable scrolling)
        JScrollPane scrollPane = new JScrollPane(app.table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create a search bar with placeholder text
        JTextField searchBar = new JTextField(20); // Set preferred width
        searchBar.setText("Search by Arrivals or Departures"); // Placeholder text
        searchBar.setForeground(Color.GRAY); // Set placeholder text color
        searchBar.setFont(searchBar.getFont().deriveFont(Font.ITALIC)); // Italicize placeholder text

        // Add focus listeners to handle placeholder text
        searchBar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchBar.getText().equals("Search by Arrivals or Departures")) {
                    searchBar.setText("");
                    searchBar.setForeground(Color.BLACK); // Set text color to black when typing
                    searchBar.setFont(searchBar.getFont().deriveFont(Font.PLAIN)); // Remove italic when typing
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchBar.getText().isEmpty()) {
                    searchBar.setText("Search by Arrivals or Departures");
                    searchBar.setForeground(Color.GRAY); // Set placeholder text color
                    searchBar.setFont(searchBar.getFont().deriveFont(Font.ITALIC)); // Italicize placeholder text
                    MRT3Utils.filterTable(app.table, app.tableModel, ""); // Restore original table data when focus is lost
                }
            }
        });

        // Add a document listener to filter the table dynamically
        searchBar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                MRT3Utils.filterTable(app.table, app.tableModel, searchBar.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                MRT3Utils.filterTable(app.table, app.tableModel, searchBar.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                MRT3Utils.filterTable(app.table, app.tableModel, searchBar.getText());
            }
        });

        // Create the sort dropdown with placeholder text
        JComboBox<String> sortDropdown = new JComboBox<>(new String[]{"Arrival Time Ascending", "Arrival Time Descending", "Departure Time Ascending", "Departure Time Descending", "Clear Sort"});
        sortDropdown.setSelectedIndex(-1); // No option selected by default

        // Set the preferred size of the dropdown
        sortDropdown.setPreferredSize(new Dimension(190, 20));

        // Set a custom renderer to display "Sort by" as placeholder text
        sortDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index == -1 && sortDropdown.getSelectedIndex() == -1) {
                    // Display "Sort by" as placeholder text when no option is selected
                    return super.getListCellRendererComponent(list, "Sort by", index, isSelected, cellHasFocus);
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Add an action listener to handle sorting and resetting the dropdown text
        sortDropdown.addActionListener(e -> {
            String selectedItem = (String) sortDropdown.getSelectedItem();
            if (selectedItem != null) {
                if (selectedItem.equals("Clear Sort")) {
                    // Reset the dropdown to display "Sort by"
                    sortDropdown.setSelectedIndex(-1);
                    MRT3Utils.sortTable(app.table, app.tableModel, "Clear Sort", ""); // Clear the sorting
                } else {
                    // Extract the sort criteria and order
                    String[] parts = selectedItem.split(" ");
                    if (parts.length >= 3) {
                        String sortBy = parts[0] + " " + parts[1]; // e.g., "Arrival Time"
                        String order = parts[2]; // e.g., "Ascending"
                        MRT3Utils.sortTable(app.table, app.tableModel, sortBy, order); // Apply the sorting
                    }
                }
            }
        });

        // Create a panel to center the search bar and sort dropdown
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.add(searchBar);
        searchPanel.add(sortDropdown);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Create buttons
        JButton addButton = new JButton("Add Schedule");
        JButton deleteButton = new JButton("Delete Schedule");
        JButton editButton = new JButton("Edit Schedule");
        JButton nextButton = new JButton("Next");
        JButton previousButton = new JButton("Previous");
        JButton detailsButton = new JButton("View Train Details");

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(detailsButton);

        // Add the button panel to the bottom of the main panel
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners for buttons
        addButton.addActionListener(e -> {
            // Show a dialog to input schedule details
            String arrivals = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Arrivals:",
                    "Add Arrivals",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    MRT3Stations.STATIONS,
                    MRT3Stations.STATIONS[0]
            );
            if (arrivals == null) {
                // User clicked Cancel, handle accordingly
                return;
            }

            String departures = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Departures:",
                    "Add Departures",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    MRT3Stations.STATIONS,
                    MRT3Stations.STATIONS[0]
            );
            if (departures == null) {
                // User clicked Cancel, handle accordingly
                return;
            }
            //Check Whether arrival and departure station is the same
            if (arrivals.equals(departures)) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Arrival and Departure stations cannot be the same. Please select different stations.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );
                return; // Exit the method without saving
            }
            

            String trainId = JOptionPane.showInputDialog(
                    frame,
                    "Enter Train ID:",
                    "Add Train ID",
                    JOptionPane.PLAIN_MESSAGE
            );

            while (trainId == null || trainId.trim().isEmpty()) {
                if (trainId == null) {
                    return; // Exit if the user cancels
                }
                JOptionPane.showMessageDialog(
                        frame,
                        "Train ID cannot be empty. Please enter a valid Train ID.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );
                trainId = JOptionPane.showInputDialog(
                        frame,
                        "Enter Train ID:",
                        "Add Train ID",
                        JOptionPane.PLAIN_MESSAGE
                );
            }

            String arrivalTime = MRT3Utils.getValidatedTimeInput(frame, null, "Add Arrival Time", "Enter Arrival Time (HH:MM):");
            if (arrivalTime == null) {
                // User clicked Cancel, handle accordingly
                return;
            }

            String departureTime = MRT3Utils.getValidatedTimeInput(frame, null, "Add Departure Time", "Enter Departure Time (HH:MM):");
            if (departureTime == null) {
                // User clicked Cancel, handle accordingly
                return;
            }

            String remarks = JOptionPane.showInputDialog(
                frame,
                "Enter Remarks:",
                "Add Remarks",
                JOptionPane.PLAIN_MESSAGE
            );

            if (arrivals != null && departures != null && trainId != null && arrivalTime != null && departureTime != null) {
                try {
                    app.dbHandler.saveDataToDatabase(arrivals, departures, trainId, arrivalTime, departureTime, remarks);
                    app.dbHandler.loadDataFromDatabase(app.tableModel); // Refresh the table
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Train ID already exists. Please enter a unique Train ID.",
                            "Duplicate Train ID",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
            

        deleteButton.addActionListener(e -> {
            int selectedRow = app.table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = app.table.convertRowIndexToModel(selectedRow); // Convert to model index
                int id = ((MRT3TableModel)app.tableModel).getHiddenId(modelRow);	

                int confirm = JOptionPane.showConfirmDialog(
                    frame, 
                    "Are you sure you want to delete this schedule?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    app.dbHandler.deleteDataFromDatabase(id); // Pass ID to delete from DB
                    app.dbHandler.loadDataFromDatabase(app.tableModel); // Refresh table
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to delete.", "Error Deleting Schedule", JOptionPane.ERROR_MESSAGE);
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = app.table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = app.table.convertRowIndexToModel(selectedRow); // Convert to model index

                // Get the current values from the selected row
                String currentArrivals = (String) app.tableModel.getValueAt(modelRow, 0); // Arrivals 
                String currentDepartures = (String) app.tableModel.getValueAt(modelRow, 1); // Departures 
                String currentTrainId = (String) app.tableModel.getValueAt(modelRow, 2); // Train ID 
                String currentArrivalTime = (String) app.tableModel.getValueAt(modelRow, 3); // Arrival Time 
                String currentDepartureTime = (String) app.tableModel.getValueAt(modelRow, 4); // Departure Time 
                String currentRemarks = (String) app.tableModel.getValueAt(modelRow, 5); // Remarks 

                // Prompt the user to edit values
                String newArrivals = (String) JOptionPane.showInputDialog(
                        frame,
                        "Edit Arrivals:",
                        "Edit Arrivals",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        MRT3Stations.STATIONS,
                        currentArrivals
                );
                if (newArrivals == null) {
                    // User clicked Cancel, handle accordingly
                    return;
                }

                String newDepartures = (String) JOptionPane.showInputDialog(
                        frame,
                        "Edit Departures:",
                        "Edit Departures",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        MRT3Stations.STATIONS,
                        currentDepartures
                );
                if (newDepartures == null) {
                    // User clicked Cancel, handle accordingly
                    return;
                }
                
                // Check if arrival and departure stations are the same
                if (newArrivals.equals(newDepartures)) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Arrival and Departure stations cannot be the same. Please select different stations.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return; // Exit the method without updating
                }

                String newTrainId = JOptionPane.showInputDialog(
                        frame,
                        "Edit Train ID:",
                        currentTrainId
                );
                if (newTrainId == null) {
                    // User clicked Cancel, handle accordingly
                    return;
                }

                while (newTrainId == null || newTrainId.trim().isEmpty()) {
                    if (newTrainId == null) {
                    	// User clicked Cancel, handle accordingly
                        return;
                    }
                    JOptionPane.showMessageDialog(
                            frame,
                            "Train ID cannot be empty. Please enter a valid Train ID.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                    newTrainId = JOptionPane.showInputDialog(
                            frame,
                            "Enter Train ID:",
                            "Edit Train ID",
                            JOptionPane.PLAIN_MESSAGE
                    );
                }

                String newArrivalTime = MRT3Utils.getValidatedTimeInput(frame, currentArrivalTime, "Edit Arrival Time", "Edit Arrival Time (HH:MM):");
                if (newArrivalTime == null) {
                    // User clicked Cancel, handle accordingly
                    return;
                }

                String newDepartureTime = MRT3Utils.getValidatedTimeInput(frame, currentDepartureTime, "Edit Departure Time", "Edit Departure Time (HH:MM):");
                if (newDepartureTime == null) {
                    // User clicked Cancel, handle accordingly
                    return;
                }

                String newRemarks = JOptionPane.showInputDialog(
                    frame,
                    "Edit Remarks:",
                    currentRemarks
                );

                if (newArrivals != null && newDepartures != null && newTrainId != null && newArrivalTime != null && newDepartureTime != null) {
                    try {
                        int id = ((MRT3TableModel) app.tableModel).getHiddenId(modelRow); // Hidden ID rows
                        app.dbHandler.updateDataInDatabase(id, newArrivals, newDepartures, newTrainId, newArrivalTime, newDepartureTime, newRemarks); // Call the update method in the database handler
                        app.dbHandler.loadDataFromDatabase(app.tableModel); // Refresh the table model after update
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Train ID already exists. Please enter a unique Train ID.",
                                "Duplicate Train ID",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to edit.", "Error Editing Schedule", JOptionPane.ERROR_MESSAGE);
            }
        });


        nextButton.addActionListener(e -> {
            if (app.currentIndex < app.tableModel.getRowCount() - 1) {
                app.currentIndex++;
                app.table.setRowSelectionInterval(app.currentIndex, app.currentIndex);
            }
        }); 

        previousButton.addActionListener(e -> {
            if (app.currentIndex > 0) {
                app.currentIndex--;
                app.table.setRowSelectionInterval(app.currentIndex, app.currentIndex);
            }
        });

        detailsButton.addActionListener(e -> {
            int selectedRow = app.table.getSelectedRow();
            
            if (selectedRow >= 0) {
                String details = "Train ID: " + app.tableModel.getValueAt(selectedRow, 2) + "\n"
                        + "Arrivals: " + app.tableModel.getValueAt(selectedRow, 0) + "\n"
                        + "Departures: " + app.tableModel.getValueAt(selectedRow, 1) + "\n"
                        + "Arrival Time: " + app.tableModel.getValueAt(selectedRow, 3) + "\n"
                        + "Departure Time: " + app.tableModel.getValueAt(selectedRow, 4) + "\n"
                        + "Remarks: " + app.tableModel.getValueAt(selectedRow, 5);
                JOptionPane.showMessageDialog(frame, details, "Train Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to view details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Make the frame visible
        frame.setVisible(true);
        app.table.requestFocusInWindow();
    }
}
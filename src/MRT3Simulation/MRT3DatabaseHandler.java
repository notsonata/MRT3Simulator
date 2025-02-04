package MRT3Simulation;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class MRT3DatabaseHandler {
	private static final String URL = "jdbc:sqlite:mrt3_schedule.db";

	    public void createTable() {
	    	
	        String sql = "CREATE TABLE IF NOT EXISTS mrt3_schedules (\n"
	                   + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
	                   + " arrivals TEXT NOT NULL,\n"
	                   + " departures TEXT NOT NULL,\n"
	                   + " train_id TEXT NOT NULL,\n"
	                   + " arrival_time TEXT NOT NULL,\n"
	                   + " departure_time TEXT NOT NULL,\n"
	                   + " remarks TEXT\n"
	                   + ");";

	        try (Connection connection = DriverManager.getConnection(URL);
	             Statement statement = connection.createStatement()) {

	            statement.execute(sql);
	            System.out.println("Table created successfully.");

	        } catch (SQLException e) {
	            System.out.println("Error creating table: " + e.getMessage());
	        }
	    }

	    public void loadDataFromDatabase(DefaultTableModel tableModel) {
	        try (Connection connection = DriverManager.getConnection(URL);
	             Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery("SELECT * FROM mrt3_schedules")) {

	            // Clear existing data
	            tableModel.setRowCount(0);

	            // Load data from the database
	            while (resultSet.next()) {
	                int id = resultSet.getInt("id"); // Fetch ID as an int
	                String arrivals = resultSet.getString("arrivals");
	                String departures = resultSet.getString("departures");
	                String trainId = resultSet.getString("train_id");
	                String arrivalTime = resultSet.getString("arrival_time");
	                String departureTime = resultSet.getString("departure_time");
	                String remarks = resultSet.getString("remarks");
	                tableModel.addRow(new Object[]{arrivals, departures, trainId, arrivalTime, departureTime, remarks});
	                
	                // Store ID in a separate list or custom table model
	                ((MRT3TableModel)tableModel).addHiddenId(id);
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void saveDataToDatabase(String arrivals, String departures, String trainId, String arrivalTime, String departureTime, String remarks) {
	        String sql = "INSERT INTO mrt3_schedules(arrivals, departures, train_id, arrival_time, departure_time, remarks) VALUES(?,?,?,?,?,?)";

	        try (Connection connection = DriverManager.getConnection(URL);
	             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

	            preparedStatement.setString(1, arrivals);
	            preparedStatement.setString(2, departures);
	            preparedStatement.setString(3, trainId);
	            preparedStatement.setString(4, arrivalTime);
	            preparedStatement.setString(5, departureTime);
	            preparedStatement.setString(6, remarks);
	            preparedStatement.executeUpdate();

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void deleteDataFromDatabase(int id) {
	        String sql = "DELETE FROM mrt3_schedules WHERE id = ?";

	        try (Connection connection = DriverManager.getConnection(URL);
	             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

	            preparedStatement.setInt(1, id);
	            preparedStatement.executeUpdate();

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void updateDataInDatabase(int id, String arrivals, String departures, String trainId, String arrivalTime, String departureTime, String remarks) {
	        String sql = "UPDATE mrt3_schedules SET arrivals = ?, departures = ?, train_id = ?, arrival_time = ?, departure_time = ?, remarks = ? WHERE id = ?";

	        try (Connection connection = DriverManager.getConnection(URL);
	             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            // Set the parameters for the prepared statement
	            preparedStatement.setString(1, arrivals);
	            preparedStatement.setString(2, departures);
	            preparedStatement.setString(3, trainId);
	            preparedStatement.setString(4, arrivalTime);
	            preparedStatement.setString(5, departureTime);	
	            preparedStatement.setString(6, remarks);
	            preparedStatement.setInt(7, id);

	            // Log the SQL execution for debugging
	            System.out.println("Executing update: " + preparedStatement.toString());

	            // Execute the update
	            preparedStatement.executeUpdate();
	            System.out.println("Schedule updated successfully!");

	        } catch (SQLException e) {
	            // Catch and log any SQL exceptions
	            System.out.println("Error updating schedule: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	    
	    

}

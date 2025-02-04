package MRT3Simulation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class MRT3TableModel extends DefaultTableModel{
	
    public MRT3TableModel() {
        super(new String[]{"Arrivals", "Departures", "Train ID", "Arrival Time", "Departure Time", "Remarks"}, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // All cells are non-editable
    }
    
    private List<Integer> hiddenIds = new ArrayList<>();
    
    public void addHiddenId(int id) {
        hiddenIds.add(id);
    }
    
    public int getHiddenId(int row) {
        return hiddenIds.get(row);
    }
    
    public void clearHiddenIds() {
        hiddenIds.clear();
    }
}


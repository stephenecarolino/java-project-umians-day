package PHARMACY;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class PharmacyApp extends JFrame {

    // --- 1. MODEL (Encapsulation & Inheritance) ---
    static class Medicine {
        private String id, name, type, price, stock, status, loc, desc;

        public Medicine(String id, String name, String type, String price, String stock, String status, String loc, String desc) {
            this.id = id; 
            this.name = name; 
            this.type = type; 
            this.price = price;
            this.stock = stock; 
            this.status = status;
            this.loc = loc; 
            this.desc = desc;
        }

        @Override
        public String toString() { 
            return id + "|" + name + "|" + type + "|" + price + "|" + stock + "|" + status + "|" + loc + "|" + desc;
        }

        // Getters for the Table
        public String[] toArray() { return new String[]{id, name, type, price, stock, status, loc, desc}; }
        public String getId() { return id; }
        public String getName() { return name; }
    }


    // --- 2. VIEW (GUI Elements) ---
    private JTextField txtID = new JTextField(), txtName = new JTextField(), txtPrice = new JTextField(), 
                       txtStock = new JTextField(), txtLoc = new JTextField(), txtDesc = new JTextField(),
                       txtSearch = new JTextField(15);
    private JComboBox<String> cbType = new JComboBox<>(new String[]{"Tablet", "Syrup", "Capsule"}),
                              cbStatus = new JComboBox<>(new String[]{"In Stock", "Out of Stock"});
    private DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Price", "Stock", "Status", "Location", "Description"}, 0);
    private JTable table = new JTable(model);

    public PharmacyApp() {
        setTitle("Pharmacy Inventory System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // TOP PANEL: Input Fields (Asset Details style)
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Medicine Details"));
        inputPanel.add(new JLabel(" Medicine ID:")); inputPanel.add(txtID);
        inputPanel.add(new JLabel(" Brand Name:")); inputPanel.add(txtName);
        inputPanel.add(new JLabel(" Type:")); inputPanel.add(cbType);
        inputPanel.add(new JLabel(" Price:")); inputPanel.add(txtPrice);
        inputPanel.add(new JLabel(" Stock:")); inputPanel.add(txtStock);
        inputPanel.add(new JLabel(" Status:")); inputPanel.add(cbStatus);
        inputPanel.add(new JLabel(" Location:")); inputPanel.add(txtLoc);
        inputPanel.add(new JLabel(" Description:")); inputPanel.add(txtDesc);
        add(inputPanel, BorderLayout.NORTH);

        // CENTER PANEL: Table (Asset Records style)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Inventory Records"));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // BOTTOM PANEL: Search & Actions
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search (ID/Name):")); searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Search"), btnShowAll = new JButton("Show All");
        searchPanel.add(btnSearch); searchPanel.add(btnShowAll);

        JPanel actionPanel = new JPanel();
        JButton btnAdd = new JButton("Add Medicine"), btnUpdate = new JButton("Update"), 
                btnDelete = new JButton("Delete"), btnClear = new JButton("Clear Form");
        actionPanel.add(btnAdd); actionPanel.add(btnUpdate); actionPanel.add(btnDelete); actionPanel.add(btnClear);

        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- BUTTON LOGIC ---
        btnAdd.addActionListener(e -> {
            Medicine m = new Medicine(txtID.getText(), txtName.getText(), cbType.getSelectedItem().toString(), 
                                      txtPrice.getText(), txtStock.getText(), cbStatus.getSelectedItem().toString(), 
                                      txtLoc.getText(), txtDesc.getText());
            List<Medicine> list = loadFromFile();
            list.add(m);
            saveToFile(list);
            refreshTable(list);
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String id = model.getValueAt(row, 0).toString();
                List<Medicine> list = loadFromFile();
                list.removeIf(m -> m.getId().equals(id));
                saveToFile(list);
                refreshTable(list);
            }
        });

        btnShowAll.addActionListener(e -> refreshTable(loadFromFile()));
        btnClear.addActionListener(e -> { txtID.setText(""); txtName.setText(""); txtSearch.setText(""); });

        refreshTable(loadFromFile());
        setVisible(true);
    }
    
    // --- 3. CONTROLLER (File Handling CRUD) ---
    private final String FILE_NAME = "inventory.txt";

    private void saveToFile(List<Medicine> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Medicine m : list) pw.println(m.toString());
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Save Error!"); }
    }

    private List<Medicine> loadFromFile() {
        List<Medicine> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                list.add(new Medicine(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]));
            }
        } catch (IOException e) { /* New file */ }
        return list;
    }

    private void refreshTable(List<Medicine> list) {
        model.setRowCount(0);
        for (Medicine m : list) model.addRow(m.toArray());
    }

    public static void main(String[] args) { new PharmacyApp(); }
}
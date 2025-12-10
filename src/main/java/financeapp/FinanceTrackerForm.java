package financeapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.util.ArrayList;

public class FinanceTrackerForm {

    private JPanel mainPanel;
    private JLabel naslov;
    private JLabel text1;
    private JTextField amountField;
    private JLabel text2;
    private JTextField descriptionField;
    private JComboBox<String> typeCombo;
    private JComboBox<String> categoryCombo;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton exportButton;
    private JButton addButton;
    private JTable transactionTable;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;

    private TransactionManager manager;

    private Object selectedID = null;

    public FinanceTrackerForm() {

        manager = new TransactionManager();
        loadDataIntoTable();
        updateSummary();

        addButton.addActionListener(e -> addNewTransaction());
        updateButton.addActionListener(e -> updateSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        exportButton.addActionListener(e -> exportData());

        transactionTable.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());
    }

    private void addNewTransaction() {
        try {
            String type = (String) typeCombo.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();
            String category = (String) categoryCombo.getSelectedItem();

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Opis ne može biti prazan!");
                return;
            }

            Transaction t = new Transaction(type, amount, description, category);
            manager.addTransaction(t);

            loadDataIntoTable();
            updateSummary();

            amountField.setText("");
            descriptionField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška: " + ex.getMessage());
        }
    }

    private void loadSelectedRow() {

        int row = transactionTable.getSelectedRow();
        if (row == -1) return;

        selectedID = transactionTable.getModel().getValueAt(row, 0);

        String type = (String) transactionTable.getValueAt(row, 1);
        double amount = Double.parseDouble(transactionTable.getValueAt(row, 2).toString());
        String desc = (String) transactionTable.getValueAt(row, 3);
        String category = (String) transactionTable.getValueAt(row, 4);

        typeCombo.setSelectedItem(type);
        amountField.setText(String.valueOf(amount));
        descriptionField.setText(desc);
        categoryCombo.setSelectedItem(category);
    }

    private void updateSelected() {
        try {
            if (selectedID == null) {
                JOptionPane.showMessageDialog(null, "Niste odabrali transakciju!");
                return;
            }

            Transaction t = new Transaction(
                    (org.bson.types.ObjectId) selectedID,
                    (String) typeCombo.getSelectedItem(),
                    Double.parseDouble(amountField.getText()),
                    descriptionField.getText(),
                    (String) categoryCombo.getSelectedItem()
            );

            manager.updateTransaction(t);

            loadDataIntoTable();
            updateSummary();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška kod ažuriranja!");
        }
    }

    private void deleteSelected() {

        int row = transactionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Odaberite transakciju!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Jeste li sigurni da želite izbrisati ovu transakciju?",
                "Potvrda",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Object id = transactionTable.getModel().getValueAt(row, 0);
            manager.deleteTransaction((org.bson.types.ObjectId) id);
            loadDataIntoTable();
            updateSummary();
        }
    }

    private void exportData() {

        try {
            FileWriter fw = new FileWriter("finansije_export.txt");

            double income = manager.getTotalIncome();
            double expense = manager.getTotalExpense();
            double balance = income - expense;

            fw.write("Ukupni prihod: " + income + "\n");
            fw.write("Ukupni rashod: " + expense + "\n");
            fw.write("Stanje: " + balance + "\n\n");

            fw.write("Rashodi po kategorijama:\n");
            fw.write("Hrana: " + manager.getCategoryTotal("hrana") + "\n");
            fw.write("Prevoz: " + manager.getCategoryTotal("prevoz") + "\n");
            fw.write("Zabava: " + manager.getCategoryTotal("zabava") + "\n");
            fw.write("Racuni: " + manager.getCategoryTotal("racuni") + "\n");

            fw.close();

            JOptionPane.showMessageDialog(null, "Export uspješan!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška pri exportu!");
        }
    }

    private void loadDataIntoTable() {

        ArrayList<Transaction> list = manager.getAllTransactions();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Vrsta");
        model.addColumn("Iznos");
        model.addColumn("Opis");
        model.addColumn("Kategorija");

        for (Transaction t : list) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getType(),
                    t.getAmount(),
                    t.getDescription(),
                    t.getCategory()
            });
        }

        transactionTable.setModel(model);
    }

    private void updateSummary() {
        double income = manager.getTotalIncome();
        double expense = manager.getTotalExpense();
        double balance = income - expense;

        incomeLabel.setText("Prihod: " + income);
        expenseLabel.setText("Rashod: " + expense);
        balanceLabel.setText("Saldo: " + balance);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}

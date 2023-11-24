package gui.staff;

import controllers.AppContext;
import db.DatabaseBridge;
import db.DatabaseOperation;
import entity.product.Product;
import gui.components.ButtonEditor;
import gui.components.ButtonRenderer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static utils.GUI.ukCurrencyFormat;

public class StockManagementScreen extends JPanel {
    private final String[] columns = new String[]{"Code", "Product", "Price Per Unit", "Stock", "Delete Item"};

    public StockManagementScreen() {
        Object[][] productData;

        try {
            DatabaseBridge.instance().openConnection();
            PreparedStatement countQuery = DatabaseBridge.instance().prepareStatement("SELECT COUNT(*) FROM Product;");
            ResultSet res = countQuery.executeQuery();
            int count = -1;
            if (res.next()) {
                count = res.getInt(1);
            } else {
                throw new RuntimeException("Could not get product data");
            }

            ResultSet products = Product.getAllProducts();
            productData = new Object[count - 1][columns.length];
            int index = 0;
            while (true) {
                assert products != null;
                if (!products.next()) break;
                productData[index][0] = products.getString("productCode");
                productData[index][1] = products.getString("name");
                productData[index][2] = products.getDouble("price");
                productData[index][3] = products.getInt("stockLevel");
                ++index;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseBridge.instance().closeConnection();
        }

        setLayout(new GridLayout());

        JTable jt = new JTable( new StockManagementModel(productData, columns));
        jt.getColumnModel().getColumn(2).setCellRenderer(new CurrencyCellRenderer());
//        jt.getColumnModel().getColumn(4).setCellRenderer(new DeleteCellRenderer());
        jt.getColumn("Delete Item").setCellRenderer(new ButtonRenderer("Delete Item"));
        jt.getColumn("Delete Item").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(jt);
        this.add(scrollPane);

        // refresh database view button
    }

    public static void main(String[] args) {
        DatabaseOperation.setConnection(DatabaseBridge.instance());
        JFrame win = AppContext.getWindow();
        win.add(new StockManagementScreen());
        win.setVisible(true);
    }

    private static class CurrencyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public void setValue(Object value)
        {
            if (value != null) {
                value = ukCurrencyFormat.format((Double)value);
            }
            super.setValue(value);
        }
    }

    // https://tips4java.wordpress.com/2009/07/12/table-button-column/
//    private static class DeleteCellRenderer extends JButton implements TableCellRenderer {
//        public DeleteCellRenderer() {
//            setOpaque(true);
//        }
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            setText("Delete " + row);
//            addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    System.out.println(row + ", " + column);
//                }
//            });
//            return this;
//        }
//    }

    private static class StockManagementModel extends AbstractTableModel {

        private Object[][] productData;
        private String[] columns;

        public StockManagementModel(Object[][] productData, String[] columns) {
            this.productData = productData;
            this.columns = columns;
        }

        @Override
        public int getRowCount() {
            return this.productData.length;
        }

        @Override
        public int getColumnCount() {
            return this.columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return this.productData[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int columnIndex) {
            return this.columns[columnIndex];
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            this.productData[rowIndex][columnIndex] = value;

            // database command
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return ! (columnIndex == 0);// || columnIndex == 4);
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return String.class;
                case 1: return String.class;
                case 2: return Double.class;
                case 3: return Integer.class;
                default: return String.class;
            }
        }
    }
}


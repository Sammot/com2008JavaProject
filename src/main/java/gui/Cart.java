package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart extends JPanel {

    public Cart() {
        Map<String, CartItem> cartItems = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order
        cartItems.put("Product 1", new CartItem("Product 1", 2, "$10.99"));
        cartItems.put("Product 2", new CartItem("Product 2", 1, "$19.99"));
        cartItems.put("Product 3", new CartItem("Product 3", 3, "$5.99"));
        createOrderLine(cartItems);
    }

    public void createOrderLine(Map<String, CartItem> cartItems) {
        // Create a DefaultTableModel with column names and 0 rows
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Product Name", "Quantity", "Unit Price", "Total"}, 0);

        // Add cart items to the table model
        double totalPrice = 0.0;
        for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
            CartItem cartItem = entry.getValue();
            Object[] rowData = {
                    cartItem.getProductName(),
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice(),
                    cartItem.getTotal()
            };
            model.addRow(rowData);

            // Update total price
            totalPrice += cartItem.getTotal();
        }

        // Create the JTable with the model
        JTable cartTable = new JTable(model);

        // Create a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(cartTable);

        // Add the JScrollPane to the frame
        add(scrollPane);

        // Create labels for total price
        JLabel totalLabel = new JLabel("Total Price: " + formatCurrency(totalPrice));
        totalLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(totalLabel);

        // Create a Checkout button
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> {
            // Add your checkout logic here
            System.out.println("Checkout button clicked!");
        });

        // Add Checkout button to the panel
        add(checkoutButton);

        // Set frame properties
        setSize(500, 400);
        setVisible(true);
    }

    // Helper method to format currency
    private String formatCurrency(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return "$" + decimalFormat.format(amount);
    }

    public static void main(String[] args) {
        // Example usage with cart items

        // Show this on the screen
        JFrame frame = new JFrame("Cart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Cart());
        frame.pack();
        frame.setVisible(true);
    }

    // Define a CartItem class to represent a product in the cart
    public class CartItem {
        private String productName;
        private int quantity;
        private String unitPrice;

        public CartItem(String productName, int quantity, String unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public double getTotal() {
            return quantity * parseUnitPrice(unitPrice);
        }

        // Helper method to parse unit price as a double
        private static double parseUnitPrice(String unitPrice) {
            return Double.parseDouble(unitPrice.replaceAll("[^\\d.]", ""));
        }
    }
}

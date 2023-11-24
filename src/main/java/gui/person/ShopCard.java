package gui.person;

import db.DatabaseBridge;
import entity.product.*;
import entity.product.Component;
import org.javatuples.Pair;
import utils.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ShopCard extends JPanel {
    GridBagConstraints gbc;
    GridBagLayout gbl;

    Product product;

    Integer quantity;
    JTextField quantityBox;

    public ShopCard(Product product) throws SQLException {
        this.product = product;

        gbc = new GridBagConstraints();
        gbl = new GridBagLayout();
        gbl.setConstraints(this, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbl.setConstraints(this, gbc);
        setLayout(gbl);

        setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel productName = new JLabel("<html><h2>"+product.getName()+"</h2></html>");
        add(productName, gbc);

        gbc.gridx = 1;
        JLabel price = new JLabel("<html><h4>"+GUI.ukCurrencyFormat.format(product.getPrice())+"</h4></html>");
        price.setHorizontalAlignment(SwingConstants.RIGHT);
        add(price, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;

        JLabel productCode = new JLabel(product.getProductCode());
        add(productCode, gbc);

        gbc.gridy = 2;

        try {
            if (product.isComponent()) {
                Component productComponent = product.getComponent();

                JLabel brand = new JLabel("Brand: "+productComponent.getBrand());
                add(brand, gbc);

                gbc.gridy++;

                JLabel gauge = new JLabel("Gauge: "+productComponent.getGauge().toString());
                add(gauge, gbc);

                gbc.gridy++;

                JLabel era = new JLabel("Era: "+productComponent.getEra().toString());
                add(era, gbc);

                if (productComponent.getClass().equals(Locomotive.class)) {
                    gbc.gridy++;
                    JLabel priceBracket = new JLabel("Price Bracket: "+((Locomotive) productComponent).getPriceBracket().toString());
                    add(priceBracket, gbc);
                }

                if (productComponent.getClass().equals(Track.class)) {
                    gbc.gridy++;
                    JLabel curvature = new JLabel("Curvature: "+((Track) productComponent).getCurvature().toString());
                    add(curvature, gbc);
                }

                if (productComponent.getClass().equals(Controller.class)) {
                    gbc.gridy++;
                    JLabel controllerType = new JLabel("Controller Type: "+((Controller) productComponent).getControlType().toString());
                    add(controllerType, gbc);
                }

                JPanel quantityPanel = new JPanel();
                quantityPanel.setLayout(new BorderLayout());

                JLabel quantityLabel = new JLabel("Quantity: ");
                quantityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                quantityBox = new JTextField();
                quantityBox.setPreferredSize(new Dimension(30, 24));
                quantityPanel.add(quantityLabel, BorderLayout.CENTER);
                quantityPanel.add(quantityBox, BorderLayout.EAST);

                gbc.gridy++;
                add(quantityPanel, gbc);

                JButton addToCardBtn = new JButton("Add to Cart");

                gbc.gridx = 1;
                add(addToCardBtn, gbc);
            } else {
                BoxedSet boxedSet = product.getBoxedSet();
                List<Pair<Component, Integer>> components = boxedSet.getComponents();
                List<Pair<BoxedSet, Integer>> subBoxedSets = boxedSet.getBoxedSets();

                JPanel componentPanel = new JPanel();
                JScrollPane scrollPane = new JScrollPane(componentPanel);
                scrollPane.setMaximumSize(new Dimension(0, 70));
                scrollPane.setMinimumSize(new Dimension(0, 70));
                scrollPane.setPreferredSize(new Dimension(0, 70));
                scrollPane.setVerticalScrollBar(new JScrollBar());

                componentPanel.setLayout(new GridLayout(0, 1));

                subBoxedSets.forEach((c) -> {
                    BoxedSet set = c.getValue0();
                    Integer amount = c.getValue1();
                    JLabel label = new JLabel(amount.toString()+"x "+set.getName());
                    componentPanel.add(label);
                });

                components.forEach((c) -> {
                    Product component = c.getValue0();
                    Integer amount = c.getValue1();
                    JLabel label = new JLabel(amount.toString()+"x "+component.getName());
                    componentPanel.add(label);
                });

                add(scrollPane, gbc);

                JPanel quantityPanel = new JPanel();
                quantityPanel.setLayout(new BorderLayout());

                JLabel quantityLabel = new JLabel("Quantity: ");
                quantityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                quantityBox = new JTextField();
                quantityBox.setPreferredSize(new Dimension(30, 24));
                quantityPanel.add(quantityLabel, BorderLayout.CENTER);
                quantityPanel.add(quantityBox, BorderLayout.EAST);

                gbc.gridy++;
                add(quantityPanel, gbc);

                JButton addToCardBtn = new JButton("Add to Cart");

                gbc.gridx = 1;
                add(addToCardBtn, gbc);
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}

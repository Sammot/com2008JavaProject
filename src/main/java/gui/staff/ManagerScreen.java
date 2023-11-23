package gui.staff;

import db.DatabaseBridge;
import entity.StoreAttributes;
import entity.user.Person;
import gui.person.TabbedGUIContainer;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerScreen extends JPanel implements TabbedGUIContainer.TabPanel {
    private static class UserRow extends JPanel {
        static String[] roleNames = StoreAttributes.Role.getStringValues();
        Person person;

        public UserRow(Person user) {
            person = user;
            JComboBox<String> roleBox = new JComboBox<>(roleNames);
            roleBox.setSelectedItem(user.getRole().toString());

            JLabel nameLabel = new JLabel(user.getFullName());

            GridLayout gridLayout = new GridLayout(0,2);
            JPanel content = new JPanel();

            content.setLayout(gridLayout);
            add(content);

            content.add(nameLabel);
            content.add(roleBox);

            content.add(new JSeparator(SwingConstants.HORIZONTAL));
            content.add(new JSeparator(SwingConstants.HORIZONTAL));

            roleBox.addActionListener(e -> {
                StoreAttributes.Role newRole = StoreAttributes.Role.valueOf((String) roleBox.getSelectedItem());

                DatabaseBridge db = DatabaseBridge.instance();
                try {
                    db.openConnection();
                    Person.updateUserRole(person, newRole);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    db.closeConnection();
                }
            });
        }
    }
    private TabbedGUIContainer parent;
    JScrollPane scrollPane;
    JPanel contentPanel;
    GridBagConstraints gbc;

    String searchTerm = "";

    public ManagerScreen() {
        setLayout(new BorderLayout());

        GridBagLayout gbl = new GridBagLayout();
        gbc = new GridBagConstraints();
        contentPanel = new JPanel();
        contentPanel.setLayout(gbl);

        JPanel header = new JPanel();
        header.setLayout(new FlowLayout());

        JLabel title = new JLabel("<html><h1>Search by Name: </h1></html>");
        JTextField searchBox = new JTextField();
        searchBox.setPreferredSize( new Dimension( 200, 24 ) );
        JButton search = new JButton("\uD83D\uDD0E");

        search.addActionListener((e) -> {
            this.searchTerm = searchBox.getText();
            setAll();
        });

        header.add(title);
        header.add(searchBox);
        header.add(search);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(header, BorderLayout.NORTH);

        scrollPane = new JScrollPane(contentPanel);
        this.add(scrollPane, BorderLayout.CENTER);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = -1;

        setAll();
    }

    private void setAll() {
        contentPanel.removeAll();
        repaint();

        DatabaseBridge db = DatabaseBridge.instance();

        List<Person> personList = new ArrayList<>();
        try {
            db.openConnection();
            PreparedStatement query = db.prepareStatement("SELECT * FROM Person");

            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                Person them = Person.getPersonByEmail(rs.getString("email"));
                assert them != null;
                personList.add(them);
            }
        } catch (SQLException e) {
            DatabaseBridge.databaseError("Failed to fetch all persons", e);
            throw new RuntimeException();
        } finally {
            db.closeConnection();
        }

        if (searchTerm.isEmpty()) {
            personList.forEach(this::addUser);
        } else {
            personList.stream()
                    .filter((person -> person.getFullName().contains(searchTerm)))
                    .forEach((person -> {
                        System.out.println(person.getFullName());
                        addUser(person);
                    }));
        }

    }

    private void addUser(Person user) {
        gbc.gridy += 1;
        UserRow row = new UserRow(user);
        contentPanel.add(row, gbc);
    }

    @Override
    public void setNotebookContainer(TabbedGUIContainer cont) {
        parent = cont;
    }
}
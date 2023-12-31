import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

public class BidderForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField balanceField;
    private JTextField emailField;

    public BidderForm() {
        setTitle("Create New Bidder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        // Create and add components
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 10, 5);

        usernameField = createTextField("Username:");
        passwordField = createPasswordField("Password:");
        balanceField = createNumericField("Balance:");
        emailField = createTextField("Email:");

        // Add components to the formPanel with GridBagConstraints
        addToFormPanel(formPanel, "Username:", usernameField, gbc);
        addToFormPanel(formPanel, "Password:", passwordField, gbc);
        addToFormPanel(formPanel, "Balance:", balanceField, gbc);
        addToFormPanel(formPanel, "Email:", emailField, gbc);

        JButton createButton = new JButton("Create Bidder");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storeBidderData();
                displayAuctions();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JTextField createTextField(String labelText) {
        JTextField textField = new JTextField(20);
        textField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setForeground(Color.BLACK); // Change text color to black
        return textField;
    }

    private JPasswordField createPasswordField(String labelText) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setForeground(Color.BLACK); // Change text color to black
        return passwordField;
    }

    private JTextField createNumericField(String labelText) {
        JTextField numericField = createTextField(labelText);
        return numericField;
    }

    private void addToFormPanel(JPanel panel, String label, JComponent component, GridBagConstraints gbc) {
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private void storeBidderData() {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/appxviewhacks";
            String username = "root";
            String password = "12Athmikha@";

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            String insertQuery = "INSERT INTO bidder " +
                    "(username, password, balance, email) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, usernameField.getText());
            preparedStatement.setString(2, new String(passwordField.getPassword()));
            preparedStatement.setString(3, balanceField.getText());
            preparedStatement.setString(4, emailField.getText());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayAuctions() {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/appxviewhacks";
            String username = "root";
            String password = "12Athmikha@";

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Statement statement = connection.createStatement();
            String selectQuery = "SELECT * FROM auction WHERE active = 1"; // Assuming there's a column 'is_active'

            ResultSet resultSet = statement.executeQuery(selectQuery);

            JFrame auctionListFrame = new JFrame("Active Auctions");
            auctionListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Create a JTable to display the auctions
            String[] columnNames = {"AID","Title", "Description", "Current Bid", "Minimum Bid Increment", "Start Time", "End Time", "Max Bid"};

            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            JTable table = new JTable(model);
            table.getColumnModel().getColumn(0).setPreferredWidth(100);
            table.getColumnModel().getColumn(1).setPreferredWidth(200); // Description
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Current Bid
            table.getColumnModel().getColumn(3).setPreferredWidth(150); // Minimum Bid Increment
            table.getColumnModel().getColumn(4).setPreferredWidth(150); // Start Time
            table.getColumnModel().getColumn(5).setPreferredWidth(150); // End Time
            table.getColumnModel().getColumn(6).setPreferredWidth(100); // Max Bid
            table.getColumnModel().getColumn(7).setPreferredWidth(100);

            while (resultSet.next()) {
                int aid = resultSet.getInt("aid");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                double currentBid = resultSet.getDouble("current_bid");
                double minBidIncrement = resultSet.getDouble("minimum_bid_increment");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
                double maxBid = resultSet.getDouble("max_bid");

                Object[] data = {aid, title, description, currentBid, minBidIncrement, startTime, endTime, maxBid};
                model.addRow(data);
            }

            // Create the "Place Bid" button
            JButton placeBidButton = new JButton("Place Bid");
            placeBidButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle placing a bid for the selected auction
                    showPlaceBidForm(); // Show the Place Bid Form
                }
            });

            // Add the "Place Bid" button to the auctionListFrame
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(placeBidButton);
            auctionListFrame.add(buttonPanel, BorderLayout.SOUTH);

            JScrollPane scrollPane = new JScrollPane(table);
            auctionListFrame.add(scrollPane);

            auctionListFrame.pack();
            auctionListFrame.setLocationRelativeTo(this);
            auctionListFrame.setVisible(true);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPlaceBidForm() {
        // Create a new JFrame for the Place Bid form
        JFrame placeBidFrame = new JFrame("Place Bid");
        placeBidFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 10, 5);

        JTextField auctionIdField = createNumericField("Auction ID:");

        JTextField currentBidField = createNumericField("Current Bid:");

        addToFormPanel(formPanel, "Auction ID:", auctionIdField, gbc);

        addToFormPanel(formPanel, "Current Bid:", currentBidField, gbc);

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle adding details to the watchlist table
                addToWatchlist(
                        auctionIdField.getText(),
                        currentBidField.getText()
                );
                String auctionId = auctionIdField.getText();
                String currentBid = currentBidField.getText();
                if (isBidValid(auctionId, currentBid)) {
                    addToWatchlist(auctionId, currentBid);
                    updateAuctionCurrentBid(auctionId, currentBid);
                    placeBidFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Bid must be greater than the current bid for this auction.", "Invalid Bid", JOptionPane.ERROR_MESSAGE);
                }
                // Close the Place Bid form
                placeBidFrame.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(doneButton);

        placeBidFrame.add(formPanel, BorderLayout.CENTER);
        placeBidFrame.add(buttonPanel, BorderLayout.SOUTH);

        placeBidFrame.pack();
        placeBidFrame.setLocationRelativeTo(this);
        placeBidFrame.setVisible(true);
    }
    private boolean isBidValid(String auctionId, String currentBid) {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/appxviewhacks";
            String username = "root";
            String password = "12Athmikha@";
            double bidAmount = Double.parseDouble(currentBid);

            // Query to get the current_bid for the auction
            String getCurrentBidQuery = "SELECT current_bid FROM auction WHERE aid = ?";
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(getCurrentBidQuery);
            preparedStatement.setString(1, auctionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double currentAuctionBid = resultSet.getDouble("current_bid");
                return bidAmount > currentAuctionBid;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void updateAuctionCurrentBid(String auctionId, String newBid) {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/appxviewhacks";
            String username = "root";
            String password = "12Athmikha@";

            double bidAmount = Double.parseDouble(newBid);
            String updateCurrentBidQuery = "UPDATE auction SET current_bid = ? WHERE aid = ?";

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(updateCurrentBidQuery);
            preparedStatement.setDouble(1, bidAmount);
            preparedStatement.setString(2, auctionId);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
    private void addToWatchlist(String auctionId, String currentBid) {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/appxviewhacks";
            String username = "root";
            String password = "12Athmikha@";

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Retrieve the last ID from the bidder table
            String getLastBidderIdQuery = "SELECT MAX(bid) AS last_bidder_id FROM bidder";
            Statement lastBidderIdStatement = connection.createStatement();
            ResultSet lastBidderIdResult = lastBidderIdStatement.executeQuery(getLastBidderIdQuery);

            int lastBidderId = 0; // Default value
            if (lastBidderIdResult.next()) {
                lastBidderId = lastBidderIdResult.getInt("last_bidder_id");
            }

            lastBidderIdResult.close();
            lastBidderIdStatement.close();

            // Insert the details into the watchlist table with the last bidder ID
            String insertQuery = "INSERT INTO watchlist (aid, bid, max_bid) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, auctionId);
            preparedStatement.setInt(2, lastBidderId);
            preparedStatement.setString(3, currentBid);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BidderForm bidderForm = new BidderForm();
            bidderForm.setVisible(true);
        });
    }
}

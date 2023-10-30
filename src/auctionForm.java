import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class auctionForm extends JFrame {

    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField currentBidField;
    private JTextField minimumBidIncrementField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField maxBidField;

    public auctionForm() {
        setTitle("Create New Auction");
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

        titleField = createTextField("Title:");
        descriptionField = createTextField("Description:");
        currentBidField = createNumericField("Current Bid:");
        minimumBidIncrementField = createNumericField("Minimum Bid Increment:");
        startTimeField = createTextField("Start Time:");
        endTimeField = createTextField("End Time:");
        maxBidField = createNumericField("Maximum Bid:");

        // Add components to the formPanel with GridBagConstraints
        addToFormPanel(formPanel, "Title:", titleField, gbc);
        addToFormPanel(formPanel, "Description:", descriptionField, gbc);
        addToFormPanel(formPanel, "Current Bid:", currentBidField, gbc);
        addToFormPanel(formPanel, "Minimum Bid Increment:", minimumBidIncrementField, gbc);
        addToFormPanel(formPanel, "Start Time:", startTimeField, gbc);
        addToFormPanel(formPanel, "End Time:", endTimeField, gbc);
        addToFormPanel(formPanel, "Maximum Bid:", maxBidField, gbc);

        JButton createButton = new JButton("Create Auction");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storeAuctionData();
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

    private void storeAuctionData() {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/appxviewhacks";
            String username = "root";
            String password = "12Athmikha@";

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            String insertQuery = "INSERT INTO auction " +
                    "(title, description, current_bid, minimum_bid_increment, start_time, end_time, " +
                    "max_bid) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, titleField.getText());
            preparedStatement.setString(2, descriptionField.getText());

            // Validate and parse numeric fields
            if (!currentBidField.getText().isEmpty()) {
                preparedStatement.setDouble(3, Double.parseDouble(currentBidField.getText()));
            } else {
                preparedStatement.setNull(3, java.sql.Types.DOUBLE);
            }

            if (!minimumBidIncrementField.getText().isEmpty()) {
                preparedStatement.setDouble(4, Double.parseDouble(minimumBidIncrementField.getText()));
            } else {
                preparedStatement.setNull(4, java.sql.Types.DOUBLE);
            }

            preparedStatement.setString(5, startTimeField.getText());
            preparedStatement.setString(6, endTimeField.getText());

            if (!maxBidField.getText().isEmpty()) {
                preparedStatement.setDouble(7, Double.parseDouble(maxBidField.getText()));
            } else {
                preparedStatement.setNull(7, java.sql.Types.DOUBLE);
            }

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            auctionForm auctionForm = new auctionForm();
            auctionForm.setVisible(true);
        });
    }
}

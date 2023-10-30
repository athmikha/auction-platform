import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AuctionDataStore {
    public static void storeAuctionData(String title, String description, double currentBid, double minimumBidIncrement, String startTime, String endTime, double maxBid, boolean proxyBidding) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/appxviewhacks"; // Your database URL
        String username = "localhost"; // Your database username
        String password = "12Athmikha@"; // Your database password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            String insertQuery = "INSERT INTO auctions (title, description, current_bid, minimum_bid_increment, start_time, end_time, max_bid, proxy_bidding) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, currentBid);
            preparedStatement.setDouble(4, minimumBidIncrement);
            preparedStatement.setString(5, startTime);
            preparedStatement.setString(6, endTime);
            preparedStatement.setDouble(7, maxBid);
            preparedStatement.setBoolean(8, proxyBidding);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package model;

import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class MReservation {

    // Method to add a reservation
    public void addReservation(int guestID, int roomID, int quantity,
            double totalPrice, String reservationDate) {

        PreparedStatement ps = null;

        try {
            // Prepare the SQL query
            String sql = "INSERT INTO reservations "
                    + "(GuestID, RoomID, Quantity, TotalPrice, ReservationDate) "
                    + "VALUES (?, ?, ?, ?, ?)";

            // Use PreparedStatement for safer SQL queries
            ps = DBConnection.createDBConnection().prepareStatement(sql);

            ps.setInt(1, guestID);
            ps.setInt(2, roomID);
            ps.setInt(3, quantity);
            ps.setDouble(4, totalPrice);

            // Convert the reservation date string to SQL Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(reservationDate);

            ps.setDate(5, new java.sql.Date(parsedDate.getTime()));

            // Execute the query
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    null,
                    "Reservation added successfully."
            );

        } catch (SQLException | java.text.ParseException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Error adding reservation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Method to fetch Room ID using Room Name
    public int fetchRoomID(String roomName) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;

        int roomID = -1;

        try {

            String sql = "SELECT RoomID FROM room WHERE RoomName = ?";

            ps = DBConnection.createDBConnection().prepareStatement(sql);
            ps.setString(1, roomName);

            rs = ps.executeQuery();

            if (rs.next()) {
                roomID = rs.getInt("RoomID");
            }

        } finally {

            if (rs != null) {
                rs.close();
            }

            if (ps != null) {
                ps.close();
            }
        }

        return roomID;
    }

    // Method to fetch room price
    public double fetchRoomPrice(int roomID) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;

        double roomPrice = 0.0;

        try {

            String sql = "SELECT PricePerNight FROM room WHERE RoomID = ?";

            ps = DBConnection.createDBConnection().prepareStatement(sql);
            ps.setInt(1, roomID);

            rs = ps.executeQuery();

            if (rs.next()) {
                roomPrice = rs.getDouble("PricePerNight");
            }

        } finally {

            if (rs != null) {
                rs.close();
            }

            if (ps != null) {
                ps.close();
            }
        }

        return roomPrice;
    }

    // Method to update an existing reservation
    public void updateReservation(int reservationID, int guestID,
            int roomID, int quantity,
            double totalPrice, String reservationDate) {

        PreparedStatement ps = null;

        try {

            String sql = "UPDATE reservations SET "
                    + "GuestID = ?, "
                    + "RoomID = ?, "
                    + "Quantity = ?, "
                    + "TotalPrice = ?, "
                    + "ReservationDate = ? "
                    + "WHERE ReservationID = ?";

            ps = DBConnection.createDBConnection().prepareStatement(sql);

            // Set parameters
            ps.setInt(1, guestID);
            ps.setInt(2, roomID);
            ps.setInt(3, quantity);
            ps.setDouble(4, totalPrice);

            // Convert the reservation date to java.sql.Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(reservationDate);

            java.sql.Date sqlDate
                    = new java.sql.Date(parsedDate.getTime());

            ps.setDate(5, sqlDate);
            ps.setInt(6, reservationID);

            // Execute the update query
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 1) {

                JOptionPane.showMessageDialog(
                        null,
                        "Reservation updated successfully!"
                );

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "Reservation not found or no changes made.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException | java.text.ParseException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Error updating reservation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Method to delete a reservation
    public void deleteReservation(int reservationID) {

        PreparedStatement ps = null;

        try {

            String sql = "DELETE FROM reservations WHERE ReservationID = ?";

            ps = DBConnection.createDBConnection().prepareStatement(sql);
            ps.setInt(1, reservationID);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {

                JOptionPane.showMessageDialog(
                        null,
                        "Reservation deleted successfully!"
                );

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "Reservation not found. Deletion failed."
                );
            }

        } catch (SQLException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Database error while deleting reservation: "
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Method to load reservations into the table
    public void loadReservations(DefaultTableModel model) {

        Statement st = null;
        ResultSet rs = null;

        try {

            st = DBConnection.createDBConnection().createStatement();

            rs = st.executeQuery("SELECT * FROM reservations");

            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();

            // Clear existing rows in the table model
            model.setRowCount(0);

            while (rs.next()) {

                Vector<String> row = new Vector<>();

                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }

                model.addRow(row);
            }

        } catch (SQLException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Error loading reservation data: "
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Method to search reservation by ID
    public ResultSet searchReservationById(int reservationID)
            throws SQLException {

        String query
                = "SELECT r.ReservationID, "
                + "r.GuestID, "
                + "rm.RoomName, "
                + "r.Quantity, "
                + "r.ReservationDate "
                + "FROM reservations r "
                + "INNER JOIN room rm ON r.RoomID = rm.RoomID "
                + "WHERE r.ReservationID = ?";

        PreparedStatement ps
                = DBConnection.createDBConnection().prepareStatement(query);

        ps.setInt(1, reservationID);

        return ps.executeQuery();
    }
}

package model;

import java.sql.*;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class MRoom {

    // Add Room
    public void addRoom(int roomID, String roomName, String roomType,
            double pricePerNight, int quantity) {

        Statement st = null;

        try {
            st = DBConnection.createDBConnection().createStatement();

            String sql = "INSERT INTO Room "
                    + "(RoomID, RoomName, RoomType, PricePerNight, Quantity) "
                    + "VALUES ("
                    + roomID + ", '"
                    + roomName + "', '"
                    + roomType + "', "
                    + pricePerNight + ", "
                    + quantity + ")";

            st.executeUpdate(sql);

            JOptionPane.showMessageDialog(
                    null,
                    "Room added successfully."
            );

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Error adding room or room already exists.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Check if Room Exists
    public void checkRoomExists(String roomID) {

        String sql = "SELECT COUNT(*) FROM Room WHERE RoomID = " + roomID;

        try (
                Connection conn = DBConnection.createDBConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {

                if (rs.getInt(1) > 0) {
                    System.out.println("Room exists in the database.");
                } else {
                    System.out.println("Room does not exist.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    // Update Room
    public void updateRoom(int roomID, String roomName, String roomType,
            double pricePerNight, int quantity) {

        Statement st = null;

        try {
            st = DBConnection.createDBConnection().createStatement();

            String sql = "UPDATE Room SET "
                    + "RoomName='" + roomName + "', "
                    + "RoomType='" + roomType + "', "
                    + "PricePerNight=" + pricePerNight + ", "
                    + "Quantity=" + quantity
                    + " WHERE RoomID=" + roomID;

            int i = st.executeUpdate(sql);

            if (i == 1) {
                System.out.println("Room updated successfully!");
            } else {
                System.out.println("Error updating room or room not found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());

        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Delete Room
    public void deleteRoom(int roomID) {

        Statement st = null;

        try {
            st = DBConnection.createDBConnection().createStatement();

            String sql = "DELETE FROM Room WHERE RoomID=" + roomID;

            st.executeUpdate(sql);

            JOptionPane.showMessageDialog(
                    null,
                    "Room deleted successfully!"
            );

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Load Rooms into JTable
    public void loadRooms(DefaultTableModel model) {

        Statement st = null;
        ResultSet rs = null;

        try {
            st = DBConnection.createDBConnection().createStatement();

            rs = st.executeQuery("SELECT * FROM Room");

            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();

            // Clear existing rows
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
                    "Error loading room data: " + ex.getMessage(),
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

    // Search Room by ID
    public ResultSet searchRoomById(int roomID) throws SQLException {

        Statement st = DBConnection.createDBConnection().createStatement();

        return st.executeQuery(
                "SELECT * FROM Room WHERE RoomID = " + roomID
        );
    }
}

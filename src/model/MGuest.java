
package model;

import java.sql.*;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class MGuest {

    public void addGuest(int guestID, String name, String contact, String address, String email) {
        Statement st = null;
        try {
            st = DBConnection.createDBConnection().createStatement();
            String sql = "INSERT INTO Guests (GuestID, GuestName, Contact, Address, Email) VALUES ('"
                    + guestID + "', '"
                    + name + "', '"
                    + contact + "', '"
                    + address + "', '"
                    + email + "')";
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Guest added successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding guest or guest already exists.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void checkGuestExists(String guestID) {
        String sql = "SELECT COUNT(*) FROM Guests WHERE GuestID = '" + guestID + "'";
        try (Connection conn = DBConnection.createDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    System.out.println("Guest exists in the database.");
                } else {
                    System.out.println("Guest does not exist.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public void updateGuest(int guestID, String name, String contact, String address, String email) {
        Statement st = null;
        try {
            st = DBConnection.createDBConnection().createStatement();
            String sql = "UPDATE Guests SET GuestName='" + name
                    + "', Contact='" + contact
                    + "', Address='" + address
                    + "', Email='" + email
                    + "' WHERE GuestID=" + guestID;

            int i = st.executeUpdate(sql);

            if (i == 1) {
                System.out.println("Guest updated successfully!");
            } else {
                System.out.println("Error updating guest or guest not found.");
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

    public void deleteGuest(int guestID) {
        Statement st = null;
        try {
            st = DBConnection.createDBConnection().createStatement();
            String sql = "DELETE FROM Guests WHERE GuestID='" + guestID + "'";
            int i = st.executeUpdate(sql);

            /*if (i == 1) {
                JOptionPane.showMessageDialog(null, "Guest deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error deleting guest or guest not found.");
            }*/
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public void loadGuest(DefaultTableModel model) {
        Statement st = null;
        ResultSet rs = null;

        try {
            st = DBConnection.createDBConnection().createStatement();
            rs = st.executeQuery("SELECT * FROM Guests");

            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();

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
            JOptionPane.showMessageDialog(null, "Error loading guest data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public ResultSet searchGuestById(int guestID) throws SQLException {
        Statement st = DBConnection.createDBConnection().createStatement();

        return st.executeQuery("SELECT * FROM Guests WHERE GuestID = " + guestID);
    }
}
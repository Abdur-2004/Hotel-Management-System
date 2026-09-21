package model;

import java.sql.*;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class MRoom {

    public void addProduct(int productID, String productName, double unitPrice) {
        Statement st = null;
        try {
            st = DBConnection.createDBConnection().createStatement();
            String sql = "INSERT INTO Product (ProductID, ProductName, UnitPrice) VALUES ('"
                    + productID + "', '"
                    + productName + "', "
                    + unitPrice + ")";
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Product added successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding product or product already exists.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void checkProductExists(String productID) {
        String sql = "SELECT COUNT(*) FROM product WHERE ProductID = '" + productID + "'";
        try (Connection conn = DBConnection.createDBConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    System.out.println("Product exists in the database.");
                } else {
                    System.out.println("Product does not exist.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public void updateProduct(int productID, String productName, double unitPrice) {
        Statement st = null;
        try {
            st = DBConnection.createDBConnection().createStatement();
            String sql = "UPDATE Product SET ProductName='" + productName
                    + "', UnitPrice=" + unitPrice
                    + " WHERE ProductID=" + productID;
            int i = st.executeUpdate(sql);

            if (i == 1) {
                System.out.println("Product updated successfully!");
            } else {
                System.out.println("Error updating product or product not found.");
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

    public void deleteProduct(int productID) {
        Statement st = null;
        try {
            st = DBConnection.createDBConnection().createStatement();
            String sql = "DELETE FROM Product WHERE ProductID='" + productID + "'";
            st.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Product deleted successfully!");
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

    public void loadProducts(DefaultTableModel model) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = DBConnection.createDBConnection().createStatement();
            rs = st.executeQuery("SELECT * FROM Product");

            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();

            model.setRowCount(0); // Clear existing rows in the table model

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i)); // Fetch each column's data
                }
                model.addRow(row); // Add the row to the table model
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading product data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                try {
                    rs.close(); // Close ResultSet
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (st != null) {
                try {
                    st.close(); // Close Statement
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ResultSet searchProductById(int productId) throws SQLException {
        Statement st = DBConnection.createDBConnection().createStatement();
        return st.executeQuery("SELECT * FROM product WHERE ProductID = " + productId);
    }
}

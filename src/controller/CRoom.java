package controller;

import model.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CRoom {

    public void addProduct(int productID, String productName, double unitPrice) {
        MRoom mfab = new MRoom();
        mfab.addProduct(productID, productName, unitPrice);
    }

    public void updateProduct(int productID, String productName, double unitPrice) {
        if (productID <= 0 || productName.isEmpty() || unitPrice <= 0) {
            System.out.println("Invalid input. Ensure all fields are filled and product ID is valid.");
            return;
        }

        MRoom mfab = new MRoom();
        mfab.updateProduct(productID, productName, unitPrice);
    }

    public void deleteProduct(int productID) {
        MRoom mfab = new MRoom();
        mfab.deleteProduct(productID);
    }

    public void loadProducts(DefaultTableModel model) {
        MRoom mfab = new MRoom();
        mfab.loadProducts(model);
    }

    public ResultSet searchProductById(int productID) throws SQLException {
        MRoom mfab = new MRoom();
        return mfab.searchProductById(productID);
    }

    public void checkProductExists(String productID) {
        if (productID == null || productID.isEmpty()) {
            System.out.println("Invalid Product ID. Cannot check existence.");
            return;
        }
        MRoom mfab = new MRoom();
        mfab.checkProductExists(productID);
    }

}

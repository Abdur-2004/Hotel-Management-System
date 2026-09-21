
package controller;

import model.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CGuest {

    public void addGuest(int guestID, String name, String contact, String address, String email) {
        MGuest mguest = new MGuest();
        mguest.addGuest(guestID, name, contact, address, email);
    }

    public void updateGuest(int guestID, String name, String contact, String address, String email) {
        if (guestID <= 0 || name.isEmpty() || contact.isEmpty() || address.isEmpty() || email.isEmpty()) {
            System.out.println("Invalid input. Ensure all fields are filled and guest ID is valid.");
            return;
        }

        MGuest mguest = new MGuest();
        mguest.updateGuest(guestID, name, contact, address, email);
    }

    public void deleteGuest(int guestID) {
        MGuest mguest = new MGuest();
        mguest.deleteGuest(guestID);
    }

    public void loadGuest(DefaultTableModel model) {
        MGuest mguest = new MGuest();
        mguest.loadGuest(model);
    }

    public ResultSet searchGuestById(int guestID) throws SQLException {
        MGuest mguest = new MGuest();
        return mguest.searchGuestById(guestID);
    }

    public void checkGuestExists(String guestID) {
        if (guestID == null || guestID.isEmpty()) {
            System.out.println("Invalid Guest ID. Cannot check existence.");
            return;
        }

        MGuest mguest = new MGuest();
        mguest.checkGuestExists(guestID);
    }

}
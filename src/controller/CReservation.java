
package controller;

import model.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CReservation {

    MReservation model = new MReservation();

    // Method to add a reservation
    public void addReservation(int guestID, String roomName,
                               int quantity, String reservationDate)
            throws SQLException {

        try {

            // Fetch the room ID and room price
            int roomID = model.fetchRoomID(roomName);
            double roomPrice = model.fetchRoomPrice(roomID);

            if (roomID <= 0) {
                throw new SQLException(
                        "Invalid room for name: " + roomName
                );
            }

            if (roomPrice <= 0) {
                throw new SQLException(
                        "Invalid room price for room: " + roomName
                );
            }

            // Calculate total price
            double totalPrice = roomPrice * quantity;

            // Pass the data to the model for insertion
            model.addReservation(
                    guestID,
                    roomID,
                    quantity,
                    totalPrice,
                    reservationDate
            );

        } catch (SQLException ex) {

            throw new SQLException(
                    "Failed to add reservation: " + ex.getMessage()
            );
        }
    }

    // Method to update a reservation
    public void updateReservation(int reservationID, int guestID,
                                  int roomID, int quantity,
                                  double totalPrice,
                                  String reservationDate) {

        if (reservationID <= 0
                || guestID <= 0
                || roomID <= 0
                || quantity <= 0
                || reservationDate == null
                || reservationDate.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Invalid input. Ensure all fields are filled "
                    + "and IDs are valid.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Pass data to the model for updating the reservation
        MReservation mreservation = new MReservation();

        mreservation.updateReservation(
                reservationID,
                guestID,
                roomID,
                quantity,
                totalPrice,
                reservationDate
        );
    }

    // Method to delete a reservation
    public void deleteReservation(int reservationID) {

        if (reservationID <= 0) {

            JOptionPane.showMessageDialog(
                    null,
                    "Invalid Reservation ID. Please check your input.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        MReservation mreservation = new MReservation();

        mreservation.deleteReservation(reservationID);
    }

    // Method to load reservations into the table
    public void loadReservations(DefaultTableModel model) {

        MReservation mreservation = new MReservation();

        mreservation.loadReservations(model);
    }

    // Method to search a reservation by ID
    public ResultSet searchReservationById(int reservationID)
            throws SQLException {

        MReservation mreservation = new MReservation();

        return mreservation.searchReservationById(reservationID);
    }

    // Method to fetch Room ID
    public int fetchRoomID(String roomName) throws SQLException {

        return model.fetchRoomID(roomName);
    }

    // Method to fetch room price
    public double fetchRoomPrice(int roomID) throws SQLException {

        return model.fetchRoomPrice(roomID);
    }
}
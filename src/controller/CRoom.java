package controller;

import model.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CRoom {

    // Add Room
    public void addRoom(int roomID, String roomName, String roomType,
            double pricePerNight, int quantity) {

        MRoom mroom = new MRoom();

        mroom.addRoom(
                roomID,
                roomName,
                roomType,
                pricePerNight,
                quantity
        );
    }

    // Update Room
    public void updateRoom(int roomID, String roomName, String roomType,
            double pricePerNight, int quantity) {

        if (roomID <= 0
                || roomName == null || roomName.isEmpty()
                || roomType == null || roomType.isEmpty()
                || pricePerNight <= 0
                || quantity < 0) {

            System.out.println(
                    "Invalid input. Ensure all room fields are filled and values are valid."
            );

            return;
        }

        MRoom mroom = new MRoom();

        mroom.updateRoom(
                roomID,
                roomName,
                roomType,
                pricePerNight,
                quantity
        );
    }

    // Delete Room
    public void deleteRoom(int roomID) {

        MRoom mroom = new MRoom();

        mroom.deleteRoom(roomID);
    }

    // Load Rooms
    public void loadRooms(DefaultTableModel model) {

        MRoom mroom = new MRoom();

        mroom.loadRooms(model);
    }

    // Search Room by ID
    public ResultSet searchRoomById(int roomID) throws SQLException {

        MRoom mroom = new MRoom();

        return mroom.searchRoomById(roomID);
    }

    // Check if Room Exists
    public void checkRoomExists(String roomID) {

        if (roomID == null || roomID.isEmpty()) {

            System.out.println(
                    "Invalid room ID. Cannot check existence."
            );

            return;
        }

        MRoom mroom = new MRoom();

        mroom.checkRoomExists(roomID);
    }
}

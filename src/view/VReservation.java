/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Owner
 */
public class VReservation extends javax.swing.JFrame {

    private Connection con;
    private PreparedStatement pst;
    private DefaultTableModel model;

    // Stores Guest Name and corresponding GuestID
    private Map<String, Integer> guestMap = new HashMap<>();

    public VReservation() {
        initComponents();

        connect();

        populateComboBoxes();
        setupRoomSelection();

        clearFields();
        fetchReservations();

        setBounds(230, 90, 835, 490);

        txtReservationID.setEditable(false);
        txtReservationID.setFocusable(false);

        loadNextReservationID();
    }

    private void connect() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/hotel_management",
                    "root",
                    ""
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database Connection Failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadNextReservationID() {

        String sql = "SELECT COALESCE(MAX(ReservationID), 0) + 1 "
                + "AS NextReservationID FROM reservations";

        try (
                Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                txtReservationID.setText(
                        rs.getString("NextReservationID")
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error generating Reservation ID: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateComboBoxes() {

        populateGuestComboBox();
        populateRoomComboBox();
    }

    private void populateGuestComboBox() {

        cmbGuestName.removeAllItems();
        guestMap.clear();

        String sql = "SELECT GuestID, GuestName "
                + "FROM Guests ORDER BY GuestName";

        try (
                PreparedStatement pstmt = con.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                int guestID = rs.getInt("GuestID");
                String guestName = rs.getString("GuestName");

                /*
                 * The dropdown displays only the Guest Name.
                 * The GuestID is stored in guestMap.
                 */
                guestMap.put(guestName, guestID);

                cmbGuestName.addItem(guestName);
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading guests: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateRoomComboBox() {

        cmbRoomName.removeAllItems();

        String sql = "SELECT RoomName FROM room "
                + "ORDER BY RoomName";

        try (
                PreparedStatement pstmt = con.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                cmbRoomName.addItem(
                        rs.getString("RoomName")
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading rooms: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int getSelectedGuestID() {

        String selectedGuest
                = (String) cmbGuestName.getSelectedItem();

        if (selectedGuest == null
                || selectedGuest.trim().isEmpty()) {

            return -1;
        }

        return guestMap.getOrDefault(selectedGuest, -1);
    }

    private int getSelectedRoomID() throws SQLException {

        String roomName
                = (String) cmbRoomName.getSelectedItem();

        if (roomName == null
                || roomName.trim().isEmpty()) {

            return -1;
        }

        String sql = "SELECT RoomID FROM room "
                + "WHERE RoomName = ?";

        try (
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, roomName);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("RoomID");
                }
            }
        }

        return -1;
    }

    private double getRoomPrice(int roomID) throws SQLException {

        double price = 0;

        String sql = "SELECT PricePerNight FROM room "
                + "WHERE RoomID = ?";

        try (
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, roomID);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    price = rs.getDouble("PricePerNight");
                }
            }
        }

        return price;
    }

    private void setupRoomSelection() {

        cmbRoomName.addActionListener(evt -> {

            String roomName
                    = (String) cmbRoomName.getSelectedItem();

            if (roomName == null
                    || roomName.trim().isEmpty()) {

                txtTotalPrice.setText("");
                return;
            }

            try {

                int roomID = getSelectedRoomID();

                if (roomID == -1) {

                    txtTotalPrice.setText("");

                    return;
                }

                double roomPrice = getRoomPrice(roomID);

                txtTotalPrice.setText(
                        String.valueOf(roomPrice)
                );

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error retrieving room price: "
                        + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private void fetchReservations() {

        String query = "SELECT ReservationID, GuestID, "
                + "ReservationDate, RoomID, TotalPrice "
                + "FROM reservations";

        try (
                PreparedStatement pstmt
                = con.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            model
                    = (DefaultTableModel) tblReservation.getModel();

            model.setRowCount(0);

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("ReservationID"),
                    rs.getInt("GuestID"),
                    rs.getDate("ReservationDate"),
                    rs.getInt("RoomID"),
                    rs.getDouble("TotalPrice")
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to retrieve data from "
                    + "Reservations table: "
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addReservation() {

        int guestID = getSelectedGuestID();

        String roomName
                = (String) cmbRoomName.getSelectedItem();

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd");

        String reservationDate
                = dtpdate.getDate() != null
                ? sdf.format(dtpdate.getDate())
                : "";

        if (guestID == -1
                || roomName == null
                || roomName.trim().isEmpty()
                || reservationDate.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            int roomID = getSelectedRoomID();

            if (roomID == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Room selected.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            double roomPrice = getRoomPrice(roomID);

            if (roomPrice <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Room price.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            /*
             * There is no Quantity.
             * The reservation total is the room's price per night.
             */
            double totalValue = roomPrice;

            String sql = "INSERT INTO reservations "
                    + "(GuestID, RoomID, TotalPrice, ReservationDate) "
                    + "VALUES (?, ?, ?, ?)";

            try (
                    PreparedStatement pstmt
                    = con.prepareStatement(sql)) {

                pstmt.setInt(1, guestID);
                pstmt.setInt(2, roomID);
                pstmt.setDouble(3, totalValue);
                pstmt.setString(4, reservationDate);

                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Reservation Added Successfully."
            );

            clearFields();
            fetchReservations();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding reservation: "
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateReservation() {

        String reservationID
                = txtReservationID.getText().trim();

        int guestID = getSelectedGuestID();

        String roomName
                = (String) cmbRoomName.getSelectedItem();

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd");

        String reservationDate
                = dtpdate.getDate() != null
                ? sdf.format(dtpdate.getDate())
                : "";

        if (reservationID.isEmpty()
                || guestID == -1
                || roomName == null
                || roomName.trim().isEmpty()
                || reservationDate.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            int roomID = getSelectedRoomID();

            if (roomID == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Room selected.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            double roomPrice = getRoomPrice(roomID);

            if (roomPrice <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Room price.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            double totalValue = roomPrice;

            String sql = "UPDATE reservations SET "
                    + "GuestID = ?, "
                    + "RoomID = ?, "
                    + "TotalPrice = ?, "
                    + "ReservationDate = ? "
                    + "WHERE ReservationID = ?";

            try (
                    PreparedStatement pstmt
                    = con.prepareStatement(sql)) {

                pstmt.setInt(1, guestID);
                pstmt.setInt(2, roomID);
                pstmt.setDouble(3, totalValue);
                pstmt.setString(4, reservationDate);
                pstmt.setInt(
                        5,
                        Integer.parseInt(reservationID)
                );

                int rowsAffected
                        = pstmt.executeUpdate();

                if (rowsAffected > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Reservation Updated Successfully."
                    );

                    clearFields();
                    fetchReservations();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Reservation ID not found.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to update reservation: "
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Reservation ID.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void deleteReservation() {

        String reservationID
                = txtReservationID.getText().trim();

        if (reservationID.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a Reservation ID to delete.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            String sql = "DELETE FROM reservations "
                    + "WHERE ReservationID = ?";

            try (
                    PreparedStatement pstmt
                    = con.prepareStatement(sql)) {

                pstmt.setInt(
                        1,
                        Integer.parseInt(reservationID)
                );

                int rowsAffected
                        = pstmt.executeUpdate();

                if (rowsAffected > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Reservation Deleted Successfully."
                    );

                    clearFields();
                    fetchReservations();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "No matching reservation found "
                            + "to delete.",
                            "Error",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete reservation: "
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Reservation ID.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void clearFields() {

        txtReservationID.setText("");

        cmbGuestName.setSelectedIndex(-1);
        cmbRoomName.setSelectedIndex(-1);

        txtTotalPrice.setText("");

        dtpdate.setDate(null);

        loadNextReservationID();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnclose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservation = new javax.swing.JTable();
        btnupdate = new javax.swing.JButton();
        btndelete = new javax.swing.JButton();
        btnadd = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtReservationID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbGuestName = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cmbRoomName = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        dtpdate = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        txtTotalPrice = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnclose.setBackground(new java.awt.Color(51, 51, 51));
        btnclose.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnclose.setForeground(new java.awt.Color(255, 255, 255));
        btnclose.setText("Close");
        btnclose.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        btnclose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnclose, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 30));

        tblReservation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Reservation ID", "Guest ID", "Date", "Room ID", "Total Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblReservation);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 80, 420, 290));

        btnupdate.setBackground(new java.awt.Color(51, 51, 51));
        btnupdate.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnupdate.setForeground(new java.awt.Color(255, 255, 255));
        btnupdate.setText("Update");
        btnupdate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        btnupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnupdateActionPerformed(evt);
            }
        });
        getContentPane().add(btnupdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 340, 90, 30));

        btndelete.setBackground(new java.awt.Color(51, 51, 51));
        btndelete.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btndelete.setForeground(new java.awt.Color(255, 255, 255));
        btndelete.setText("Delete");
        btndelete.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        btndelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndeleteActionPerformed(evt);
            }
        });
        getContentPane().add(btndelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 340, 90, 30));

        btnadd.setBackground(new java.awt.Color(51, 51, 51));
        btnadd.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnadd.setForeground(new java.awt.Color(255, 255, 255));
        btnadd.setText("Add");
        btnadd.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        btnadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddActionPerformed(evt);
            }
        });
        getContentPane().add(btnadd, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 90, 30));

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("RESERVATION DATA");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, -1, -1));

        jLabel10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Reservation ID");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, -1, -1));

        txtReservationID.setBorder(null);
        txtReservationID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReservationIDActionPerformed(evt);
            }
        });
        getContentPane().add(txtReservationID, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 110, 160, 20));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Guest Name");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, -1, -1));

        cmbGuestName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(cmbGuestName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 150, 160, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Room Name");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, -1, -1));

        cmbRoomName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(cmbRoomName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, 160, -1));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Date");
        jLabel5.setRequestFocusEnabled(false);
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, -1, -1));

        dtpdate.setRequestFocusEnabled(false);
        getContentPane().add(dtpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 230, 160, 20));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Total Price");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 270, -1, -1));

        txtTotalPrice.setBorder(null);
        getContentPane().add(txtTotalPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 270, 160, 20));

        jPanel1.setBackground(new java.awt.Color(107, 185, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 310, 210));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hotel/management/system/WhatsApp Image 2024-11-20 at 11.52.37.jpeg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 450));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btncloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncloseActionPerformed
        Dashboard dbForm = new Dashboard();
        dbForm.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btncloseActionPerformed

    private void btnupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupdateActionPerformed
        updateReservation();
    }//GEN-LAST:event_btnupdateActionPerformed

    private void btndeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeleteActionPerformed
        deleteReservation();
    }//GEN-LAST:event_btndeleteActionPerformed

    private void btnaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddActionPerformed
        addReservation();
    }//GEN-LAST:event_btnaddActionPerformed

    private void txtReservationIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReservationIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReservationIDActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VReservation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VReservation().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnadd;
    private javax.swing.JButton btnclose;
    private javax.swing.JButton btndelete;
    private javax.swing.JButton btnupdate;
    private javax.swing.JComboBox<String> cmbGuestName;
    private javax.swing.JComboBox<String> cmbRoomName;
    private org.jdesktop.swingx.JXDatePicker dtpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReservation;
    private javax.swing.JTextField txtReservationID;
    private javax.swing.JTextField txtTotalPrice;
    // End of variables declaration//GEN-END:variables
}

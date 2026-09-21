/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mahis
 */
public class VRoom extends javax.swing.JFrame {

    static final String URL = "jdbc:mysql://localhost:3307/hotel_management";
    static final String USER = "root";
    static final String PASSWORD = "";

    public VRoom() {
        initComponents();

        setBounds(230, 90, 835, 490);

        txtRoomID.setEditable(false);
        txtRoomID.setFocusable(false);

        clearFields();
        loadNextRoomID();
        loadRoomData();

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void loadNextRoomID() {
        String sql = "SELECT COALESCE(MAX(RoomID), 0) + 1 AS NextRoomID FROM room";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                txtRoomID.setText(rs.getString("NextRoomID"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error generating Room ID: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadRoomData() {
        DefaultTableModel model = (DefaultTableModel) tblRoom.getModel();
        model.setRowCount(0);  // Clear existing rows

        String sql = "SELECT * FROM room";  // Change table name to 'room'
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("RoomID"));
                row.add(rs.getString("RoomName"));
                row.add(rs.getString("RoomType"));
                row.add(rs.getString("PricePerNight"));
                row.add(rs.getString("Quantity"));
                model.addRow(row);  // Add row to the table model
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading room data: " + e.getMessage());
        }
    }

    private void addRoom() {
        String roomName = txtRoomName.getText().trim();
        String roomType = txtRoomType.getText().trim();
        String pricePerNightStr = txtPrice.getText().trim();
        String quantityStr = txtQty.getText().trim();

        // Input validation
        if (roomName.isEmpty()
                || roomType.isEmpty()
                || pricePerNightStr.isEmpty()
                || quantityStr.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            double pricePerNight = Double.parseDouble(pricePerNightStr);
            int quantity = Integer.parseInt(quantityStr);

            if (pricePerNight <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Price per night must be a positive number.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Quantity must be a positive number.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String sql = "INSERT INTO room "
                    + "(RoomName, RoomType, PricePerNight, Quantity) "
                    + "VALUES (?, ?, ?, ?)";

            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, roomName);
                pstmt.setString(2, roomType);
                pstmt.setDouble(3, pricePerNight);
                pstmt.setInt(4, quantity);

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Room added successfully."
                );

                loadRoomData();
                clearFields();
                loadNextRoomID();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for Price per Night and Quantity.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error adding room: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Method to update an existing course in the database
    private void updateRoom() {
        String roomID = txtRoomID.getText().trim();
        String roomName = txtRoomName.getText().trim();
        String roomType = txtRoomType.getText().trim();
        String pricePerNightStr = txtPrice.getText().trim();
        String quantityStr = txtQty.getText().trim();

        // Input validation
        if (roomID.isEmpty() || roomName.isEmpty() || roomType.isEmpty() || pricePerNightStr.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double pricePerNight = Double.parseDouble(pricePerNightStr);
            int quantity = Integer.parseInt(quantityStr);

            if (pricePerNight <= 0) {
                JOptionPane.showMessageDialog(this, "Price per Night must be a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "UPDATE room SET RoomName = ?, RoomType = ?, PricePerNight = ?, Quantity = ? WHERE RoomID = ?";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, roomName);
                pstmt.setString(2, roomType);
                pstmt.setDouble(3, pricePerNight);
                pstmt.setInt(4, quantity);
                pstmt.setString(5, roomID);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Room updated successfully.");
                    loadRoomData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Room ID not found.", "Update Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Price per Night and Quantity.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to delete a course from the database
    private void deleteRoom() {
        String roomID = txtRoomID.getText().trim();

        // Input validation
        if (roomID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Room ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM room WHERE RoomID = ?";  // Change table name to 'room'
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully.");
                loadRoomData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Room ID not found.", "Delete Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtRoomID = new javax.swing.JTextField();
        txtRoomName = new javax.swing.JTextField();
        txtRoomType = new javax.swing.JTextField();
        btnclose = new javax.swing.JButton();
        btnupdate = new javax.swing.JButton();
        btndelete = new javax.swing.JButton();
        btnadd = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        txtQty = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRoom = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("HOTEL ROOM DATA");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Room ID");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, -1, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Room Type");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, -1, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Room Name");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        txtRoomID.setBorder(null);
        txtRoomID.setSelectionColor(new java.awt.Color(237, 51, 5));
        txtRoomID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRoomIDActionPerformed(evt);
            }
        });
        getContentPane().add(txtRoomID, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 130, 160, 20));

        txtRoomName.setBorder(null);
        getContentPane().add(txtRoomName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 160, 20));

        txtRoomType.setBorder(null);
        txtRoomType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRoomTypeActionPerformed(evt);
            }
        });
        getContentPane().add(txtRoomType, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 160, 20));

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
        getContentPane().add(btnupdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 350, 90, 30));

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
        getContentPane().add(btndelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 350, 90, 30));

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
        getContentPane().add(btnadd, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 350, 90, 30));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Price per night");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, -1, -1));

        txtPrice.setBorder(null);
        txtPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPriceActionPerformed(evt);
            }
        });
        getContentPane().add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 250, 160, 20));

        txtQty.setBorder(null);
        txtQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtyActionPerformed(evt);
            }
        });
        getContentPane().add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 290, 160, 20));

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Quantity");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, -1, -1));

        jPanel1.setBackground(new java.awt.Color(107, 185, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 320, 220));

        tblRoom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Room ID", "Room Name", "Room Type", "Price per night", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRoomMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblRoom);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, 420, 290));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hotel/management/system/WhatsApp Image 2024-11-20 at 11.52.37.jpeg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 450));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtRoomIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRoomIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRoomIDActionPerformed

    private void txtRoomTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRoomTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRoomTypeActionPerformed

    private void btncloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncloseActionPerformed
        Dashboard dbForm = new Dashboard();
        dbForm.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btncloseActionPerformed

    private void btnupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupdateActionPerformed
        updateRoom();
    }//GEN-LAST:event_btnupdateActionPerformed

    private void btndeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeleteActionPerformed
        deleteRoom();
    }//GEN-LAST:event_btndeleteActionPerformed

    private void btnaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddActionPerformed
        addRoom();
    }//GEN-LAST:event_btnaddActionPerformed

    private void txtPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPriceActionPerformed

    private void txtQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQtyActionPerformed

    private void tblRoomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRoomMouseClicked
        int selectedRow = tblRoom.getSelectedRow();

        if (selectedRow >= 0) {

            txtRoomID.setText(
                    tblRoom.getValueAt(selectedRow, 0).toString()
            );

            txtRoomName.setText(
                    tblRoom.getValueAt(selectedRow, 1).toString()
            );

            txtRoomType.setText(
                    tblRoom.getValueAt(selectedRow, 2).toString()
            );

            txtPrice.setText(
                    tblRoom.getValueAt(selectedRow, 3).toString()
            );

            txtQty.setText(
                    tblRoom.getValueAt(selectedRow, 4).toString()
            );
        }
    }//GEN-LAST:event_tblRoomMouseClicked

    private void clearFields() {
        txtRoomName.setText("");     // Clear Room Name field
        txtRoomType.setText("");     // Clear Room Type field
        txtPrice.setText(""); // Clear Price per Night field
        txtQty.setText("");      // Clear Quantity field

        loadNextRoomID();
    }

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
            java.util.logging.Logger.getLogger(VRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new VRoom().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnadd;
    private javax.swing.JButton btnclose;
    private javax.swing.JButton btndelete;
    private javax.swing.JButton btnupdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblRoom;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtRoomID;
    private javax.swing.JTextField txtRoomName;
    private javax.swing.JTextField txtRoomType;
    // End of variables declaration//GEN-END:variables
}

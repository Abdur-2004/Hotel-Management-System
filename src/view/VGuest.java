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
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mahis
 */
public class VGuest extends javax.swing.JFrame {

    /**
     * Creates new form Guest
     */
    public VGuest() {
        initComponents();

        setBounds(230, 90, 835, 490);

        txtGuestID.setEditable(false);
        txtGuestID.setFocusable(false);

        clearFields();
        loadNextGuestID();
        loadGuestData();
    }

    static final String URL = "jdbc:mysql://localhost:3307/hotel_management";
    static final String USER = "root";
    static final String PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void loadNextGuestID() {
        String sql = "SELECT COALESCE(MAX(GuestID), 0) + 1 AS NextGuestID FROM Guests";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                txtGuestID.setText(rs.getString("NextGuestID"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error generating Guest ID: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadGuestData() {
        DefaultTableModel model = (DefaultTableModel) tblGuest.getModel();
        model.setRowCount(0);  // Clear existing rows

        String sql = "SELECT * FROM Guests";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("GuestID"));
                row.add(rs.getString("GuestName"));
                row.add(rs.getString("Contact"));
                row.add(rs.getString("Address"));
                row.add(rs.getString("Email"));
                model.addRow(row);  // Add row to the table model
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading guest data: " + e.getMessage());
        }
    }

    private void addGuest() {
        String name = txtname.getText().trim();
        String contact = txtcontact.getText().trim();
        String address = txtaddress.getText().trim();
        String email = txtemail.getText().trim();

        if (name.isEmpty() || contact.isEmpty()
                || address.isEmpty() || email.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all the fields!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql = "INSERT INTO Guests "
                + "(GuestName, Contact, Address, Email) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, address);
            pstmt.setString(4, email);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Guest added successfully."
            );

            clearFields();
            loadGuestData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error adding guest: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateGuest() {
        String guestID = txtGuestID.getText();
        String name = txtname.getText();
        String contact = txtcontact.getText();
        String address = txtaddress.getText();
        String email = txtemail.getText();

        if (guestID.isEmpty() || name.isEmpty() || contact.isEmpty() || address.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (checkGuestExists(guestID)) {
            String sql = "UPDATE Guests SET GuestName = ?, Contact = ?, Address = ?, Email = ? WHERE GuestID = ?";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, contact);
                pstmt.setString(3, address);
                pstmt.setString(4, email);
                pstmt.setString(5, guestID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Guest updated successfully.");
                    clearFields();
                    loadGuestData();
                } else {
                    JOptionPane.showMessageDialog(this, "Guest ID not found.", "Update Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating guest: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Guest ID not found.", "Update Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean checkGuestExists(String guestID) {
        String sql = "SELECT COUNT(*) FROM Guests WHERE GuestID = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guestID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return false;
    }

    private void deleteGuest() {
        String guestID = txtGuestID.getText().trim();

        if (guestID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the Guest ID to delete!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Guests WHERE GuestID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guestID);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Guest deleted successfully.");
                clearFields();
                loadGuestData();
            } else {
                JOptionPane.showMessageDialog(this, "Guest ID not found.", "Deletion Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting guest: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtGuestID = new javax.swing.JTextField();
        txtname = new javax.swing.JTextField();
        txtcontact = new javax.swing.JTextField();
        txtemail = new javax.swing.JTextField();
        txtaddress = new javax.swing.JTextField();
        btnclose = new javax.swing.JButton();
        btnupdate = new javax.swing.JButton();
        btndelete = new javax.swing.JButton();
        btnadd = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGuest = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("HOTEL GUEST DATA");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Guest ID");
        jLabel2.setToolTipText("");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, -1, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Contact");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, -1, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Name");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, -1, -1));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Address");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, -1, -1));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Email");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 270, -1, -1));

        txtGuestID.setBorder(null);
        txtGuestID.setSelectionColor(new java.awt.Color(237, 51, 5));
        txtGuestID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGuestIDActionPerformed(evt);
            }
        });
        getContentPane().add(txtGuestID, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 160, 20));

        txtname.setBorder(null);
        getContentPane().add(txtname, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 160, 20));

        txtcontact.setBorder(null);
        txtcontact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcontactActionPerformed(evt);
            }
        });
        getContentPane().add(txtcontact, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 190, 160, 20));

        txtemail.setBorder(null);
        getContentPane().add(txtemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 270, 160, 20));

        txtaddress.setBorder(null);
        txtaddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtaddressActionPerformed(evt);
            }
        });
        getContentPane().add(txtaddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, 160, 20));

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

        jPanel1.setBackground(new java.awt.Color(107, 185, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 330, 220));

        tblGuest.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Guest ID", "Guest Name", "Contact", "Address", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tblGuest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblGuestMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblGuest);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 90, 420, 280));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hotel/management/system/WhatsApp Image 2024-11-20 at 11.52.37.jpeg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 450));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtGuestIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGuestIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGuestIDActionPerformed

    private void txtcontactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcontactActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcontactActionPerformed

    private void txtaddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtaddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtaddressActionPerformed

    private void btncloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncloseActionPerformed
        Dashboard dbForm = new Dashboard();
        dbForm.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btncloseActionPerformed

    private void btnupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupdateActionPerformed
        updateGuest();
    }//GEN-LAST:event_btnupdateActionPerformed

    private void btndeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeleteActionPerformed
        deleteGuest();
    }//GEN-LAST:event_btndeleteActionPerformed

    private void btnaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddActionPerformed
        addGuest();
    }//GEN-LAST:event_btnaddActionPerformed

    private void tblGuestMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGuestMouseClicked
        int selectedRow = tblGuest.getSelectedRow();

        if (selectedRow >= 0) {
            txtGuestID.setText(
                    tblGuest.getValueAt(selectedRow, 0).toString()
            );

            txtname.setText(
                    tblGuest.getValueAt(selectedRow, 1).toString()
            );

            txtcontact.setText(
                    tblGuest.getValueAt(selectedRow, 2).toString()
            );

            txtaddress.setText(
                    tblGuest.getValueAt(selectedRow, 3).toString()
            );

            txtemail.setText(
                    tblGuest.getValueAt(selectedRow, 4).toString()
            );
        }
    }//GEN-LAST:event_tblGuestMouseClicked

    private void clearFields() {
        txtname.setText("");
        txtcontact.setText("");
        txtaddress.setText("");
        txtemail.setText("");

        loadNextGuestID();
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
            java.util.logging.Logger.getLogger(VGuest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VGuest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VGuest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VGuest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new VGuest().setVisible(true);
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblGuest;
    private javax.swing.JTextField txtGuestID;
    private javax.swing.JTextField txtaddress;
    private javax.swing.JTextField txtcontact;
    private javax.swing.JTextField txtemail;
    private javax.swing.JTextField txtname;
    // End of variables declaration//GEN-END:variables
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotel.management.system;

import view.Login;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Prestige
 */
public class HotelManagementSystem extends JFrame {

    public HotelManagementSystem() {
        setTitle("Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 400);
        setVisible(true);
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Login login = new Login();
        login.setVisible(true);
    }

}

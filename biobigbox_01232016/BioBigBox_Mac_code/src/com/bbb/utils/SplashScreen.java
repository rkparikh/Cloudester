/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 *
 * @author Totaram
 */
/**
 * SplashScreen CLASS USE FOR SPLASH SCREEN
 */
public class SplashScreen extends JWindow {

    private int duration = 8000;

    public SplashScreen(int d) {
        duration = d;
    }

    public void showSplash() {
        JPanel content = (JPanel) getContentPane();
        content.setBackground(Color.white);

        // Set the window's bounds, centering the window
        int width = 350;
        int height = 115;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        // Build the splash screen
        JLabel label = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/biobigboxheader.png")), JLabel.CENTER);
        JLabel copyrt = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/lodingBar.gif")), JLabel.CENTER);
        copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
        content.add(label, BorderLayout.NORTH);
        content.add(copyrt, BorderLayout.CENTER);
        Color oraRed = new Color(30, 144, 255);
        content.setBorder(BorderFactory.createLineBorder(oraRed, 5));

        // Display it
        setVisible(true);

        // Wait a little while, maybe while loading resources
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
        }

        setVisible(false);
    }

    public void showSplashAndExit() {
        showSplash();

    }

}

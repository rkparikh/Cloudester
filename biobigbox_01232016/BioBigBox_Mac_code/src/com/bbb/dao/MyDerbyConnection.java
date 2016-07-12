/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.dao;

import com.bbb.init.Initialize;
import static com.bbb.main.Main.mainAppFrame;
import com.bbb.ui.LoginPanel;
import com.bbb.ui.MainAppFrame;
import com.bbb.ui.ProgressBarDia;
import com.bbb.utils.TrayIconUtils;
import static com.bbb.utils.TrayIconUtils.appIcon;
import static com.bbb.utils.TrayIconUtils.processUtils;
import static com.bbb.utils.TrayIconUtils.setApplicationIcon;
import java.awt.CardLayout;
import java.awt.TrayIcon;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * MyDerbyConnection CLASS USE FOR GET CONNECTION FROM DERBY DATA BASE CREATED
 * IN USER DIRECTORY
 */
public class MyDerbyConnection {

    final static Logger logger = Logger.getLogger(MyDerbyConnection.class);

    /**
     * getMyConnection() METHOD USE FOR GET CONNECTION
     */
    public void getMyConnection() {
        Connection conn = null;
        TrayIcon trayIcon = null;
        String dbURL = "jdbc:derby:" + System.getProperty("user.home") + File.separator + "bbbDB;create=true;user=tr; password=rathore";
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection(dbURL);
            logger.info("Connection Successfully Created");
            Initialize.setConn(conn);
            CreateTable createTable = new CreateTable();
            createTable.createTableLogin();
            createTable.createTableFile();
            createTable.createTableEmail();
        } catch (Exception except) {
            logger.error("Error in create Connection" + except);
            File f = new File(System.getProperty("user.home") + File.separator + "bbbDB");
            f.delete();
            JOptionPane.showMessageDialog(null, "There are some Connectivity issues.\nPlease Sign in again.", "BBB Backup App Login", JOptionPane.INFORMATION_MESSAGE, TrayIconUtils.errImageIcon);
            UpdateLogin.logOutUser();
            Initialize.setLogin_id(0);
            System.out.println("try icon set");
            trayIcon.setImage(appIcon);
            System.out.println("try icon set done");
            MainAppFrame.progressDialog.dispose();
            mainAppFrame.setVisible(true);
            // mainAppFrame.setExtendedState(mainAppFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            setApplicationIcon();
            processUtils.stop();
            setApplicationIcon();
            processUtils = null;
            ProgressBarDia.fileNameUrl.setText("");
            ProgressBarDia.progressSync.setValue(0);
            ProgressBarDia.textareamQueue.setText("");
            // SyncPanel.directorySelectLabel.setText("");
            LoginPanel.userName.setText("");
            LoginPanel.password.setText("");
            Initialize.setDefaultDirectory("");
            CardLayout cardLayout = (CardLayout) mainAppFrame.getContentPane().getLayout();
            cardLayout.show(mainAppFrame.getContentPane(), "loginPanel");
            TrayIconUtils.trayIcon("SignWithoutAdd");
            TrayIconUtils.setApplicationIcon();
        }

    }

}

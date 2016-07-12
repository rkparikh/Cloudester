/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import com.bbb.dao.SelectFilePath;
import com.bbb.dao.UpdateLogin;
import com.bbb.init.Initialize;
import static com.bbb.main.Main.mainAppFrame;
import com.bbb.ui.LoginPanel;
import com.bbb.ui.MainAppFrame;
import com.bbb.ui.ProgressBarDia;
import static com.bbb.ui.ProgressBarDia.fileNameUrl;
import static com.bbb.ui.ProgressBarDia.prgressBarStatus;
import com.bbb.webservice.WebServices;
import java.awt.AWTException;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * TrayIconUtils CLASS USE FOR SHOW TRAY ICON WITH DIFFERNT MENU ITMES
 */
public class TrayIconUtils {

    final static Logger logger = Logger.getLogger(TrayIconUtils.class);

    public static File f = new File(TrayIconUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    public static Image appIcon = Toolkit.getDefaultToolkit().getImage(TrayIconUtils.class.getResource("/bbb.png"));
    public static ImageIcon appImageIcon = new ImageIcon(TrayIconUtils.class.getResource("/biobigboxIcon.png"));
    public static ImageIcon errImageIcon = new ImageIcon(TrayIconUtils.class.getResource("/error1.png"));
    public static ImageIcon folderIcon = new ImageIcon(TrayIconUtils.class.getResource("/folder_icon.png"));
    public static ImageIcon fileIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon.png"));
    public static ImageIcon errorMessage = new ImageIcon(TrayIconUtils.class.getResource("/error.png"));
    public static Image uploadIcon = Toolkit.getDefaultToolkit().getImage(TrayIconUtils.class.getResource("/uploadAnimation.gif"));
    public static Image netErrorIcon = Toolkit.getDefaultToolkit().getImage(TrayIconUtils.class.getResource("/error.png"));
    public static Image errorMssgIcon = Toolkit.getDefaultToolkit().getImage(TrayIconUtils.class.getResource("/ErrorMessage.png"));
    public static Image stopICon = Toolkit.getDefaultToolkit().getImage(TrayIconUtils.class.getResource("/Stop.png"));
    public static Image playIcon = Toolkit.getDefaultToolkit().getImage(TrayIconUtils.class.getResource("/play.png"));
    public static Image uploadWaterIcon = Toolkit.getDefaultToolkit().getImage(TrayIconUtils.class.getResource("/processing.gif"));
    public static TrayIcon trayIcon = new TrayIcon(TrayIconUtils.appIcon, "BBB APP ");
    public static ProcessUtils processUtils = null;

    public static void uploadIconChange() {
        try {
            trayIcon.setImage(uploadIcon);
            trayIcon.setToolTip("BBB App Syncing");

        } catch (Exception ex) {
            logger.error("TrayIconUtils uploadIconChange " + ex);
        }
    }

    public static void setApplicationIcon() {
        try {
            trayIcon.setImage(appIcon);
            trayIcon.setToolTip("BBB App");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void trayIcon(String mode) {
        SystemTray tray = SystemTray.getSystemTray();
        UIManager.put("PopupMenu.consumeEventOnClose", Boolean.FALSE);
        switch (mode) {
            case "Sign": {
                if (SystemTray.isSupported()) {
                    PopupMenu popup = new PopupMenu();
                    trayIcon.setPopupMenu(popup);
                    MenuItem item = new MenuItem("Show BioBigBox Sync");
                    item = new MenuItem("Sign in");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Sign in", "Signin", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Exit from BioBigBox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Exit from Biobigbox", "Exit", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    try {
                        tray.remove(trayIcon);
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        logger.error("Error in TrayIconUtils  syncProcess" + e);
                    }
                }
                break;
            }
            case "SignWithoutAdd": {
                if (SystemTray.isSupported()) {
                    PopupMenu popup = new PopupMenu();
                    trayIcon.setPopupMenu(popup);
                    MenuItem item = new MenuItem("Show BioBigBox Sync");
                    item = new MenuItem("Sign in");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Sign in", "Signin", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Exit from BioBigBox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Exit from Biobigbox", "Exit", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    try {
                        tray.remove(trayIcon);
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        logger.error("Error in TrayIconUtils  syncProcess" + e);
                    }

                }
                break;
            }
            case "Exit": {
                if (SystemTray.isSupported()) {
                    PopupMenu popup = new PopupMenu();
                    trayIcon.setPopupMenu(popup);
                    MenuItem item = new MenuItem("Show BioBigBox Sync");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Show Biobigbox Sync", "Syncdownload", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    item = new MenuItem("Exit from Biobigbox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Exit from Biobigbox", "Exit", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    try {
                        tray.remove(trayIcon);
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        logger.error("Error in TrayIconUtils  syncProcess" + e);
                    }
                }
                break;
            }
            case "Network_Error": {
                PopupMenu popup = new PopupMenu();
                trayIcon.setPopupMenu(popup);
                trayIcon.setImage(errorMssgIcon);
                trayIcon.setToolTip("BBB APP Required Network Connection");
                MenuItem item = new MenuItem("Show BioBigBox Sync");
                item = new MenuItem("Exit from BioBigBox");
                item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                        "Exit from Biobigbox", "Exit", TrayIcon.MessageType.NONE));
                popup.add(item);
                trayIcon.setImageAutoSize(true);
                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    logger.error("Error in TrayIconUtils  syncProcess" + e);
                }
                break;
            }
            case "Sync": {

                if (SystemTray.isSupported()) {
                    BBBUtils.createDirectory(Initialize.getDefaultDirectory());
                    PopupMenu popup = new PopupMenu(Initialize.getUsername());
                    System.out.println("username :-"+Initialize.getUsername());
                    trayIcon.setPopupMenu(popup);
                    MenuItem item = new MenuItem(Initialize.getUsername().toLowerCase());
                    Font font = new Font("Verdana", Font.BOLD, 12);
                    item.setFont(font);
                    popup.add(item);
                    popup.addSeparator();
                    if(Initialize.fileStopStatus==5){
                        item = new MenuItem("Select Syncing Directory");    
                    }else{
                        item = new MenuItem(Initialize.getDefaultDirectory());
                        item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            Initialize.getDefaultDirectory(), "OpenDir", TrayIcon.MessageType.INFO));
                    }           
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Show BioBigBox Sync");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Show Biobigbox Sync", "Sync", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    popup.addSeparator();

                    item = new MenuItem("Pause Syncing with BioBigBox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Pause Syncing with Biobigbox ", "Pause", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Restore Files");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Restore Files ", "Restore", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("View Log");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "View Log", "Log", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    popup.addSeparator();
                    /* item = new MenuItem("Pricing");
                     item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                     "Pricing", "Pricing", TrayIcon.MessageType.NONE));
                     popup.add(item);*/
                    item = new MenuItem("Upgrade Account");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Upgrade Account", "Account", TrayIcon.MessageType.NONE));
                    popup.add(item);

                    /* item = new MenuItem("Send Feedback");
                     item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                     "Send Feedback", "Feedback", TrayIcon.MessageType.NONE));
                     popup.add(item);*/
                    popup.addSeparator();
                    item = new MenuItem("Help");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Help", "Help", TrayIcon.MessageType.NONE));
                    popup.add(item);
                     popup.addSeparator();
                    item = new MenuItem("Update BioBigBox version");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Update BioBigBox version", "updated", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Sign Out");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Sign Out", "SignOut", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Exit from BioBigBox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Exit from BioBigBox", "Exit", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    try {
                        tray.remove(trayIcon);
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        logger.error("Error in TrayIconUtils  syncProcess" + e);
                    }

                }
                break;
            }
            case "Sync1": {

                if (SystemTray.isSupported()) {
                    BBBUtils.createDirectory(Initialize.getDefaultDirectory());
                    PopupMenu popup = new PopupMenu(Initialize.getUsername());
                    trayIcon.setPopupMenu(popup);
                    MenuItem item = new MenuItem(Initialize.getUsername().toLowerCase());
                    Font font = new Font("Verdana", Font.BOLD, 12);
                    item.setFont(font);
                    popup.add(item);
                    popup.addSeparator();
                    if(Initialize.fileStopStatus==5){
                        item = new MenuItem("Select Syncing Directory");    
                    }else{
                        item = new MenuItem(Initialize.getDefaultDirectory());
                         item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            Initialize.getDefaultDirectory(), "OpenDir", TrayIcon.MessageType.INFO));
                    }                    
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Show BioBigBox Sync");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Show Biobigbox Sync", "Sync", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Resume Syncing with BioBigBox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Resume Syncing with Biobigbox ", "Play", TrayIcon.MessageType.INFO));
                    popup.add(item);

                    popup.addSeparator();
                    item = new MenuItem("Restore Files");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Restore Files ", "Restore", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("View Log");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "View Log", "Log", TrayIcon.MessageType.INFO));
                    popup.add(item);
                    popup.addSeparator();
                    /* item = new MenuItem("Pricing");
                     item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                     "Pricing", "Pricing", TrayIcon.MessageType.NONE));
                     popup.add(item);*/
                    item = new MenuItem("Upgrade Account");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Upgrade Account", "Account", TrayIcon.MessageType.NONE));
                    popup.add(item);

                    /* item = new MenuItem("Send Feedback");
                     item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                     "Send Feedback", "Feedback", TrayIcon.MessageType.NONE));
                     popup.add(item);*/
                    popup.addSeparator();
                    item = new MenuItem("Help");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Help", "Help", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Update BioBigBox version");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Update BioBigBox version", "updated", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Sign Out");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Sign Out", "SignOut", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Exit from BioBigBox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Exit from BioBigBox", "Exit", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    try {
                        tray.remove(trayIcon);
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        logger.error("Error in TrayIconUtils  syncProcess" + e);
                    }

                }
                break;
            }
            case "SignOutWithoutDirectory": {

                if (SystemTray.isSupported()) {
                    PopupMenu popup = new PopupMenu(Initialize.getUsername());
                    trayIcon.setPopupMenu(popup);
                    MenuItem item = new MenuItem(Initialize.getUsername());
                    Font font = new Font("Verdana", Font.BOLD, 12);
                    item.setFont(font);
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Sign Out");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Sign Out", "SignOutWithoutDirectory", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    popup.addSeparator();
                    item = new MenuItem("Exit from BioBigBox");
                    item.addActionListener(new TrayIconUtils.ShowMessageListener(trayIcon,
                            "Exit from BioBigBox", "Exit", TrayIcon.MessageType.NONE));
                    popup.add(item);
                    try {
                        tray.remove(trayIcon);
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        logger.error("Error in TrayIconUtils  syncProcess" + e);
                    }

                }
                break;
            }

        }
        trayIcon.setImageAutoSize(true);

    }

    public static void synProcessUpload() {
        logger.error("Syn Process Upload Before ");
        processUtils = new ProcessUtils();
        TrayIconUtils.setApplicationIcon();

        try {
            processUtils.run();
            processUtils.watcherStart();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static class ShowMessageListener implements ActionListener {

        TrayIcon trayIcon;
        String title;
        String message;
        TrayIcon.MessageType messageType;

        ShowMessageListener(
                TrayIcon trayIcon,
                String title,
                String message,
                TrayIcon.MessageType messageType) {
            this.trayIcon = trayIcon;
            this.title = title;
            this.message = message;
            this.messageType = messageType;
        }

        public void actionPerformed(ActionEvent e) {
            // trayIcon.displayMessage(title, message, messageType);

            switch (message) {
                case "Sync": {
                    try {
                        if (Initialize.Uptodatemsg) {
                            JOptionPane.showMessageDialog(null, "Up to date", "BBB Syncing", JOptionPane.INFORMATION_MESSAGE, appImageIcon);

                        } else {
                            fileNameUrl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/upload.gif")));
                            Initialize.progressBarVisible = true;
                            MainAppFrame.progressDialog.setVisible(Initialize.progressBarVisible);
                            prgressBarStatus = "Uploading";
                            logger.info("Show Progress Sync");
                        }

                    } catch (Exception ex) {
                        logger.error("Progress Sync Error " + ex);
                    }
                    break;
                }
                case "Restore": {
                    try {
                        if (!MainAppFrame.restoreDia.downloadProgressBarDia.isVisible() && !MainAppFrame.restoreDia.isVisible()) {
                            MainAppFrame.restoreDia.setDownloadFileList("");
                            MainAppFrame.restoreDia.setVisible(true);
                        }

                    } catch (Exception ex) {
                        logger.error("Progress Sync Error " + ex);
                        ex.printStackTrace();
                    }
                    break;
                }
                case "Syncdownload": {
                    try {
                        Initialize.progressBarVisible = true;
                        MainAppFrame.progressDialog.setVisible(Initialize.progressBarVisible);
                        logger.info("Show Progress Sync");

                    } catch (Exception ex) {
                        logger.error("Progress Sync Error " + ex);
                    }
                    break;
                }
                case "Pause": {
                    if (ProgressBarDia.stop.isEnabled()) {
                        MainAppFrame.progressDialog.uploadStop();

                    }
                    break;
                }
                case "Play": {
                    MainAppFrame.progressDialog.uploadStop();
                    TrayIconUtils.trayIcon("Sync");
                    break;
                }

                case "Log": {
                    String path = BBBUtils.getCurrentPath();
                    try {
                        Desktop.getDesktop().open(new File(System.getProperty("user.home") + File.separator + "BBBLog/log.txt"));
                    } catch (Exception ex) {
                        logger.error(" Log Error " + ex);
                    }
                    break;
                }
                case "Signin": {
//                     Initialize.is_folder_sync=1;
//                    WebServices.updateFolderSync(Initialize.is_folder_sync);
                    logger.info("Main Frme visible ");
                    mainAppFrame.setVisible(true);
                    break;
                }
                case "SignOut": {
//                    Initialize.is_folder_sync=0;
//                    WebServices.updateFolderSync(Initialize.is_folder_sync);
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
                    setApplicationIcon();
                    break;
                }
                case "SignOutWithoutDirectory": {
                    UpdateLogin.logOutUser();
                    Initialize.setLogin_id(0);
                    CardLayout cardLayout = (CardLayout) mainAppFrame.getContentPane().getLayout();
                    cardLayout.show(mainAppFrame.getContentPane(), "loginPanel");
                    TrayIconUtils.trayIcon("SignWithoutAdd");

                    break;
                }
                case "Account": {
                    try {
                        Desktop.getDesktop().browse(new URL("https://biobigbox.com/#/page/pricing").toURI());
                        logger.info("Account");
                    } catch (Exception ex) {
                        logger.error("Account  :" + ex);
                    }
                    break;
                }
                case "restore": {
                    try {
                        Desktop.getDesktop().browse(new URL("http://biobigbox.infinity-stores.co.uk").toURI());
                        logger.info("Account");
                    } catch (Exception ex) {
                        logger.error("Account  :" + ex);
                    }
                    break;
                }

                case "Pricing": {
                    try {
                        Desktop.getDesktop().browse(new URL("https://biobigbox.com/#/page/pricing").toURI());
                        logger.info("Pricing");
                    } catch (Exception ex) {
                        logger.error("Pricing  :" + ex);
                    }
                    break;
                }
                case "Feedback": {
                    try {
                        Desktop.getDesktop().browse(new URL("https://biobigbox.com/#/page/contact").toURI());
                        logger.info("Feedback");
                    } catch (Exception ex) {
                        logger.error("Feedback  :" + ex);
                    }
                    break;
                }
                case "updated":{
                    WebServices.checkUpdatedVersion();
                    break;
                }
                
                case "Help": {
                    try {
                        Desktop.getDesktop().browse(new URL("https://biobigbox.com/#/page/contact").toURI());
                        logger.info("Help");
                    } catch (Exception ex) {
                        logger.error("Help :" + ex);
                    }
                    break;
                }
                case "OpenDir":{
                    try{
                        Desktop.getDesktop().open(new File(Initialize.getDefaultDirectory() + File.separator));
                    }catch(IOException ex){
                        
                    }
                    break;
                } 
                case "Exit": {
                    String parent_id = SelectFilePath.getParentFolderId();
                    Initialize.is_folder_sync=0;
                    WebServices.updateFolderSync(Initialize.is_folder_sync, parent_id);
                    logger.info("Exit Application");
                    System.exit(0);
                    break;
                }
            }
        }
    }
}

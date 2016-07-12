/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.main;

import com.bbb.dao.MyDerbyConnection;
import com.bbb.dao.SelectLogin;
import com.bbb.dao.UpdateLogin;
import com.bbb.init.Initialize;
import com.bbb.ui.MainAppFrame;
import com.bbb.utils.TrayIconUtils;
import com.bbb.webservice.WebServices;
import java.awt.CardLayout;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
public class RegistryCall {

    final static Logger logger = Logger.getLogger(RegistryCall.class);

    public static void main(String[] args) {
        MainAppFrame mainAppFrame = Initialize.getMainAppFrame();
        if (mainAppFrame != null) {
            mainAppFrame.setVisible(true);
            CardLayout cardLayout = (CardLayout) mainAppFrame.getContentPane().getLayout();
            cardLayout.show(mainAppFrame.getContentPane(), "syncPanel");

        } else {
            /*ACCESS DATBASE */
            MyDerbyConnection myDerbyConnection = new MyDerbyConnection();
            myDerbyConnection.getMyConnection();
            mainAppFrame = new MainAppFrame();

        }
       
        /*CHECK FOR ALREADY SAVE CREDIANTIAL*/
        TreeMap<String, String> loginMap = SelectLogin.getLoginCridential();
        if (loginMap != null && loginMap.size() > 0 && loginMap.firstEntry().getKey() != null && loginMap.firstEntry().getValue() != null && WebServices.loginCheck(loginMap.firstEntry().getKey(), loginMap.firstEntry().getValue())) {
            UpdateLogin.updateDirectoryPath();
            WebServices.getAmazonKeys();
            TrayIconUtils.trayIcon("Sync");
            TrayIconUtils.synProcessUpload();
        }
        if (args.length > 0) {
            Initialize.setDefaultDirectory(args[0]);
           // mainAppFrame.syncPanel.directorySelectLabel.setText(args[0]);
            //mainAppFrame.syncPanel.directoryChooser.setVisible(false);
        }

        mainAppFrame.setVisible(true);
        CardLayout cardLayout = (CardLayout) mainAppFrame.getContentPane().getLayout();
        cardLayout.show(mainAppFrame.getContentPane(), "syncPanel");
    }
}

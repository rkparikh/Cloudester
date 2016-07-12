package com.bbb.server;

/**
 *
 * @author Totaram
 */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * This class implements java socket client
 *
 * @author TOTARAM RATHORE
 *
 */
/**
 * BBBClient CLASS USE ON RIGHT CLICK SEND PATH TO BBBServer CLASS
 */
public class BBBClient {

    final static Logger logger = Logger.getLogger(BBBClient.class);

    public static void main(String[] args) {
        try {
            InetAddress host = InetAddress.getLocalHost();
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;

            socket = new Socket(host.getHostName(), 9876);
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            logger.error("Sending request to Socket Server");
            //oos.writeObject("reg~"+"C:\Users\Chandrabhan\Desktop\PICS");
            if (args[0] == null) {
            } else {
                logger.error("Sending request to Socket Server" + args[0]);
                oos.writeObject("reg~" + args[0]);
            }
        } catch (Exception e) {
            logger.error("Sending request to Socket Server" + e);
//            if(e.toString().contains("java.net.ConnectException")){  
//                JOptionPane.showMessageDialog(null, "Connection has been refused, So please exit BioBigBox and run again.", "Error", JOptionPane.ERROR_MESSAGE);
//          }

            e.printStackTrace();
        }
    }
}

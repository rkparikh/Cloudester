package com.bbb.server;

import com.bbb.main.Main;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * This class implements java Socket server
 *
 * @author Totaram Rathore
 *
 */
/**
 * BBBServer CLASS IS USED FOR RUN APPLICATION ON PORT AND GET ARGUMENT FROM
 * BBBClient FOR RIGHT CLICK
 */
public class BBBServer {

    final static Logger logger = Logger.getLogger(BBBServer.class);

    private static ServerSocket server;
    private static int port = 9876;
    private Main main;
    int i = 0;

    private BBBServer() {
    }

    public BBBServer(Main main) {
        this.main = main;
    }

    /**
     * request() METHOD IS USE FOR GET REQUEST FOR CHANGE DEFULAT DIRECTORY PATH
     * AND SET NEW DEFAULT DIRECTORY PATH THAT IS REQUESTING ON RIGHT CLICK
     */
    public void request() {
        System.out.println("i am running.................");
        logger.error("i am running from server");
        try {
            server = new ServerSocket(port);
            int count = 0;
            while (true) {
                count++;
                logger.error("count for server:" + count);
                Socket socket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                String val[] = message.split("~");
                if (val[0].equals("reg")) {
                    logger.error("i am running from server" + val[1]);
                    main.save(val[1]);
                }
                if (val[0].equals("Exit")) {
                    System.exit(0);
                }

                ois.close();
                socket.close();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

            }
            server.close();
        } catch (Exception e) {
//            if(e.toString().contains("java.net.ConnectException")){  
//                JOptionPane.showMessageDialog(null, "Connection has been refused, So please exit BioBigBox and run again.", "Error", JOptionPane.ERROR_MESSAGE);
//          }

        }
    }

}

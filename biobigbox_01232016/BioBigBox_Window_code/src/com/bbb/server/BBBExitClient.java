/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Totaram
 */
public class BBBExitClient {

    public static void main(String args[]) {
        try {
            InetAddress host = InetAddress.getLocalHost();
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;

            socket = new Socket(host.getHostName(), 9876);
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            //oos.writeObject("reg~"+"E:\\Project\\BBB\\Dir1");
            oos.writeObject("Exit");
            //oos.writeObject("reg~" + args[0]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.bean;

import org.apache.log4j.Logger;

/**
 *
 * @author Totaram
 */
/**
 * LoginBean CLASS USE FOR GET AND SET LOGIN INFORMATION
 */
public class LoginBean {

    final static Logger logger = Logger.getLogger(LoginBean.class);

    private String username;
    private String password;
    private String token;
    private String directorypath;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDirectorypath() {
        return directorypath;
    }

    public void setDirectorypath(String directorypath) {
        this.directorypath = directorypath;
    }

}

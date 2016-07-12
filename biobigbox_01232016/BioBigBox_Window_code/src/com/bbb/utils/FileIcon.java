/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import javax.swing.ImageIcon;

/**
 *
 * @author Totaram
 */
public class FileIcon {

    public static ImageIcon setFileIcon(String fileName) {
        ImageIcon imageIcon = null;
        String contentType;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        if (extension.equalsIgnoreCase("PNG")) {
            contentType = "image/png";
        } else if (extension.equalsIgnoreCase("JPEG") || extension.equalsIgnoreCase("JPG")) {
            contentType = "image/jpeg";
        } else if (extension.equalsIgnoreCase("GIF")) {
            contentType = "image/gif";
        } else if (extension.equalsIgnoreCase("BMP")) {
            contentType = "image/bmp";
        } else if (extension.equalsIgnoreCase("TIFF")) {
            contentType = "image/tiff";
        } else if (extension.equalsIgnoreCase("RTF")) {
            contentType = "txt/rtf";
        } else if (extension.equalsIgnoreCase("txt")) {
            contentType = "txt/plain";
        } else if (extension.equalsIgnoreCase("PDF")) {
            contentType = "application/pdf";
        } else if (extension.equalsIgnoreCase("ZIP")) {
            contentType = "application/zip";
        } else if (extension.equalsIgnoreCase("JAR")) {
            contentType = "application/jar";
        } else if (extension.equalsIgnoreCase("EXE")) {
            contentType = "application/exe";
        } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            contentType = "application/msword";
        } else {
            contentType = "application/octet-stream";
        }
        switch (contentType) {
            case "application/msword":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/doc.png"));
                break;
            case "excel":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/excel.png"));
                break;
            case "exe":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/exe.png"));
                break;
            case "image/jpeg":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/image.png"));
                break;
            case "image/gif":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/image.png"));
                break;
            case "image/png":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/image.png"));
                break;
            case "image/bmp":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/image.png"));
                break;
            case "media":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/media.png"));
                break;
            case "application/pdf":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/pdf.png"));
                break;
            case "application/zip":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/zip.png"));
                break;
            case "txt/plain":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/txt.png"));
                break;
                case "application/exe":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/exe.png"));
                break;
            case "application/jar":
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/jar.png"));
                break;
            default:
                imageIcon = new ImageIcon(TrayIconUtils.class.getResource("/file_icon/file_icon.png"));
        }
        return imageIcon;
    }

}

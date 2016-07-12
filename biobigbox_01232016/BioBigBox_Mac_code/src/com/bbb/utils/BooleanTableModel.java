
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

import com.bbb.bean.FileBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Totaram
 */
public class BooleanTableModel extends AbstractTableModel {

    Vector data = null;
    String[] columns = {"", "","FileName", "Size", "Date"};
    int count = 0;

    public String getSizeInMB(Long getBytes) {
        double MB = 1024 * 1024;
        double KB = 1024;

        if (getBytes == null) {
            return "";
        }
        if (getBytes <= KB) {
            return String.format("%.2f", (getBytes / KB)) + " Bytes";
        } else if (getBytes > KB && getBytes < MB) {
            return String.format("%.2f", (getBytes / KB)) + " KB";
        } else if (getBytes >= MB) {
            return String.format("%.2f", (getBytes / MB)) + " MB";
        }
        return "0 Bytes";
    }

    public BooleanTableModel(List<FileBean> fileList) {
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        data = new Vector();
        for (FileBean fileBean : fileList) {
            count++;
            Vector vector = new Vector();
            vector.add(Boolean.FALSE);
            if (fileBean.isFolder()) {
                vector.add(TrayIconUtils.folderIcon);
                vector.add(fileBean.getFile_name());
                
            } else {
                vector.add(FileIcon.setFileIcon(fileBean.getFile_name()));
                vector.add(fileBean.getFile_name());
            }

            vector.add(getSizeInMB(fileBean.getSize()));
            // vector.add(df2.format(new Date(fileBean.getCreated())));
            if (fileBean.isFolder()) {
                //ImageIcon foldericon= new ImageIcon("folder_icon.png");

                vector.add(df2.format(new Date(fileBean.getCreated())));
            } else {
                vector.add(df2.format(new Date(fileBean.getCreated())));
                //vector.add("");
            }

            data.add(vector);

        }
    }

    public int getRowCount() {
        return count;
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Vector rowVector = (Vector) data.get(rowIndex);
        return rowVector.get(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col < 1) {
            return true;
        } else {
            return false;
        }
    }

    //
    // This method is used by the JTable to define the default
    // renderer or editor for each cell. For example if you have
    // a boolean data it will be rendered as a check box. A
    // number value is right aligned.
    //
//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//        return data[0][columnIndex].getClass();
//    }
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return Boolean.class;
            case 1:
                 return Icon.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return String.class;
            default:
                return String.class;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Vector rowVector = (Vector) data.get(row);
        rowVector.set(col, value);

        fireTableCellUpdated(row, col);
    }
}

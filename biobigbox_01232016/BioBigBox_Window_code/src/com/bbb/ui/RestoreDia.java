package com.bbb.ui;

import com.bbb.bean.FileBean;
import static com.bbb.ui.DownloadProgressBarDia.downTextareamQueue;
import javax.swing.UIManager;
import com.bbb.utils.BooleanTableModel;
import com.bbb.utils.TrayIconUtils;
import com.bbb.webservice.DownloadFiles;
import com.bbb.webservice.WebServices;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFileChooser;
import java.util.*;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Totaram
 */
/**
 * ProgressBarDia CLASS USE FOR SHOW PROGRESS BAR
 */
public class RestoreDia extends javax.swing.JFrame {

    /**
     * Creates new form ProgressBarDia
     */
    public static String prgressBarStatus; /*Progress Bar status Downloading and Uploading */

    public static List<FileBean> fileList = null;
    public static List<FileBean> subList;
    public static String downloadDirectory = "";
    public DownloadProgressBarDia downloadProgressBarDia;
    public static List<List<FileBean>> levelNextList;
    public static String folder_id = "";

    public RestoreDia(java.awt.Frame parent, boolean modal) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {

        }
        initComponents();

        login.setVisible(false);
        setIconImage(TrayIconUtils.appIcon);
        subList = new ArrayList<FileBean>();
        levelNextList = new ArrayList<List<FileBean>>();
        downloadProgressBarDia = new DownloadProgressBarDia(this, true);
        downloadProgressBarDia.setVisible(false);
        listTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {

                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();

                    System.out.println("row " + row);
                    System.out.println("columne " + column);
                    FileBean fileBean = fileList.get(row);
                    if (fileBean.isFolder()) {

                        folder_id = fileBean.getId().toString();
                        levelNextList.add(fileList);
                        if (column == 1 || column == 2) {
                            if (levelNextList.size() > 0) {
                                login.setVisible(true);
                            }
                            setDownloadFileList(folder_id);
                        }
                    }
                }
            }
        });

    }

    public void setDownloadFileList(String folder_id) {
        fileList = WebServices.getFilesAndFolderList(folder_id);
        BooleanTableModel model = new BooleanTableModel(fileList);
        listTable.setModel(model);
        listTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(30, 144, 255));
        headerRenderer.setFont(new Font("Verdana", Font.BOLD, 12));
        headerRenderer.setForeground(Color.WHITE);
        TableColumnModel tcm = listTable.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(0);
        tcm.getColumn(0).setMaxWidth(26);
        tcm.getColumn(0).setMinWidth(26);
        tcm.getColumn(1).setPreferredWidth(0);
        tcm.getColumn(1).setMaxWidth(26);
        tcm.getColumn(1).setMinWidth(26);
        tcm.getColumn(2).setPreferredWidth(330);
        tcm.getColumn(3).setPreferredWidth(50);
        tcm.getColumn(4).setPreferredWidth(50);
        TableColumn tc = listTable.getColumnModel().getColumn(0);
        tc.setCellEditor(listTable.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(listTable.getDefaultRenderer(Boolean.class));
        tc.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));
        listTable.getTableHeader().setBackground(new Color(30, 144, 255));
        //listTable.getTableHeader().getColumnModel().getColumn(0).setHeaderRenderer(headerRenderer);
        listTable.getTableHeader().getColumnModel().getColumn(1).setHeaderRenderer(headerRenderer);
        listTable.getTableHeader().getColumnModel().getColumn(2).setHeaderRenderer(headerRenderer);
        listTable.getTableHeader().getColumnModel().getColumn(3).setHeaderRenderer(headerRenderer);
        listTable.getTableHeader().getColumnModel().getColumn(4).setHeaderRenderer(headerRenderer);
//       listTable.getTableHeader().setBorder((BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1), 
//                            BorderFactory.createLineBorder(new Color(30,144,255), 2))));
        //SetIcon(listTable, 1, TrayIconUtils.folderIcon);

    }

    public void SetIcon(JTable table, int col_index, ImageIcon icon) {
        table.getTableHeader().getColumnModel().getColumn(col_index).setCellRenderer(new IconRenderer());
        table.getColumnModel().getColumn(col_index).setHeaderValue(new Imageicon(icon));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        login = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listTable = new javax.swing.JTable();
        login1 = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bbbappversion"); // NOI18N
        setTitle(bundle.getString("bbb.app.version")); // NOI18N
        setResizable(false);

        login.setBackground(new java.awt.Color(30, 144, 255));
        login.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        login.setForeground(new java.awt.Color(255, 255, 255));
        login.setText("Back");
        login.setBorderPainted(false);
        login.setContentAreaFilled(false);
        login.setOpaque(true);
        login.setSelected(true);
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        listTable.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        listTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        listTable.setCellSelectionEnabled(true);
        listTable.setRowHeight(20);
        jScrollPane1.setViewportView(listTable);

        login1.setBackground(new java.awt.Color(30, 144, 255));
        login1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        login1.setForeground(new java.awt.Color(255, 255, 255));
        login1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/downloadarrow.png"))); // NOI18N
        login1.setText("Restore Files");
        login1.setBorderPainted(false);
        login1.setContentAreaFilled(false);
        login1.setOpaque(true);
        login1.setSelected(true);
        login1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                login1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(login1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(login1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public void getAllFilesList(int folderID) {
        List<FileBean> fileListInner = WebServices.getFilesAndFolderList(folderID + "");
        for (FileBean fileBean : fileListInner) {
            if (fileBean.isFolder()) {
                // System.out.println("Restore REcursive Folder ID"+fileBean.isFolder());
                getAllFilesList(fileBean.getId());
            } else {
                subList.add(fileBean);
            }
        }

    }
    private void login1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_login1ActionPerformed
        // TODO add your handling code here:
        downTextareamQueue.setText("");
        subList.clear();
        for (int i = 0; i < listTable.getModel().getRowCount(); i++) {
            Boolean value = (Boolean) listTable.getModel().getValueAt(i, 0);// check state
            if (value) {
                FileBean fileBean = fileList.get(i);
                if (fileBean.isFolder()) {
                    //System.out.println("Restore Folder ID"+fileBean.isFolder());
                    getAllFilesList(fileBean.getId());
                } else {
                    subList.add(fileList.get(i));
                }
                System.out.println(listTable.getModel().getValueAt(i, 1));// second column value
            }
        }
        if (subList == null || subList.size() == 0) {
            JOptionPane.showMessageDialog(this, "Please Select Files or Folders ", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            downloadDirectory = getDirectoryPath();
            if (!downloadDirectory.equals("")) {
                download = true;
                downloadProgressBarDia.downFileCountLabel.setText("0/" + subList.size());
                this.setVisible(false);
                downloadProgressBarDia.setVisible(true);
                new Thread(new RestoreProcessThread()).start();
            } else {
                JOptionPane.showMessageDialog(this, "Please select Directory for Restore files", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_login1ActionPerformed

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        // TODO add your handling code here:

        if (levelNextList.size() > 0) {

            for (int i = 0; i < listTable.getModel().getRowCount(); i++) {
                Boolean value = (Boolean) listTable.getModel().getValueAt(i, 0);// check state
                if (value) {
                    subList.add(fileList.get(i));
                    System.out.println(listTable.getModel().getValueAt(i, 1));// second column value
                }
            }
            fileList = levelNextList.get(levelNextList.size() - 1);
            //System.out.println("File List" + fileList);
            BooleanTableModel model = new BooleanTableModel(fileList);
            listTable.setModel(model);
            levelNextList.remove(levelNextList.size() - 1);
            if (levelNextList.size() == 0) {
                login.setVisible(false);
            }
            TableColumnModel tcm = listTable.getColumnModel();
            TableColumn tc = listTable.getColumnModel().getColumn(0);
            DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
            headerRenderer.setBackground(new Color(30, 144, 255));
            headerRenderer.setFont(new Font("Verdana", Font.BOLD, 12));
            headerRenderer.setForeground(Color.WHITE);
            tcm.getColumn(0).setPreferredWidth(0);
            tcm.getColumn(0).setMaxWidth(26);
            tcm.getColumn(0).setMinWidth(26);
            tcm.getColumn(1).setPreferredWidth(0);
            tcm.getColumn(1).setMaxWidth(26);
            tcm.getColumn(1).setMinWidth(26);
            tcm.getColumn(2).setPreferredWidth(330);
            tcm.getColumn(3).setPreferredWidth(50);
            tcm.getColumn(4).setPreferredWidth(50);
            listTable.getTableHeader().setBackground(new Color(30, 144, 255));
            listTable.getTableHeader().getColumnModel().getColumn(1).setHeaderRenderer(headerRenderer);
            listTable.getTableHeader().getColumnModel().getColumn(2).setHeaderRenderer(headerRenderer);
            listTable.getTableHeader().getColumnModel().getColumn(3).setHeaderRenderer(headerRenderer);
            listTable.getTableHeader().getColumnModel().getColumn(4).setHeaderRenderer(headerRenderer);
            tc.setCellEditor(listTable.getDefaultEditor(Boolean.class));
            tc.setCellRenderer(listTable.getDefaultRenderer(Boolean.class));
            tc.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));

        } else {

        }
    }//GEN-LAST:event_loginActionPerformed

    /*Select Default Directory */
    public String getDirectoryPath() {
        String directory = "";
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Download Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            if (chooser.getSelectedFile() != null) {

                directory = chooser.getSelectedFile().toString();
            }
        }
        return directory;
    }
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable listTable;
    private javax.swing.JButton login;
    private javax.swing.JButton login1;
    // End of variables declaration//GEN-END:variables

    /**
     * // * @param args the command line arguments //
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            //    java.util.logging.Logger.getLogger(RestoreUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            //     java.util.logging.Logger.getLogger(RestoreUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            //    java.util.logging.Logger.getLogger(RestoreUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            //     java.util.logging.Logger.getLogger(RestoreUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new RestoreDia().setVisible(true);
//            }
//        });
//    }
    public static boolean download = true;

    public class RestoreProcessThread implements Runnable {

        public void run() {
            try {
                while (download) {
                    if (subList != null && subList.size() > 0 && DownloadFiles.downLoadFromAmazon(subList, downloadDirectory)) {
                        download = false;
                        downloadProgressBarDia.setVisible(false);
                        System.out.println("i am stop download sync process ");
                        subList.clear();
                        DownloadFiles.tx.shutdownNow();
                    } else {
                        download = false;
                        downloadProgressBarDia.setVisible(false);

                    }

                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    class MyItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            System.out.println("inside checked");
            Object source = e.getSource();
            if (source instanceof AbstractButton == false) {
                return;
            }
            boolean checked = e.getStateChange() == ItemEvent.SELECTED;
            for (int x = 0, y = listTable.getRowCount(); x < y; x++) {
                listTable.setValueAt(new Boolean(checked), x, 0);
            }
        }
    }

    public class IconRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
            Imageicon i = (Imageicon) obj;
            if (obj == i) {
                setIcon(i.imageIcon);
            }
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(JLabel.CENTER);
            return this;
        }
    }

    public class Imageicon {

        ImageIcon imageIcon;

        Imageicon(ImageIcon icon) {
            imageIcon = icon;
        }
    }

}

class CheckBoxHeader extends JCheckBox
        implements TableCellRenderer, MouseListener {

    protected CheckBoxHeader rendererComponent;
    protected int column;
    protected boolean mousePressed = false;

    public CheckBoxHeader(ItemListener itemListener) {
        rendererComponent = this;
        rendererComponent.addItemListener(itemListener);
    }

    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                rendererComponent.setForeground(header.getForeground());
                rendererComponent.setBackground(header.getBackground());
                rendererComponent.setFont(header.getFont());
                rendererComponent.setHorizontalAlignment(10);

                header.addMouseListener(rendererComponent);
            }
        }
        setColumn(column);
        //rendererComponent.setText("Check All");
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return rendererComponent;
    }

    protected void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    protected void handleClickEvent(MouseEvent e) {
        System.out.println("unchecked");
        if (mousePressed) {
            mousePressed = false;
            JTableHeader header = (JTableHeader) (e.getSource());
            JTable tableView = header.getTable();
            TableColumnModel columnModel = tableView.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = tableView.convertColumnIndexToModel(viewColumn);

            if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {
                doClick();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        handleClickEvent(e);
        ((JTableHeader) e.getSource()).repaint();
    }

    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}

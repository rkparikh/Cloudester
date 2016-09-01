package com.bbb.utils;

import java.awt.CheckboxMenuItem;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.bbb.init.Initialize;

/**
 * 
 * @author rahul_parikh
 *
 */
public class LocalSyncUtil {
	public static final String CLOUD = "Cloud";
	public static final String LOCAL = "Local";

	private static class Constants {
		public static final String DRIVE_CHANGE_DIA_LABEL = "What do you want to do with exsting Backup Data?";
		public static final String DRIVE_CHANGE_DIA_DELETE_OPT = "Delete Data from original backup path";
		public static final String DRIVE_CHANGE_DIA_KEEP_OPT = "Keep data as it is";

		public static final String BKP_RESTORE_DIA_LABEL = "What do you want to do with exsting Backup Data?";
		public static final String BKP_RESTORE_DIA_CLOUD_OPT = "Cloud";
		public static final String BKP_RESTORE_DIA_LOCAL_OPT = "Local";

	}

	public static void showDriveChangeDialogue() {

		final JPanel panel = new JPanel(new GridLayout(3, 1));
		final JLabel label = new JLabel(Constants.DRIVE_CHANGE_DIA_LABEL);

		final JRadioButton delete = new JRadioButton(
				Constants.DRIVE_CHANGE_DIA_DELETE_OPT);
		final JRadioButton keep = new JRadioButton(
				Constants.DRIVE_CHANGE_DIA_KEEP_OPT, true);
		ButtonGroup group = new ButtonGroup();

		panel.add(label);
		group.add(delete);

		panel.add(delete);

		group.add(keep);
		panel.add(keep);

		int selectedOption = JOptionPane.showOptionDialog(null, panel,
				"BBB Backup App", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, TrayIconUtils.appImageIcon, null,
				null);
		System.out.println(selectedOption);
		if (selectedOption == 0) {
			if (delete.isSelected()) {
				showDeleteInputDia();
			}
		}
	}

	public static void showDeleteInputDia() {
		String input = (String) JOptionPane
				.showInputDialog(
						null,
						"To Continue, Please enter 'DELETE' in below box and click Ok!!!",
						"BBB Backup App", JOptionPane.WARNING_MESSAGE,
						TrayIconUtils.appImageIcon, null, null);
		if ("DELETE".equals(input)) {
			showDeleteWarningDia();
		} else if (input != null) {
			showDeleteInputDia();
		}
	}

	public static void showDeleteWarningDia() {
		int selectedOption = JOptionPane
				.showOptionDialog(
						null,
						"This actione will not be reversible, do you want to continue?",
						"BBB Backup App", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, null);
		System.out.println(selectedOption);
		if (JOptionPane.OK_OPTION == selectedOption) {
			showExternalDriveSelectionDialogue();
		}
	}

	public static String showBackupRestoreDialogue() {

		if (Initialize.isPaidCustomer() == false) {
			return CLOUD;
		}

		String restoreFrom = null;
		final JPanel panel = new JPanel(new GridLayout(3, 1));
		final JLabel label = new JLabel(Constants.BKP_RESTORE_DIA_LABEL);

		final JRadioButton cloud = new JRadioButton(
				Constants.BKP_RESTORE_DIA_CLOUD_OPT, true);
		final JRadioButton local = new JRadioButton(
				Constants.BKP_RESTORE_DIA_LOCAL_OPT);
		ButtonGroup group = new ButtonGroup();

		panel.add(label);
		group.add(cloud);

		panel.add(cloud);

		group.add(local);
		panel.add(local);

		int selectedOption = JOptionPane.showOptionDialog(null, panel,
				"BBB Backup", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, TrayIconUtils.appImageIcon, null,
				null);
		System.out.println(selectedOption);
		if (JOptionPane.OK_OPTION == selectedOption) {
			if (cloud.isSelected()) {
				restoreFrom = CLOUD;
			} else {
				restoreFrom = LOCAL;
			}
		}

		return restoreFrom;
	}

	public static void showExternalDriveSelectionDialogue() {
		String[] selectionValues = { "C:\\", "D:\\", "E:\\" };
		String initialSelection = "C:\\";
		String selection = (String) JOptionPane.showInputDialog(null,
				"Select Backup Drive:", "BBB App",
				JOptionPane.QUESTION_MESSAGE, TrayIconUtils.appImageIcon,
				selectionValues, initialSelection);
		if (selection != null) {
			Initialize.setCurrentExternalDrive(selection);
		}
	}

	public static void addSyncOptionMenu(PopupMenu popup) {
		// TODO : enable based on paid customer
		Initialize.setPaidCustomer(true);
		if (Initialize.isPaidCustomer() == false) {
			return;
		}

		final Menu syncMenu = new Menu();
		syncMenu.setLabel("Current Sync : Cloud Sync Only");

		final CheckboxMenuItem cloudSyncMenu = new CheckboxMenuItem(
				"Cloud Sync Only", true);
		final CheckboxMenuItem localSyncMenu = new CheckboxMenuItem(
				"Local Sync Only");
		final CheckboxMenuItem syncBothMenu = new CheckboxMenuItem(
				"Sync to Both");

		syncMenu.add(cloudSyncMenu);
		syncMenu.add(localSyncMenu);
		syncMenu.add(syncBothMenu);

		cloudSyncMenu.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (ItemEvent.DESELECTED == e.getStateChange()) {
					cloudSyncMenu.setState(true);
				}
				syncMenu.setLabel("Current Sync : "
						+ ((MenuItem) e.getSource()).getLabel());
				localSyncMenu.setState(false);
				syncBothMenu.setState(false);

			}
		});

		localSyncMenu.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (ItemEvent.DESELECTED == e.getStateChange()) {
					localSyncMenu.setState(true);
				}
				syncMenu.setLabel("Current Sync : "
						+ ((MenuItem) e.getSource()).getLabel());
				handleLocalSyncOPtion();
				cloudSyncMenu.setState(false);
				syncBothMenu.setState(false);
			}
		});

		syncBothMenu.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (ItemEvent.DESELECTED == e.getStateChange()) {
					syncBothMenu.setState(true);
				}
				syncMenu.setLabel("Current Sync : "
						+ ((MenuItem) e.getSource()).getLabel());
				handleLocalSyncOPtion();
				localSyncMenu.setState(false);
				cloudSyncMenu.setState(false);

			}
		});
		popup.add(syncMenu);
		popup.addSeparator();
	}

	private static void handleLocalSyncOPtion() {
		boolean backupDriveSet = Initialize.getCurrentExternalDrive() != null ? true
				: false;
		if (backupDriveSet) {
			int selectedOption = JOptionPane
					.showOptionDialog(
							null,
							"You have already set External Drive for Backup, do want to change it?",
							"BBB Backup App", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE, null, null, null);
			if (JOptionPane.OK_OPTION == selectedOption) {
				showDriveChangeDialogue();
			}
		} else {
			showExternalDriveSelectionDialogue();
		}
	}

}

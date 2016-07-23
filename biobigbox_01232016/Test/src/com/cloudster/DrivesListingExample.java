package com.cloudster;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author www.codejava.net
 *
 */
public class DrivesListingExample {

	public static void main(String[] args) {

		FileSystemView fsv = FileSystemView.getFileSystemView();

		File[] drives = File.listRoots();
		if (drives != null && drives.length > 0) {
			for (File aDrive : drives) {
				System.out.println("Drive Letter: " + aDrive);
				System.out.println("\tType: "
						+ fsv.getSystemTypeDescription(aDrive));
				System.out.println("\tType: "
						+ fsv.isDrive(aDrive));
				System.out.println("\tTotal space: " + aDrive.getTotalSpace());
				System.out.println("\tFree space: " + aDrive.getFreeSpace());
				System.out.println();
			}
		}
	}

	public static void main1(String[] args) {
		System.out
				.println("File system roots returned by   FileSystemView.getFileSystemView():");
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File[] roots = fsv.getRoots();
		for (int i = 0; i < roots.length; i++) {
			System.out.println("Root: " + roots[i]);
		}

		System.out.println("Home directory: " + fsv.getHomeDirectory());

		System.out.println("File system roots returned by File.listRoots():");

		File[] f = File.listRoots();
		for (int i = 0; i < f.length; i++) {
			System.out.println("Drive: " + f[i]);
			System.out.println("Display name: "
					+ fsv.getSystemDisplayName(f[i]));
			System.out.println("Is drive: " + fsv.isDrive(f[i]));
			System.out.println("Is floppy: " + fsv.isFloppyDrive(f[i]));
			System.out.println("Readable: " + f[i].canRead());
			System.out.println("Writable: " + f[i].canWrite());
		}
	}
}

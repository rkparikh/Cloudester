package com.cloudster;
import java.io.*;
import javax.swing.filechooser.*;

public class DriveTypeInfo {
	public static void main(String[] args) {
		System.out
				.println("File system roots returned by   FileSystemView.getFileSystemView():");
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File[] roots = fsv.getRoots();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		for (int i = 0; i < roots.length; i++) {
			System.out.println("Root: " + roots[i]);
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Home directory: " + fsv.getHomeDirectory());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("File system roots returned by File.listRoots():");

		File[] f = File.listRoots();
		for (int i = 0; i < f.length; i++) {
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("Drive: " + f[i]);
			System.out.println("Display name: "
					+ fsv.getSystemDisplayName(f[i]));
			System.out.println("Is drive: " + fsv.isDrive(f[i]));
			System.out.println("Is floppy: " + fsv.isFloppyDrive(f[i]));
			System.out.println("Readable: " + f[i].canRead());
			System.out.println("Writable: " + f[i].canWrite());
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
	}
}
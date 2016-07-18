package com.cloudster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FileEncryption {

	public static void main(String[] args) throws Exception {

		String dir = "C:\\Users\\rahul_parikh\\Devlopment\\HowToGeekFolder\\Test";

		FileInputStream inFile = new FileInputStream(new File(dir,
				"For CAC event.docx"));

		File out = new File(dir, "For CAC event.docx.des");
		if (!out.exists()) {
			out.createNewFile();
		}

		FileOutputStream outFile = new FileOutputStream(out);

		String password = "mypassword";
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory secretKeyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndTripleDES");
		SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

		byte[] salt = new byte[8];
		Random random = new Random();
		random.nextBytes(salt);

		PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
		outFile.write(salt);

		byte[] input = new byte[64];
		int bytesRead;
		while ((bytesRead = inFile.read(input)) != -1) {
			byte[] output = cipher.update(input, 0, bytesRead);
			if (output != null)
				outFile.write(output);
		}

		byte[] output = cipher.doFinal();
		if (output != null)
			outFile.write(output);

		inFile.close();
		outFile.flush();
		outFile.close();

		Process p;
		p = Runtime.getRuntime().exec("attrib +h \"" + out.getPath() + "\"");
		p.waitFor();

	}
}
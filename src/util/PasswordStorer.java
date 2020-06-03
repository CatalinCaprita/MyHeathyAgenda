package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordStorer {
	private static SecureRandom sr = new SecureRandom();
	private static SecretKeySpec sks;
	private static final String secret = "aesEncryptionKey";
	private static final String initVec = "encryptionIntVec";
	private static byte[] bytes;
	private PasswordStorer() {
		
	}
	public static void setSecret() {
		try{
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			bytes = secret.getBytes("UTF-8");
			bytes = sha.digest(bytes);
			bytes = Arrays.copyOf(bytes, 16);
			sks = new SecretKeySpec(bytes,"AES");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static String encrypt(String password) {
		try{
			 IvParameterSpec iv = new IvParameterSpec(initVec.getBytes("UTF-8"));
		        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
		 
		        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		 
		        byte[] encrypted = cipher.doFinal(password.getBytes());
		        return Base64.getEncoder().encodeToString(encrypted);
		}catch(Exception e ) {
			System.out.println("Error while encrypting password" + e.getMessage());
		}
		return null;
	}
	public static String decrypt(String encrPass) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVec.getBytes("UTF-8"));
	        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec,iv);
	         byte[]original = cipher.doFinal(Base64.getDecoder().decode(encrPass));
	         return new String(original);
			
		}catch(Exception e) {
			System.out.println("Error while decrypting password " + e.getMessage());
		}
		return null;
	}
	public static void main(String[] args) {
		String pass = "acadele22";
		String encr = encrypt(pass);
		String decr = decrypt(encr);
		
		System.out.println(pass);
		System.out.println(encr);
		System.out.println(decr);
		
	}
	
}

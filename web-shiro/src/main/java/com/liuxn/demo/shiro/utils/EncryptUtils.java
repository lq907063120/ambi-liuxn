package com.liuxn.demo.shiro.utils;


import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha1Hash;

public class EncryptUtils {

	/**
	 * 生成随机盐
	 *
	 * @return
	 */
	public static String generateSalt() {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		String salt = rng.nextBytes().toString();
		return salt;
	}

	/**
	 * 加密规则
	 *
	 * @param plainPassword
	 * @param salt
	 *
	 * @return hashedPasswordBase64
	 */
	public static String encryptPassword(String plainPassword, String salt) {
		//String hashedPasswordBase64 = new Sha256Hash(plainPassword, salt, 1024).toBase64();
		String hashedPasswordBase64 = new Sha1Hash(plainPassword, "", 1).toString();
		return hashedPasswordBase64;
	}

	public static void main(String[] args) {
		String s = encryptPassword("admin", null);
		System.out.println(s);
	}

}

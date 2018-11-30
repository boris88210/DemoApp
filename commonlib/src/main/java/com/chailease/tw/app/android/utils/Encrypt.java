package com.chailease.tw.app.android.utils;

//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import java.security.InvalidKeyException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.cert.CertificateException;
//import java.security.cert.CertificateFactory;
//import java.security.cert.X509Certificate;
//import java.security.spec.AlgorithmParameterSpec;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.DESKeySpec;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;

/**
 * 字串加密元件
 */
public class Encrypt {

	private static final String DES = "DES";

	/**
	 * DES解密
	 * 
	 * @param src
	 *            數據源
	 * @param key
	 *            密鑰，長度必須是8的倍數
	 * @return 返回解密後的原始數據
	 * @throws Exception
	 */
	private static byte[] decryptDES(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一個可信任的隨機數源
		SecureRandom sr = new SecureRandom();
		// 從原始密匙數據創建一個DESKeySpec對象
		DESKeySpec dks = new DESKeySpec(key);
		// 創建一個密匙工廠，然後用它把DESKeySpec對象轉換成
		// 一個SecretKey對象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher對象實際完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher對象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 現在，獲取數據並解密
		// 正式執行解密操作
		return cipher.doFinal(src);
	}
	/**
	 * DES加密
	 * 
	 * @param src
	 *            數據源
	 * @param key
	 *            密鑰，長度必須是8的倍數
	 * @return 返回加密後的數據
	 * @throws Exception
	 */
	private static byte[] encryptDES(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一個可信任的隨機數源
		SecureRandom sr = new SecureRandom();
		// 從原始密匙數據創建DESKeySpec對象
		DESKeySpec dks = new DESKeySpec(key);
		// 創建一個密匙工廠，然後用它把DESKeySpec轉換成
		// 一個SecretKey對象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher對象實際完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher對象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 現在，獲取數據並加密
		// 正式執行加密操作
		return cipher.doFinal(src);
	}

	private static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("長度非偶數");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	private static String byte2hex(byte[] b) { 
		String hs = ""; 
		String stmp = ""; 
		for (int n = 0; n < b.length; n++) { 
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) 
				hs = hs + "0" + stmp; 
			else 
				hs = hs + stmp; 
		} 
		return hs.toUpperCase(); 
	} 

	/**
	 * 密碼加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String cryptKey) {
		try {
			return byte2hex(encryptDES(data.getBytes(), cryptKey.getBytes()));
		} catch (Exception e) {
			LogUtility.e(Encrypt.class.getName(), "encrypt", "data:" + data + "; key:" + cryptKey, e);
		}
		return "";
	}
	
	/**
	 * 密碼解密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data, String cryptKey) {
		if(data==null || data.equals(""))
			return "";
		try {
			return new String(decryptDES(hex2byte(data.getBytes()), cryptKey.getBytes()));
		} catch (Exception e) {
			LogUtility.e(Encrypt.class.getName(), "decrypt", "data:" + data + "; key:" + cryptKey, e);
		}
		return null;
	}

	public static String md5(String src) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(src.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

//	public static PublicKey transToPublicKey(FileInputStream fin) throws CertificateException {
//		PublicKey pk = null;
//		CertificateFactory f;
//		f = CertificateFactory.getInstance("X.509");
//		X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
//		pk = certificate.getPublicKey();
//		return pk;
//	}
//
	public static PublicKey transToPublicKey(InputStream fin) throws CertificateException, CertificateException {
		PublicKey pk = null;
		CertificateFactory f;
		f = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
		pk = certificate.getPublicKey();
		return pk;
	}

	public static byte[] encryptRSA_T(PublicKey key, String src) throws EncryptException {
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
			// 编码前设定编码方式及密钥
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] data = src.getBytes("UTF-8");
			// 传入编码数据并返回编码结果
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptException(e);
		} catch (InvalidKeyException e) {
			throw new EncryptException(e);
		} catch (NoSuchPaddingException e) {
			throw new EncryptException(e);
		} catch (BadPaddingException e) {
			throw new EncryptException(e);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptException(e);
		} catch (UnsupportedEncodingException e) {
			throw new EncryptException(e);
		}
	}
	public static byte[] encryptRSA(PublicKey key, String src) throws EncryptException {
		//每次加密的字节数，不能超过密钥的长度值减去11
		byte[] EncryptionBytes = null;
		if (key != null && src != null && !"".equals(src.trim())) {
			try {
				Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
				byte[] originalText = src.getBytes("UTF-8");
				cipher.init(Cipher.ENCRYPT_MODE, key);
				int inputLen = originalText.length;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int offSet = 0;
				byte[] cache;
				int i = 0;
				// 對數據分段加密
				while (inputLen - offSet > 0) {
					if (inputLen - offSet > 245) {
						cache = cipher.doFinal(originalText, offSet, 245);
					} else {
						cache = cipher.doFinal(originalText, offSet, inputLen - offSet);
					}
					out.write(cache, 0, cache.length);
					i++;
					offSet = i * 245;
				}

				EncryptionBytes = out.toByteArray();
				out.close();
			} catch (IOException e) {
				throw new EncryptException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new EncryptException(e);
			} catch (InvalidKeyException e) {
				throw new EncryptException(e);
			} catch (NoSuchPaddingException e) {
				throw new EncryptException(e);
			} catch (BadPaddingException e) {
				throw new EncryptException(e);
			} catch (IllegalBlockSizeException e) {
				throw new EncryptException(e);
			}
		}
		return EncryptionBytes;
	}

//	public static byte[] EncryptAES(byte[] iv, byte[] key, byte[] text) {
//		try {
//			AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
//			SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
//			Cipher mCipher = null;
//			mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);
//			return mCipher.doFinal(text);
//		} catch (Exception ex) {
//			System.out.println("Exception-->" + ex.getMessage());
//			return null;
//		}
//	}
//
	//AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
	public static byte[] DecryptAES(byte[] iv, byte[] key, byte[] text) throws EncryptException {
		AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
		SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
		try {
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.DECRYPT_MODE,
					mSecretKeySpec,
					mAlgorithmParameterSpec);
			return mCipher.doFinal(text);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptException(e);
		} catch (InvalidKeyException e) {
			throw new EncryptException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new EncryptException(e);
		} catch (NoSuchPaddingException e) {
			throw new EncryptException(e);
		} catch (BadPaddingException e) {
			throw new EncryptException(e);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptException(e);
		}
	}

	public static class EncryptException extends Exception {
		public EncryptException(Exception e) {
			super(e);
		}
	}
}
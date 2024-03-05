package com.anytime.weather.Utils;

import com.anytime.weather.exception.EntityType;
import com.anytime.weather.exception.ExceptionType;
import com.anytime.weather.exception.TWMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class is used to enrypt and decrypr data.
 * 
 *
 */
public class Encryptor {

	private static Logger logger = LoggerFactory.getLogger(Encryptor.class);

	/**
	 * This method is used to encrypt data.
	 * 
	 * @param key        {@link String}
	 * @param initVector {@link String}
	 * @param value      {@link String}
	 * @return {@link String}
	 */
	public static String encrypt(String key, String initVector, String value) {
		logger.info("Entered into encrypt");
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));

			logger.info("Exit from encrypt");
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			logger.error("Error while encrypting data.");
			throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, "Error while encrypting data");
		}
	}

	/**
	 * This method is used to decrypt value.
	 * 
	 * @param key        {@link String}
	 * @param initVector {@link String}
	 * @param encrypted  {@link String}
	 * @return {@link String}
	 */
	public static String decrypt(String key, String initVector, String encrypted) {
		logger.info("Entered into decrypt");
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			logger.info("Exit from decrypt");
			return new String(original, UTF_8);
		} catch (Exception ex) {
			logger.error("Error while decrypting data.");
			throw exception(EntityType.USER, ExceptionType.ENTITY_EXCEPTION, "Error while decrypting data");
		}

	}

	/**
	 * Returns a new RuntimeException
	 *
	 * @param entityType
	 * @param exceptionType
	 * @param args
	 * @return
	 */
	private static RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
		return TWMException.throwException(entityType, exceptionType, args);
	}

}

package com.github.resresd.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

public class RSAKeyPairGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAKeyPairGenerator.class.getSimpleName());
	@Getter
	@Setter
	private PrivateKey privateKey;
	@Getter
	@Setter
	private PublicKey publicKey;

	public RSAKeyPairGenerator() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(4096);
		KeyPair pair = keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	public void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		LOGGER.info("Dir for store is created: {}", f.getParentFile().mkdirs());
		try (FileOutputStream fos = new FileOutputStream(f)) {
			fos.write(key);
			fos.flush();
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
	}
}

package com.github.resresd.games.resresdspace.players;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

import com.github.resresd.utils.RSAKeyPairGenerator;

import lombok.Getter;
import lombok.Setter;

public class Player implements Serializable {

	private static final long serialVersionUID = 5154561528134995928L;
	@Getter
	@Setter
	String uid = UUID.randomUUID().toString();
	@Getter
	@Setter
	String userName = "username-" + UUID.randomUUID().toString();
	@Getter
	@Setter
	transient String privKey;
	@Getter
	@Setter
	String publicKey;

	public void genNew() throws NoSuchAlgorithmException {
		RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
		PublicKey pub = keyPairGenerator.getPublicKey();
		PrivateKey priv = keyPairGenerator.getPrivateKey();
		setPrivKey(Base64.getEncoder().encodeToString(pub.getEncoded()));
		setPublicKey(Base64.getEncoder().encodeToString(priv.getEncoded()));
	}
}

package com.github.resresd.games.resresdspace.players;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

import com.github.resresd.games.resresdspace.races.Race;
import com.github.resresd.utils.RSAKeyPairGenerator;

import lombok.Getter;
import lombok.Setter;

public class Player implements Serializable {

	private static final long serialVersionUID = 5154561528134995928L;
	private @Getter @Setter String uuid = UUID.randomUUID().toString();
	private @Getter @Setter String userName = "username-" + System.currentTimeMillis();

	private @Getter @Setter Race race;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player [uuid=");
		builder.append(uuid);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", race=");
		builder.append(race);
		builder.append("]");
		return builder.toString();
	}

}

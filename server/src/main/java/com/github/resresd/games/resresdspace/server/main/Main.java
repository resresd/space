package com.github.resresd.games.resresdspace.server.main;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.server.Server;

public class Main {
	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		Server server = new Server();
		StaticData.initSecurity();
		server.initConfig();
		server.initData();
		server.initNetwork();
		// START GAME
		server.initGame();
		server.startGame();

		// WAIT FOR CONNECTIONS
		server.startNetwork();
	}
}

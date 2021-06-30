package com.github.resresd.games.resresdspace.client.online.game.init;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.handlers.network.NetworkHandler;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;

public class Main {
	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
		logger.info("main-start");
		StaticData.initSecurity();
		GameHeader.onlinegame.initConfig();
		GameHeader.onlinegame.initData();

		GameHeader.onlinegame.initNetwork();
		GameHeader.onlinegame.startNetwork();
		GameHeader.onlinegame.startGame();

		NetworkHandler.exit();
		ClientGameEngine.rune = true;
		logger.info("main-end");
	}
}

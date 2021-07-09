package com.github.resresd.games.resresdspace.client.online.game.init;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.handlers.network.Network;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;

public class Main {
	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException {
		try {
			Thread.currentThread().setName("main");
			logger.info("main-start");
			StaticData.initSecurity();
			GameHeader.onlinegame.initConfig();
			GameHeader.onlinegame.initData();

			Network.initNetwork();
			Network.startNetwork();
			GameHeader.onlinegame.startGame();

			ClientGameEngine.rune = true;
			Network.exit();
			logger.info("main-end");
		} catch (NoSuchAlgorithmException e) {
			printError(e);
		} catch (IOException e) {
			printError(e);
		} catch (InterruptedException e) {
			printError(e);
		} catch (Exception e) {
			printError(e);
		}
		logger.info("app stoped");
	}

	private static void printError(Exception e) {
		logger.error("error on start: {} ", e);
	}

}

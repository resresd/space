package com.github.resresd.games.resresdspace.server.header;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
import com.github.resresd.games.resresdspace.server.config.ServerConfig;
import com.github.resresd.games.resresdspace.server.engine.ServerEngine;

import lombok.Getter;
import lombok.Setter;

public final class ServerHeader {
	private ServerHeader() {
	}

	@Getter
	private static File rootDir = new File("serverFiles");
	@Getter
	private static File configFile = new File(getRootDir(), "ServerConfig.yml");

	@Getter
	@Setter
	private static ServerConfig serverConfig = new ServerConfig();

	@Getter
	private static ServerEngine serverEngine = new ServerEngine();
	@Getter
	private static CopyOnWriteArrayList<Ship> ships = new CopyOnWriteArrayList<>();
}

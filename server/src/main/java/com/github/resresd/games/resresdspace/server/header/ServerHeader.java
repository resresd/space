package com.github.resresd.games.resresdspace.server.header;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
import com.github.resresd.games.resresdspace.relations.Relation;
import com.github.resresd.games.resresdspace.relations.RelationshipHandler;
import com.github.resresd.games.resresdspace.server.config.ServerConfig;
import com.github.resresd.games.resresdspace.server.engine.ServerEngine;

import lombok.Getter;
import lombok.Setter;

public final class ServerHeader {

	private static final @Getter File rootDir = new File("serverFiles");

	private static final @Getter File dataDir = new File(rootDir, "data");
	private static final @Getter File worldDir = new File(dataDir, "world");
	private static final @Getter File playersDir = new File(dataDir, "players");
	private static final @Getter File configFile = new File(getRootDir(), "ServerConfig.yml");

	private static @Getter @Setter ServerConfig serverConfig = new ServerConfig();

	private static final @Getter ServerEngine serverEngine = new ServerEngine();

	private static final @Getter CopyOnWriteArrayList<Ship> ships = new CopyOnWriteArrayList<>();

	private static final @Getter ConcurrentHashMap<String, Relation> REALTIONS_MAP = new ConcurrentHashMap<>();

	private static final @Getter RelationshipHandler relationHandler = new RelationshipHandler(REALTIONS_MAP);

	private ServerHeader() {
	}
}

package com.github.resresd.games.resresdspace.client.online.game.header;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import com.github.resresd.games.resresdspace.client.online.game.configs.ClientConfig;
import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.objects.SpaceCamera;
import com.github.resresd.games.resresdspace.relations.Relation;
import com.github.resresd.games.resresdspace.relations.RelationshipHandler;

import lombok.Getter;
import lombok.Setter;

public class GameHeader {
	private GameHeader() {
	}

	// максимальная скорость
	private static @Getter float maxLinearVel = 20000.0F;

	private static @Getter float mainThrusterAccFactor = 50.0F;
	private static @Getter float straveThrusterAccFactor = 20.0F;

	private static @Getter boolean[] keyDown = new boolean[GLFW.GLFW_KEY_LAST];

	@Getter
	private static File rootDir = new File("clientFiles");
	@Getter
	private static File configFile = new File(getRootDir(), "ClientConfig.yml");
	@Getter
	@Setter
	public static ClientConfig clientConfig = new ClientConfig();

	// ENGINE
	public static ClientGameEngine onlinegame = new ClientGameEngine();
	// ENGINE

	public static SpaceCamera camera = new SpaceCamera();

	private static @Getter @Setter GLFWKeyCallback keyCallback;
	private static @Getter @Setter GLFWFramebufferSizeCallback fbCallback;

	private static final @Getter ConcurrentHashMap<String, Relation> REALTIONS_MAP = new ConcurrentHashMap<>();
	private static final @Getter RelationshipHandler relationHandler = new RelationshipHandler(REALTIONS_MAP);

}

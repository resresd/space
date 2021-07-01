package com.github.resresd.games.resresdspace.client.online.game.header;

import java.io.File;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import com.github.resresd.games.resresdspace.client.online.game.configs.ClientConfig;
import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.objects.space.entity.SpaceCamera;

import lombok.Getter;
import lombok.Setter;

public class GameHeader {
	private GameHeader() {
	}

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

	// WINDOW HEADER
	// WindowHeader windowHeader = new WindowHeader();
	// WINDOW HEADER

	// CONTROL HEADER
	// @Getter
	// private static ControlHeader controlHeader = new ControlHeader();
	// CONTROL HEADER

	public static SpaceCamera camera = new SpaceCamera();

	@Getter
	private static boolean[] keyDown = new boolean[GLFW.GLFW_KEY_LAST];

	@Getter
	private static float mainThrusterAccFactor = 50.0F;
	@Getter
	private static float straveThrusterAccFactor = 20.0F;
	@Getter

	// максимальная скорость
	private static float maxLinearVel = 20000.0F;

	@Getter
	@Setter
	private static GLFWKeyCallback keyCallback;
	@Getter
	@Setter
	private static GLFWFramebufferSizeCallback fbCallback;

}

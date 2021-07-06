package com.github.resresd.games.resresdspace.client.online.game.header.control;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import lombok.Getter;
import lombok.Setter;

public class MouseHeader {
	private static @Getter @Setter GLFWCursorPosCallback cpCallback;
	@Getter
	@Setter
	static GLFWMouseButtonCallback mbCallback;

	public enum mouseMode {

		SELECTTARGET, ONLYSHOOT, MOVEShot, MENU
	}

	public static @Getter @Setter mouseMode mode = mouseMode.MOVEShot;

	@Getter
	@Setter
	static double mouseX = 0.0D;
	@Getter
	@Setter
	static double mouseY = 0.0D;
	@Getter
	@Setter
	private static boolean leftMouseDown = false;
	@Getter
	@Setter
	private static boolean rightMouseDown = false;

}

package com.github.resresd.games.resresdspace.client.online.game.utils;

import static org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_VRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;

import com.github.resresd.games.resresdspace.client.online.game.header.control.MouseHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.control.MouseHeader.mouseMode;
import com.github.resresd.games.resresdspace.client.online.game.header.window.WindowHeader;

public class MouseUtils {
	static long timeMouse = System.currentTimeMillis() / 1000;

	public static void changeModeNext() {
		mouseMode currentMode = MouseHeader.mode;

		long cur = System.currentTimeMillis() / 1000;
		boolean bool = ((cur - timeMouse) >= 2);
		if (bool) {
			switch (currentMode) {
			case MOVEShot:
				glfwSetCursor(WindowHeader.getWindow(), glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR));
				MouseHeader.mode = mouseMode.SELECTTARGET;
				break;
			case SELECTTARGET:
				glfwSetCursor(WindowHeader.getWindow(), glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR));
				MouseHeader.mode = mouseMode.MENU;

				break;
			case MENU:
				glfwSetCursor(WindowHeader.getWindow(), glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR));
				MouseHeader.setMode(mouseMode.MOVEShot);

				break;
			default:
				break;
			}
			timeMouse = cur;
		}

	}

	public static boolean isModeForShoot() {
		return MouseHeader.mode == mouseMode.ONLYSHOOT || MouseHeader.mode == mouseMode.MOVEShot;
	}

}

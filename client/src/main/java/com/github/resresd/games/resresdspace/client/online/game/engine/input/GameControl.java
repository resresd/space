package com.github.resresd.games.resresdspace.client.online.game.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.control.MouseHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.control.MouseHeader.mouseMode;
import com.github.resresd.games.resresdspace.client.online.game.header.window.WindowHeader;
import com.github.resresd.games.resresdspace.client.online.game.utils.MouseUtils;

public class GameControl {

	static long timeLead = System.currentTimeMillis() / 1000;
	static long timeHUD = System.currentTimeMillis() / 1000;

	public static void updateControls() {
		GameHeader.camera.linearAcc.zero();
		float rotationByZ = 0.0F;

		Vector3f usedForNarmal = new Vector3f();
		if (GameHeader.getKeyDown()[GLFW_KEY_G]) {
			GameHeader.camera.linearAcc.fma(GameHeader.getMainThrusterAccFactor() * 100F,
					GameHeader.camera.forward(usedForNarmal));
		}

		if (GameHeader.getKeyDown()[GLFW_KEY_W]) {
			moveW(usedForNarmal);
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_A]) {
			moveA(usedForNarmal);
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_S]) {
			moveS(usedForNarmal);
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_D]) {
			moveD(usedForNarmal);
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_Q]) {
			rotationByZ = -1.0F;
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_E]) {
			rotationByZ = +1.0F;
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_L]) {
			long cur = System.currentTimeMillis() / 1000;
			boolean bool = ((cur - timeLead) >= 2);
			if (bool) {
				if (WindowHeader.isLeadEnabled()) {
					WindowHeader.setLeadEnabled(false);
				} else {
					WindowHeader.setLeadEnabled(true);
				}
				timeLead = cur;
			}
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_H]) {
			long cur = System.currentTimeMillis() / 1000;
			boolean bool = ((cur - timeHUD) >= 2);
			if (bool) {
				if (WindowHeader.isHUDnabled()) {
					WindowHeader.setHUDnabled(false);
				} else {
					WindowHeader.setHUDnabled(true);
				}
				timeHUD = cur;
			}
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_SPACE]) {
			GameHeader.camera.linearAcc.fma(GameHeader.getStraveThrusterAccFactor(),
					GameHeader.camera.up(usedForNarmal));
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_LEFT_CONTROL]) {
			GameHeader.camera.linearAcc.fma(-GameHeader.getStraveThrusterAccFactor(),
					GameHeader.camera.up(usedForNarmal));
		}

		if (GameHeader.getKeyDown()[GLFW_KEY_B]) {
			MouseUtils.changeModeNext();
		}

		if (MouseHeader.mode == mouseMode.MOVEShot) {
			double mouseX = MouseHeader.getMouseX();
			double mouseY = MouseHeader.getMouseY();

			float num0 = 2.0F;
			double num1 = num0 * mouseY * mouseY * mouseY;
			double num2 = num0 * mouseX * mouseX * mouseX;
			GameHeader.camera.angularAcc.set(num1, num2, rotationByZ);
		} else {
			GameHeader.camera.angularAcc.set(0, 0, rotationByZ);
			double linearVelAbs = GameHeader.camera.linearVel.length();

			if (linearVelAbs > GameHeader.getMaxLinearVel()) {
				GameHeader.camera.linearVel.normalize().mul(GameHeader.getMaxLinearVel());
			}
		}

		if (MouseHeader.isRightMouseDown()) {
		}
	}

	private static void moveA(Vector3f usedForNarmal) {
		GameHeader.camera.linearAcc.fma(-GameHeader.getStraveThrusterAccFactor(),
				GameHeader.camera.right(usedForNarmal));
	}

	private static void moveD(Vector3f usedForNarmal) {
		GameHeader.camera.linearAcc.fma(GameHeader.getStraveThrusterAccFactor(),
				GameHeader.camera.right(usedForNarmal));
	}

	private static void moveS(Vector3f usedForNarmal) {
		GameHeader.camera.linearAcc.fma(-GameHeader.getMainThrusterAccFactor(),
				GameHeader.camera.forward(usedForNarmal));
	}

	private static void moveW(Vector3f usedForNarmal) {
		GameHeader.camera.linearAcc.fma(GameHeader.getMainThrusterAccFactor(),
				GameHeader.camera.forward(usedForNarmal));
	}

	public static void createCallbacks(long window) {
		GameHeader.setKeyCallback(new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_UNKNOWN) {
					return;
				}
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
					glfwSetWindowShouldClose(window, true);
				}
				if (action == GLFW_PRESS || action == GLFW_REPEAT) {
					GameHeader.getKeyDown()[key] = true;
				} else {
					GameHeader.getKeyDown()[key] = false;
				}
			}
		});

		MouseHeader.setMbCallback(new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (button == GLFW_MOUSE_BUTTON_LEFT) {
					if (action == GLFW_PRESS) {
						MouseHeader.setLeftMouseDown(true);
					} else if (action == GLFW_RELEASE) {
						MouseHeader.setLeftMouseDown(false);
					}
				} else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
					if (action == GLFW_PRESS) {
						MouseHeader.setRightMouseDown(true);
					} else if (action == GLFW_RELEASE) {
						MouseHeader.setRightMouseDown(false);
					}
				}
			}
		});

		MouseHeader.setCpCallback(new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				float normX = (float) ((xpos - WindowHeader.getWidth() / 2.0) / WindowHeader.getWidth() * 2.0);
				float normY = (float) ((ypos - WindowHeader.getHeight() / 2.0) / WindowHeader.getHeight() * 2.0);

				MouseHeader.setMouseX(
						Math.max(-WindowHeader.getWidth() / 2.0F, Math.min(WindowHeader.getWidth() / 2.0F, normX)));
				MouseHeader.setMouseY(
						Math.max(-WindowHeader.getHeight() / 2.0F, Math.min(WindowHeader.getHeight() / 2.0F, normY)));
			}
		});

		GameHeader.setFbCallback(new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				if (width > 0 && height > 0
						&& (WindowHeader.getFbWidth() != width || WindowHeader.getFbHeight() != height)) {
					WindowHeader.setFbWidth(width);
					WindowHeader.setFbHeight(height);
				}
			}
		});

		glfwSetCursorPosCallback(window, MouseHeader.getCpCallback());
		glfwSetFramebufferSizeCallback(WindowHeader.getWindow(), GameHeader.getFbCallback());
		glfwSetMouseButtonCallback(window, MouseHeader.getMbCallback());
		glfwSetKeyCallback(window, GameHeader.getKeyCallback());

	}
}

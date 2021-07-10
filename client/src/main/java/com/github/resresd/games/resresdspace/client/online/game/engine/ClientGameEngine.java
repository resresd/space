package com.github.resresd.games.resresdspace.client.online.game.engine;

import static com.github.resresd.games.resresdspace.StaticData.shipNormalVbo;
import static com.github.resresd.games.resresdspace.StaticData.shipPositionVbo;
import static com.github.resresd.games.resresdspace.StaticData.sphereMesh;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.asteroidNormalVbo;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.asteroidPositionVbo;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.createCubemapProgram;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.createFullScreenQuad;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.createProgram;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.createShader;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.drawAsteroids;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.drawCubemap;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.frustumIntersection;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.matrixBuffer;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.modelMatrix;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.projMatrix;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.shipProgram;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.ship_modelUniform;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.ship_projUniform;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.ship_viewUniform;
import static com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders.viewMatrix;
import static org.lwjgl.demo.util.IOUtils.ioResourceToByteBuffer;
import static org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.nglfwGetFramebufferSize;
import static org.lwjgl.opengl.ARBSeamlessCubeMap.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glMultMatrixf;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.GeometryUtils;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.demo.util.WavefrontMeshLoader.Mesh;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.client.online.game.engine.input.GameControl;
import com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders;
import com.github.resresd.games.resresdspace.client.online.game.handlers.network.Network;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.control.MouseHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.window.WindowHeader;
import com.github.resresd.games.resresdspace.client.online.game.utils.ConfigUtils;
import com.github.resresd.games.resresdspace.client.online.game.utils.MouseUtils;
import com.github.resresd.games.resresdspace.gui.menu.CoordUtils;
import com.github.resresd.games.resresdspace.gui.menu.Element;
import com.github.resresd.games.resresdspace.gui.menu.Layout;
import com.github.resresd.games.resresdspace.gui.menu.Menu;
import com.github.resresd.games.resresdspace.objects.SpaceCamera;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.VectorsDataObjectPairD;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Asteroid;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;
import com.github.resresd.games.resresdspace.players.Player;
import com.github.resresd.games.resresdspace.relations.Relation.RelationType;

import lombok.Getter;

public class ClientGameEngine {
	public static boolean rune;

	private static float shotVelocity = 900.0F;

	private static float shotSeparation = 0.8F;
	private static int shotMilliseconds = 20;// ПЕРЕЗАРЯДКА своя

	private static float maxShotLifetime = 4.0F;

	private static float maxParticleLifetime = 1.0F;
	private static float shotSize = 0.5F;
	private static float particleSize = 1.0F;
	private static final int explosionParticles = 40;
	private static final int maxShots = 102400;
	private static final int maxParticles = 4096 * 4;

	private static final @Getter CopyOnWriteArrayList<SpaceEntity> SPACE_ENTITIES = new CopyOnWriteArrayList<>();

	public static CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();

	public static final @Getter CopyOnWriteArrayList<Shot> directShots = new CopyOnWriteArrayList<>();
	public static final CopyOnWriteArrayList<VectorsDataObjectPairD> particles = new CopyOnWriteArrayList<>();

	public static boolean active;

	public static void createShip() {
		Mesh shipMesh = StaticData.getMESHS_MAP().get(Ship.class);

		shipPositionVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, shipPositionVbo);
		glBufferData(GL_ARRAY_BUFFER, shipMesh.positions, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		shipNormalVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, shipNormalVbo);
		glBufferData(GL_ARRAY_BUFFER, shipMesh.normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private int shotProgram;
	private int shot_projUniform;

	private int particleProgram;

	private int particle_projUniform;

	private FloatBuffer shotsVertices = BufferUtils.createFloatBuffer(6 * 6 * maxShots);

	private FloatBuffer particleVerticesFloatBuffer = BufferUtils.createFloatBuffer(6 * 6 * maxParticles);

	private FloatBuffer crosshairVerticesFloatBuffer = BufferUtils.createFloatBuffer(6 * 2);

	private ByteBuffer charBuffer = BufferUtils.createByteBuffer(16 * 270);
	private long lastShotTime = 0L;

	private long lastTime = System.nanoTime();
	private Vector3d tmp = new Vector3d();

	// НУЖЕН ДЛЯ ОПРЕДЕЛЕНИЯ СНАРЯДОВ
	private Vector3d newPosition = new Vector3d();
	// WTF
	private Vector3f tmp3 = new Vector3f();

	// WTF
	private Vector3f tmp4 = new Vector3f();
	private Matrix4f viewProjMatrix = new Matrix4f();
	private Matrix4f invViewMatrix = new Matrix4f();

	private Matrix4f invViewProjMatrix = new Matrix4f();
	private GLCapabilities caps;
	private GLFWWindowSizeCallback wsCallback;

	private Callback debugProc;

	boolean firstShot = false;

	Menu menu = new Menu();

	private void createAsteroid() {
		Mesh asteroidMesh = StaticData.getMESHS_MAP().get(Asteroid.class);

		asteroidPositionVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, asteroidPositionVbo);
		glBufferData(GL_ARRAY_BUFFER, asteroidMesh.positions, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		asteroidNormalVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, asteroidNormalVbo);
		glBufferData(GL_ARRAY_BUFFER, asteroidMesh.normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	// ТЕКСТУРА
	private void createCubemapTexture() throws IOException {
		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, tex);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		ByteBuffer imageBuffer;
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);
		String[] names = { "right", "left", "top", "bottom", "front", "back" };
		ByteBuffer image;
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_GENERATE_MIPMAP, GL_TRUE);
		for (int i = 0; i < 6; i++) {
			imageBuffer = ioResourceToByteBuffer("org/lwjgl/demo/space_" + names[i] + (i + 1) + ".jpg", 8 * 1024);
			if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
				throw new IOException("Failed to read image information: " + stbi_failure_reason());
			}
			image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			if (image == null) {
				throw new IOException("Failed to load image: " + stbi_failure_reason());
			}
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB8, w.get(0), h.get(0), 0, GL_RGB,
					GL_UNSIGNED_BYTE, image);
			stbi_image_free(image);
		}
		if (caps.OpenGL32 || caps.GL_ARB_seamless_cube_map) {
			glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		}
	}

	private void createParticleProgram() throws IOException {
		int vshader = createShader("org/lwjgl/demo/game/particle.vs", GL_VERTEX_SHADER);
		int fshader = createShader("org/lwjgl/demo/game/particle.fs", GL_FRAGMENT_SHADER);
		int program = createProgram(vshader, fshader);
		glUseProgram(program);
		particle_projUniform = glGetUniformLocation(program, "proj");
		glUseProgram(0);
		particleProgram = program;
	}

	private void createShipProgram() throws IOException {
		int vshader = createShader("org/lwjgl/demo/game/ship.vs", GL_VERTEX_SHADER);
		int fshader = createShader("org/lwjgl/demo/game/ship.fs", GL_FRAGMENT_SHADER);
		int program = createProgram(vshader, fshader);
		glUseProgram(program);
		ship_viewUniform = glGetUniformLocation(program, "view");
		ship_projUniform = glGetUniformLocation(program, "proj");
		ship_modelUniform = glGetUniformLocation(program, "model");
		glUseProgram(0);
		shipProgram = program;
	}

	private void createShotProgram() throws IOException {
		int vshader = createShader("org/lwjgl/demo/game/shot.vs", GL_VERTEX_SHADER);
		int fshader = createShader("org/lwjgl/demo/game/shot.fs", GL_FRAGMENT_SHADER);
		int program = createProgram(vshader, fshader);
		glUseProgram(program);
		shot_projUniform = glGetUniformLocation(program, "proj");
		glUseProgram(0);
		shotProgram = program;
	}

	private void drawHeal(SpaceEntity enemyShip, Vector3f targetOrigin, int realWindowHeight, int realWindowWidth) {
		Double currentHealth = enemyShip.getHealth();
		Double currentMax = enemyShip.getHealthMax();

		float crosshairSize2 = 0.04F;
		float xs2 = crosshairSize2 * realWindowHeight / realWindowWidth;
		float ys2 = crosshairSize2;

		// координаты экрана для GL
		float x1 = targetOrigin.x - xs2;// ширина влево
		float y1 = targetOrigin.y + ys2;// вверх (рамка + crosshairSize2)
		float x2 = targetOrigin.x + xs2;// ширина вправо
		float y2 = targetOrigin.y + ys2;// вверх (рамка + crosshairSize2)

		// координаты экрана
		float rx1 = CoordUtils.forRealCoordByGl(x1, realWindowWidth);
		float rx2 = CoordUtils.forRealCoordByGl(x2, realWindowWidth);

		Double maxLen = (double) rx2 - (double) rx1;
		Double proc = 100 * currentHealth / currentMax;
		Double curLen = proc / 100 * maxLen;

		// длина полоски жизни
		float healLen = (float) (rx1 + curLen);
		// длина полоски жизни (-1><1)
		float x2N = CoordUtils.forGlCoordByReal(healLen, realWindowWidth);

		glBegin(GL_LINES);
		glColor4f(1.0F, 0.0F, 0.0F, 0.0F);// red
		glVertex2f(x1, y1);
		glVertex2f(x2N, y2);
		glColor4f(1.0F, 1.0F, 1.0F, 0.0F);// white
		glEnd();
	}

	private void drawHudShip() {
		if (WindowHeader.isHUDnabled()) {

			glUseProgram(0);
			for (SpaceEntity enemyShip : SPACE_ENTITIES) {
				if (enemyShip == null) {
					return;
				}
				Vector3f targetOrigin = StaticData.usedForNarmal;
				SpaceCamera camera = GameHeader.camera;

				double enemyShipPosX = enemyShip.getPosition().x;
				double enemyShipPosY = enemyShip.getPosition().y;
				double enemyShipPosZ = enemyShip.getPosition().z;

				double cameraPositionX = camera.getPosition().x;
				double cameraPositionY = camera.getPosition().y;
				double cameraPositionZ = camera.getPosition().z;

				float posX = (float) (enemyShipPosX - cameraPositionX);
				float posY = (float) (enemyShipPosY - cameraPositionY);
				float posZ = (float) (enemyShipPosZ - cameraPositionZ);
				targetOrigin.set(posX, posY, posZ);

				tmp3.set(StaticData.usedForNarmal);
				viewMatrix.transformPosition(targetOrigin);
				boolean backward = targetOrigin.z > 0.0F;
				if (backward) {
					return;
				}
				projMatrix.transformProject(targetOrigin);
				if (targetOrigin.x < -1.0F) {
					targetOrigin.x = -1.0F;
				}
				if (targetOrigin.x > 1.0F) {
					targetOrigin.x = 1.0F;
				}
				if (targetOrigin.y < -1.0F) {
					targetOrigin.y = -1.0F;
				}
				if (targetOrigin.y > 1.0F) {
					targetOrigin.y = 1.0F;
				}

				int realWindowHeight = WindowHeader.getHeight();
				int realWindowWidth = WindowHeader.getWidth();

				float crosshairSize = 0.03F;
				float xs = crosshairSize * realWindowHeight / realWindowWidth;
				float ys = crosshairSize;
				crosshairVerticesFloatBuffer.clear();
				crosshairVerticesFloatBuffer.put(targetOrigin.x - xs).put(targetOrigin.y - ys);
				crosshairVerticesFloatBuffer.put(targetOrigin.x + xs).put(targetOrigin.y - ys);
				crosshairVerticesFloatBuffer.put(targetOrigin.x + xs).put(targetOrigin.y + ys);
				crosshairVerticesFloatBuffer.put(targetOrigin.x - xs).put(targetOrigin.y + ys);
				crosshairVerticesFloatBuffer.flip();

				Player player = GameHeader.getClientConfig().getPlayer();
				RelationType relation = GameHeader.getRelationHandler().checkRel(player, enemyShip);

				boolean own = false;
				boolean friend = false;
				boolean enemy = false;

				if (relation == RelationType.OWN) {
					own = true;
				} else if (relation == RelationType.FRIEND) {
					friend = true;
				} else if (relation == RelationType.ENEMY) {
					enemy = true;
				}

				if (own) {
					glColor4f(0.0F, 1.0F, 1.0F, 0.0F);// r.Green.blue.a
				} else if (friend) {
					glColor4f(0.0F, 1.0F, 0.0F, 0.0F);// r.Green.b.a
				} else if (enemy) {
					glColor4f(1.0F, 0.0F, 0.0F, 0.0F);// Red.g.b.a
				}

				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				glVertexPointer(2, GL_FLOAT, 0, crosshairVerticesFloatBuffer);
				glDrawArrays(GL_QUADS, 0, 4);
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

				// clear collor
				glColor4f(1.0F, 1.0F, 1.0F, 0.0F);// white

				drawHeal(enemyShip, targetOrigin, realWindowHeight, realWindowWidth);

				// Draw distance text of enemy
				int quads = stb_easy_font_print(0, 0, Integer.toString((int) (tmp3.length())), null, charBuffer);
				glVertexPointer(2, GL_FLOAT, 16, charBuffer);
				glPushMatrix();
				// Scroll
				glTranslatef(targetOrigin.x, targetOrigin.y - crosshairSize * 1.1F, 0F);
				float aspect = (float) WindowHeader.getWidth() / WindowHeader.getHeight();
				glScalef(1.0F / 500.0F, -1.0F / 500.0F * aspect, 0.0F);
				glDrawArrays(GL_QUADS, 0, quads * 4);
				glPopMatrix();
			}
		}
	}

	private void drawHudShotDirection() {
		if (WindowHeader.isLeadEnabled()) {
			try {
				glUseProgram(0);
				for (SpaceEntity enemyShip : SPACE_ENTITIES) {
					if (enemyShip == null) {
						return;
					}

					Vector3d pos = enemyShip.getPosition();
					double shipPosX = pos.x;
					double shipPosY = pos.y;
					double shipPosZ = pos.z;

					SpaceCamera camera = GameHeader.camera;
					Vector3d targetOrigin = tmp;
					targetOrigin.set(shipPosX, shipPosY, shipPosZ);

					Vector3f interceptorDir = StaticData.intercept(camera.getPosition(), shotVelocity, targetOrigin,
							tmp3.set(camera.linearVel).negate(), StaticData.usedForNarmal);

					if (interceptorDir == null) {
						return;
					}
					viewMatrix.transformDirection(interceptorDir);

					if (interceptorDir.z > 0.0) {
						return;
					}
					projMatrix.transformProject(interceptorDir);
					float crosshairSize = 0.01F;
					float xs = crosshairSize * WindowHeader.getHeight() / WindowHeader.getWidth();
					float ys = crosshairSize;

					float intercDirXMxs = interceptorDir.x - xs;
					float intercDirXPxs = interceptorDir.x + xs;

					float intercDirYMys = interceptorDir.y - ys;
					float intercDirYPys = interceptorDir.y + ys;

					crosshairVerticesFloatBuffer.clear();
					crosshairVerticesFloatBuffer.put(intercDirXMxs).put(intercDirYMys);
					crosshairVerticesFloatBuffer.put(intercDirXPxs).put(intercDirYMys);
					crosshairVerticesFloatBuffer.put(intercDirXPxs).put(intercDirYPys);
					crosshairVerticesFloatBuffer.put(intercDirXMxs).put(intercDirYPys);
					crosshairVerticesFloatBuffer.flip();
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
					glVertexPointer(2, GL_FLOAT, 0, crosshairVerticesFloatBuffer);
					glDrawArrays(GL_QUADS, 0, 4);
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				}
			} catch (Exception e) {
				StaticData.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e);
			}
		}
	}

	private void drawMenu() {
		menu.draw();

	}

	private void drawParticles() {
		particleVerticesFloatBuffer.clear();
		int num = 0;

		for (VectorsDataObjectPairD particle : particles) {
			Vector3d particlePosition = particle.getParticlePosition();
			Vector4d particleVelocity = particle.getParticleVelocity();
			if (particleVelocity.w > 0.0F) {
				SpaceCamera camera = GameHeader.camera;

				float x = (float) (particlePosition.x - camera.getPosition().x);
				float y = (float) (particlePosition.y - camera.getPosition().y);
				float z = (float) (particlePosition.z - camera.getPosition().z);
				if (frustumIntersection.testPoint(x, y, z)) {
					float w = (float) particleVelocity.w;
					viewMatrix.transformPosition(StaticData.usedForNarmal.set(x, y, z));

					float a = StaticData.usedForNarmal.x - particleSize;
					float b = StaticData.usedForNarmal.y - particleSize;
					float c = StaticData.usedForNarmal.x + particleSize;
					float d = StaticData.usedForNarmal.y + particleSize;

					float usfnZ = StaticData.usedForNarmal.z;

					particleVerticesFloatBuffer.put(a).put(b).put(usfnZ).put(w).put(-1).put(-1);
					particleVerticesFloatBuffer.put(c).put(b).put(usfnZ).put(w).put(1).put(-1);
					particleVerticesFloatBuffer.put(c).put(d).put(usfnZ).put(w).put(1).put(1);
					particleVerticesFloatBuffer.put(c).put(d).put(usfnZ).put(w).put(1).put(1);
					particleVerticesFloatBuffer.put(a).put(d).put(usfnZ).put(w).put(-1).put(1);
					particleVerticesFloatBuffer.put(a).put(b).put(usfnZ).put(w).put(-1).put(-1);
					num++;
				}
			}
		}
		particleVerticesFloatBuffer.flip();
		if (num > 0) {
			glUseProgram(particleProgram);
			glDepthMask(false);
			glEnable(GL_BLEND);
			glVertexPointer(4, GL_FLOAT, 6 * 4, particleVerticesFloatBuffer);
			particleVerticesFloatBuffer.position(4);
			glTexCoordPointer(2, GL_FLOAT, 6 * 4, particleVerticesFloatBuffer);
			particleVerticesFloatBuffer.position(0);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glDrawArrays(GL_TRIANGLES, 0, num * 6);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisable(GL_BLEND);
			glDepthMask(true);
		}
	}

	private void drawShips() {
		glUseProgram(shipProgram);
		glBindBuffer(GL_ARRAY_BUFFER, shipPositionVbo);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		glEnableClientState(GL_NORMAL_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, shipNormalVbo);
		glNormalPointer(GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		for (SpaceEntity ship : SPACE_ENTITIES) {
			if (ship == null) {
				continue;
			}
			if (!(ship instanceof Ship)) {
				continue;
			}

			SpaceCamera camera = GameHeader.camera;

			float x = (float) (ship.getPosition().x - camera.getPosition().x);
			float y = (float) (ship.getPosition().y - camera.getPosition().y);
			float z = (float) (ship.getPosition().z - camera.getPosition().z);

			float radius = ship.getScale();
			if (frustumIntersection.testSphere(x, y, z, radius)) {
				modelMatrix.translation(x, y, z);
				modelMatrix.scale(radius);

				Mesh shipMesh = StaticData.getMESHS_MAP().get(ship.getClass());
				glUniformMatrix4fv(ship_modelUniform, false, modelMatrix.get(matrixBuffer));
				glDrawArrays(GL_TRIANGLES, 0, shipMesh.numVertices);
			}
		}
		glDisableClientState(GL_NORMAL_ARRAY);
	}

	private void drawShots() {
		shotsVertices.clear();
		int num = 0;

		for (Shot shot : directShots) {
			Vector3d projectilePosition = shot.getPosition();
			Vector4f projectileVelocity = shot.getProjectileVelocity();
			if (projectileVelocity.w > 0.0F) {
				SpaceCamera camera = GameHeader.camera;
				float x = (float) (projectilePosition.x - camera.getPosition().x);
				float y = (float) (projectilePosition.y - camera.getPosition().y);
				float z = (float) (projectilePosition.z - camera.getPosition().z);
				if (frustumIntersection.testPoint(x, y, z)) {
					float w = projectileVelocity.w;
					viewMatrix.transformPosition(StaticData.usedForNarmal.set(x, y, z));

					float a = StaticData.usedForNarmal.x - shotSize;
					float b = StaticData.usedForNarmal.y - shotSize;
					float c = StaticData.usedForNarmal.x + shotSize;
					float d = StaticData.usedForNarmal.y + shotSize;
					float ufnZ = StaticData.usedForNarmal.z;

					shotsVertices.put(a).put(b).put(ufnZ).put(w).put(-1).put(-1);
					shotsVertices.put(c).put(b).put(ufnZ).put(w).put(1).put(-1);
					shotsVertices.put(c).put(d).put(ufnZ).put(w).put(1).put(1);
					shotsVertices.put(c).put(d).put(ufnZ).put(w).put(1).put(1);
					shotsVertices.put(a).put(d).put(ufnZ).put(w).put(-1).put(1);
					shotsVertices.put(a).put(b).put(ufnZ).put(w).put(-1).put(-1);
					num++;
				}
			}
		}
		shotsVertices.flip();
		if (num > 0) {
			glUseProgram(shotProgram);
			glDepthMask(false);
			glEnable(GL_BLEND);
			glVertexPointer(4, GL_FLOAT, 6 * 4, shotsVertices);
			shotsVertices.position(4);
			glTexCoordPointer(2, GL_FLOAT, 6 * 4, shotsVertices);
			shotsVertices.position(0);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glDrawArrays(GL_TRIANGLES, 0, num * 6);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisable(GL_BLEND);
			glDepthMask(true);
		}
	}

	private void drawVelocityCompass() {
		glUseProgram(0);
		glEnable(GL_BLEND);
		glVertexPointer(3, GL_FLOAT, 0, sphereMesh.positions);
		glEnableClientState(GL_NORMAL_ARRAY);
		glNormalPointer(GL_FLOAT, 0, sphereMesh.normals);
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadMatrixf(projMatrix.get(matrixBuffer));
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glTranslatef(0, -1, -4);
		glMultMatrixf(viewMatrix.get(matrixBuffer));
		glScalef(0.3F, 0.3F, 0.3F);
		glColor4f(0.1F, 0.1F, 0.1F, 0.2F);
		glDisable(GL_DEPTH_TEST);
		glDrawArrays(GL_TRIANGLES, 0, sphereMesh.numVertices);
		glEnable(GL_DEPTH_TEST);
		glBegin(GL_LINES);
		glColor4f(1, 0, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(1, 0, 0);
		glColor4f(0, 1, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 1, 0);
		glColor4f(0, 0, 1, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, 1);
		glColor4f(1, 1, 1, 1);
		glVertex3f(0, 0, 0);
		SpaceCamera camera = GameHeader.camera;
		glVertex3f(camera.linearVel.x / GameHeader.getMaxLinearVel(), camera.linearVel.y / GameHeader.getMaxLinearVel(),
				camera.linearVel.z / GameHeader.getMaxLinearVel());
		glEnd();
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisable(GL_BLEND);
	}

	public void emitExplosion(Vector3d p, Vector3f normal) {
		int c = explosionParticles;
		if (normal != null) {
			GeometryUtils.perpendicular(normal, tmp4, tmp3);
		}
		for (int i = 0; i < c; i++) {

			VectorsDataObjectPairD particleDataObjectPairD = new VectorsDataObjectPairD();
			Vector3d particlePosition = particleDataObjectPairD.getParticlePosition();
			Vector4d particleVelocity = particleDataObjectPairD.getParticleVelocity();
			if (particleVelocity.w <= 0.0F) {
				if (normal != null) {
					float r1 = (float) Math.random() * 2.0F - 1.0F;
					float r2 = (float) Math.random() * 2.0F - 1.0F;
					particleVelocity.x = normal.x + r1 * tmp4.x + r2 * tmp3.x;
					particleVelocity.y = normal.y + r1 * tmp4.y + r2 * tmp3.y;
					particleVelocity.z = normal.z + r1 * tmp4.z + r2 * tmp3.z;
				} else {
					float x = (float) Math.random() * 2.0F - 1.0F;
					float y = (float) Math.random() * 2.0F - 1.0F;
					float z = (float) Math.random() * 2.0F - 1.0F;
					particleVelocity.x = x;
					particleVelocity.y = y;
					particleVelocity.z = z;
				}
				particleVelocity.normalize3();
				particleVelocity.mul(140);
				particleVelocity.w = 0.01F;
				particlePosition.set(p);
				if (c-- == 0) {
					break;
				}
			} // IF
			particles.add(particleDataObjectPairD);
		}
	}

	private void init() throws IOException {
		logger.info("init-start");

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode vidmode = glfwGetVideoMode(monitor);
		if (!WindowHeader.isWindowed()) {
			WindowHeader.setWidth(vidmode.width());
			WindowHeader.setHeight(vidmode.height());
			WindowHeader.setFbWidth(WindowHeader.getWidth());
			WindowHeader.setFbHeight(WindowHeader.getHeight());
		}

		long window = glfwCreateWindow(WindowHeader.getWidth(), WindowHeader.getHeight(), WindowHeader.getTitle(),
				!WindowHeader.isWindowed() ? monitor : 0L, NULL);
		WindowHeader.setWindow(window);

		if (WindowHeader.getWindow() == NULL) {
			throw new AssertionError("Failed to create the GLFW window");
		}

		glfwSetCursor(WindowHeader.getWindow(), glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR));

		GameControl.createCallbacks(WindowHeader.getWindow());

		glfwSetWindowSizeCallback(WindowHeader.getWindow(), wsCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {

				if (width > 0 && height > 0
						&& (WindowHeader.getWidth() != width || WindowHeader.getHeight() != height)) {
					WindowHeader.setWidth(width);
					WindowHeader.setHeight(height);
				}
			}
		});

		glfwMakeContextCurrent(WindowHeader.getWindow());// СОЗДАНИЕ КОНТЕКСТА
		glfwSwapInterval(0);
		glfwShowWindow(WindowHeader.getWindow());// ПОКАЗАТЬ ОКНО

		IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
		nglfwGetFramebufferSize(WindowHeader.getWindow(), memAddress(framebufferSize), memAddress(framebufferSize) + 4);

		WindowHeader.setFbWidth(framebufferSize.get(0));
		WindowHeader.setFbHeight(framebufferSize.get(1));

		caps = GL.createCapabilities();
		if (!caps.OpenGL20) {
			throw new AssertionError("Requires OpenGL 2.0.");
		}
		debugProc = GLUtil.setupDebugMessageCallback();

		/* Create all needed GL resources */
		createCubemapTexture();
		createFullScreenQuad();
		createCubemapProgram();

		createShipProgram();
		createShip();
		createParticleProgram();

		createAsteroid();
		createShotProgram();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		logger.info("init-menu-start");

		//
		int h = WindowHeader.getHeight();
		int w = WindowHeader.getWidth();
		//
		menu.setProgram(0);
		menu.setRealWindowHeight(h);
		menu.setRealWindowWidth(w);
		Layout layout = new Layout();
		//
		Element element = new Element(1, 0, 1, h);
		Element element2 = new Element(20, 0, 20, h);

		layout.getElementsHashMap().put(0, element);
		layout.getElementsHashMap().put(1, element2);

		//
		menu.getLayoutsHashMap().put(0, layout);
		logger.info("init-menu-end");

		logger.info("init-end");
	}

	public void initConfig() throws IOException, NoSuchAlgorithmException {
		logger.info("initConfig-start");
		ConfigUtils.initConfig();
		logger.info("initConfig-end");
	}

	public void initData() throws IOException {
		StaticData.init();
	}

	private void loop() {
		logger.info("loop-start");
		ClientGameEngine.active = true;
		while (!glfwWindowShouldClose(WindowHeader.getWindow())) {
			glfwPollEvents();
			glViewport(0, 0, WindowHeader.getFbWidth(), WindowHeader.getFbHeight());
			update();
			render();
			glfwSwapBuffers(WindowHeader.getWindow());
		}
		ClientGameEngine.active = false;
		logger.info("loop-end");
	}

	private void render() {
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
		drawShips();
		drawAsteroids();
		drawCubemap();
		drawShots();
		drawParticles();
		drawHudShotDirection();
		drawHudShip();
		drawVelocityCompass();
		drawMenu();
	}

	private void shoot() {
		// TODO перенести создание выстрела в сервер
		try {
			Shot shot = new Shot();

			Vector3f normal = new Vector3f();

			Vector3d shotPosition = shot.getPosition();
			Vector4f shotVel = shot.getProjectileVelocity();

			//

			double mouseX = MouseHeader.getMouseX();
			double mouseY = MouseHeader.getMouseY();
			SpaceCamera camera = GameHeader.camera;

			invViewProjMatrix.transformProject(normal.set(mouseX, -mouseY, 1.0F)).normalize();

			if (shotVel.w <= 0.0F) {
				shotVel.x = camera.linearVel.x + normal.x * shotVelocity;
				shotVel.y = camera.linearVel.y + normal.y * shotVelocity;
				shotVel.z = camera.linearVel.z + normal.z * shotVelocity;
				shotVel.w = 0.01F;
				if (!firstShot) {
					shotPosition.set(camera.right(tmp3)).mul(shotSeparation).add(camera.getPosition());
					firstShot = true;
				} else {
					shotPosition.set(camera.right(tmp3)).mul(-shotSeparation).add(camera.getPosition());
					firstShot = false;
				}
			}
			//
			directShots.add(shot);
			Network.send(shot);
		} catch (Exception e) {
			StaticData.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e);
		}
	}

	public void startGame() {
		logger.info("startGame-start");

		try {

			init();
			loop();
			if (debugProc != null) {
				debugProc.free();
			}
			GameHeader.getKeyCallback().free();
			MouseHeader.getCpCallback().free();
			MouseHeader.getMbCallback().free();
			GameHeader.getFbCallback().free();
			wsCallback.free();
			glfwDestroyWindow(WindowHeader.getWindow());
		} catch (Throwable t) {
			StaticData.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), t);
		} finally {
			glfwTerminate();
		}
		logger.info("startGame-end");
	}

	private void update() {
		long thisTime = System.nanoTime();
		float deltaTime = (thisTime - lastTime) / 1E9F;
		lastTime = thisTime;
		updateShots(deltaTime);
		updateParticles(deltaTime);

		// TODO ПЕРЕМЕЩАТЬ КАМЕРУ ОТНОСИТЕЛЬНО МЕСТА КАМЕРЫ (ГЛАВНОЕ,ТУРЕЛИ)
		//
		// ПЕРЕМЕЩЕНИЕ КАМЕРЫ НА ДИСТАНЦИЮ ИСХОДЯ ИЗ deltaTime
		SpaceCamera camera = GameHeader.camera;
		camera.update(deltaTime);

		// УСТАНОВКА ПЕРСПЕКТИВЫ
		projMatrix.setPerspective((float) Math.toRadians(40.0F),
				(float) WindowHeader.getWidth() / WindowHeader.getHeight(), 0.1F,
				GameHeader.getClientConfig().getWindowConfig().getDistanceDraw());

		viewMatrix.set(camera.rotation).invert(invViewMatrix);
		viewProjMatrix.set(projMatrix).mul(viewMatrix).invert(invViewProjMatrix);
		frustumIntersection.set(viewProjMatrix);

		/* Update the background shader */
		glUseProgram(GameShaders.getCubemapProgram());
		glUniformMatrix4fv(GameShaders.getCubemap_invViewProjUniform(), false, invViewProjMatrix.get(matrixBuffer));

		/* Update the ship shader */
		glUseProgram(shipProgram);
		glUniformMatrix4fv(ship_viewUniform, false, viewMatrix.get(matrixBuffer));
		glUniformMatrix4fv(ship_projUniform, false, projMatrix.get(matrixBuffer));

		/* Update the shot shader */
		glUseProgram(shotProgram);
		glUniformMatrix4fv(shot_projUniform, false, matrixBuffer);
		/* Update the particle shader */
		glUseProgram(particleProgram);
		glUniformMatrix4fv(particle_projUniform, false, matrixBuffer);

		GameControl.updateControls();

		if (MouseHeader.isLeftMouseDown() && (thisTime - lastShotTime >= 1E6 * shotMilliseconds)) {
			if (MouseUtils.isModeForShoot()) {
				shoot();
				lastShotTime = thisTime;
			}
		}

	}

	// ОБНОВЛЕНИЕ ЧАСТИЦ (И УДАЛЕНИЕ ПРИ .w<0 или .w > maxParticleLifetime)
	private void updateParticles(float deltaTime) {
		for (VectorsDataObjectPairD particle : particles) {
			Vector4d particleVelocity = particle.getParticleVelocity();
			if (particleVelocity.w <= 0.0F) {
				continue;
			}
			particleVelocity.w += deltaTime;
			Vector3d particlePosition = particle.getParticlePosition();
			newPosition.set(particleVelocity.x, particleVelocity.y, particleVelocity.z).mul(deltaTime)
					.add(particlePosition);
			// У частицы истекло время жизни
			if (particleVelocity.w > maxParticleLifetime) {
				particleVelocity.w = 0.0F;
				particles.remove(particle);
				continue;
			}
			particlePosition.set(newPosition);
		}
	}

	private void updateShots(float deltaTime) {
		for (Shot shot : directShots) {
			Vector4f projectileVelocity = shot.getProjectileVelocity();
			if (projectileVelocity.w <= 0.0F) {
				continue;
			}
			projectileVelocity.w += deltaTime;
			Vector3d projectilePosition = shot.getPosition();
			newPosition.set(projectileVelocity.x, projectileVelocity.y, projectileVelocity.z).mul(deltaTime)
					.add(projectilePosition);
			if (projectileVelocity.w > maxShotLifetime) {
				projectileVelocity.w = 0.0F;
				directShots.remove(shot);
				continue;
			}
			projectilePosition.set(newPosition);
		}
	}

}

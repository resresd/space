package com.github.resresd.games.resresdspace.client.online.game.engine;

import static com.github.resresd.games.resresdspace.StaticData.asteroidMesh;
import static com.github.resresd.games.resresdspace.StaticData.broadphase;
import static com.github.resresd.games.resresdspace.StaticData.narrowphase;
import static com.github.resresd.games.resresdspace.StaticData.shipMesh;
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
import java.util.ServiceConfigurationError;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.GeometryUtils;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.client.online.game.configs.ClientConfig;
import com.github.resresd.games.resresdspace.client.online.game.engine.input.GameControl;
import com.github.resresd.games.resresdspace.client.online.game.engine.window.GameShaders;
import com.github.resresd.games.resresdspace.client.online.game.handlers.network.NetworkHandler;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.control.MouseHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.window.WindowHeader;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.VectorsDataObjectPairD;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Asteroid;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;

public class ClientGameEngine {
	Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private boolean windowed = false;

	public static boolean rune;

	private static float shotVelocity = 900.0F;
	private static float shotSeparation = 0.8f;
	private static int shotMilliseconds = 20;// ПЕРЕЗАРЯДКА своя
	private static float maxShotLifetime = 4.0F;
	private static float maxParticleLifetime = 1.0F;
	private static float shotSize = 0.5f;
	private static float particleSize = 1.0F;
	private static final int explosionParticles = 40;
	private static final int maxShots = 102400;
	private static final int maxParticles = 4096 * 4;

	private int shotProgram;
	private int shot_projUniform;

	private int particleProgram;
	private int particle_projUniform;

	public static int asteroidCount = 3;
	private static float shipRadius = 4.0F;

	public static CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Asteroid> localAsteroids = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Shot> directShots = new CopyOnWriteArrayList<>();

	private FloatBuffer shotsVertices = BufferUtils.createFloatBuffer(6 * 6 * maxShots);
	private FloatBuffer particleVertices = BufferUtils.createFloatBuffer(6 * 6 * maxParticles);
	private FloatBuffer crosshairVertices = BufferUtils.createFloatBuffer(6 * 2);

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

	public static CopyOnWriteArrayList<VectorsDataObjectPairD> particles = new CopyOnWriteArrayList<>();

	public void initConfig() throws IOException, NoSuchAlgorithmException {
		logger.info("initConfig-start");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		if (!GameHeader.getConfigFile().exists()) {
			if (!GameHeader.getConfigFile().getParentFile().exists()) {
				GameHeader.getConfigFile().getParentFile().mkdirs();
			}
			if (GameHeader.getConfigFile().createNewFile()) {

			}
			GameHeader.clientConfig.getPlayer().genNew();
			mapper.writeValue(GameHeader.getConfigFile(), GameHeader.clientConfig);
			throw new ServiceConfigurationError("please edit " + GameHeader.getConfigFile().getAbsolutePath());
		}
		GameHeader.setClientConfig(mapper.readValue(GameHeader.getConfigFile(), ClientConfig.class));
		if (!GameHeader.getClientConfig().isPrepare()) {
			throw new ServiceConfigurationError("please edit " + GameHeader.getConfigFile().getAbsolutePath());
		}
		logger.info("initConfig-end");
	}

	public void initNetwork() {
		logger.info("initNetwork-start");
		NetworkHandler.initNetwork();
		logger.info("initNetwork-end");
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
			GameHeader.getControlHeader().getMouseHeader().getCpCallback().free();
			MouseHeader.getMbCallback().free();
			GameHeader.getFbCallback().free();
			wsCallback.free();
			glfwDestroyWindow(WindowHeader.getWindow());
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			glfwTerminate();
		}
		logger.info("startGame-end");
	}

	private void init() throws IOException {
		logger.info("init-start");
		showHelpInfo();

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode vidmode = glfwGetVideoMode(monitor);
		if (!windowed) {
			WindowHeader.setWidth(vidmode.width());
			WindowHeader.setHeight(vidmode.height());
			WindowHeader.setFbWidth(WindowHeader.getWidth());
			WindowHeader.setFbHeight(WindowHeader.getHeight());
		}
		WindowHeader.setWindow(glfwCreateWindow(WindowHeader.getWidth(), WindowHeader.getHeight(),
				WindowHeader.getTitle(), !windowed ? monitor : 0L, NULL));

		if (WindowHeader.getWindow() == NULL) {
			throw new AssertionError("Failed to create the GLFW window");
		}
		glfwSetCursor(WindowHeader.getWindow(), glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR));

		// TODO CREATE CALLBACKS
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
			throw new AssertionError("This demo requires OpenGL 2.0.");
		}
		debugProc = GLUtil.setupDebugMessageCallback();

		/* Create all needed GL resources */
		createCubemapTexture();
		createFullScreenQuad();
		createCubemapProgram();
		createShipProgram();
		createParticleProgram();
		createShip();
		createAsteroid();
		createShotProgram();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		logger.info("init-end");
	}

	public static void createShip() throws IOException {
		shipPositionVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, shipPositionVbo);
		glBufferData(GL_ARRAY_BUFFER, shipMesh.positions, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		shipNormalVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, shipNormalVbo);
		glBufferData(GL_ARRAY_BUFFER, shipMesh.normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void showHelpInfo() {
		logger.info("showHelpInfo-start");
		System.out.println("Press W/S to move forward/backward");
		System.out.println("Press L.Ctrl/Spacebar to move down/up");
		System.out.println("Press A/D to strafe left/right");
		System.out.println("Press Q/E to roll left/right");
		System.out.println("Hold the left mouse button to shoot");
		System.out.println("Hold the right mouse button to rotate towards the mouse cursor");
		logger.info("showHelpInfo-end");
	}

	public void startNetwork() throws InterruptedException {
		NetworkHandler.startNetwork();
	}

	private void createAsteroid() throws IOException {
		asteroidPositionVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, asteroidPositionVbo);
		glBufferData(GL_ARRAY_BUFFER, asteroidMesh.positions, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		asteroidNormalVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, asteroidNormalVbo);
		glBufferData(GL_ARRAY_BUFFER, asteroidMesh.normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
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

	private void createParticleProgram() throws IOException {
		int vshader = createShader("org/lwjgl/demo/game/particle.vs", GL_VERTEX_SHADER);
		int fshader = createShader("org/lwjgl/demo/game/particle.fs", GL_FRAGMENT_SHADER);
		int program = createProgram(vshader, fshader);
		glUseProgram(program);
		particle_projUniform = glGetUniformLocation(program, "proj");
		glUseProgram(0);
		particleProgram = program;
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

	private void update() {
		long thisTime = System.nanoTime();
		float deltaTime = (thisTime - lastTime) / 1E9f;
		lastTime = thisTime;
		updateShots(deltaTime);
		updateParticles(deltaTime);
		// ПЕРЕМЕЩЕНИЕ КАМЕРЫ НА ДИСТАНЦИЮ ИСХОДЯ ИЗ deltaTime
		GameHeader.camera.update(deltaTime);
		// УСТАНОВКА ПЕРСПЕКТИВЫ

		projMatrix.setPerspective((float) Math.toRadians(40.0F),
				(float) WindowHeader.getWidth() / WindowHeader.getHeight(), 0.1f,
				GameHeader.getClientConfig().getWindowConfig().getDistanceDraw());

		viewMatrix.set(GameHeader.camera.rotation).invert(invViewMatrix);
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

		/* Let the player shoot a bullet */
		if (MouseHeader.isLeftMouseDown() && (thisTime - lastShotTime >= 1E6 * shotMilliseconds)) {
			// TODO ОТСЫЛАТЬ ВЫСТРЕЛЫ НА СЕРВЕР
			shoot();
			lastShotTime = thisTime;
		}
		/* Let the opponent shoot a bullet */

	}

	boolean firstShot = false;

	private void shoot() {
		// TODO перенести в сервер
		try {

			Shot shot = new Shot();
			Vector3d shotPosition = shot.getPosition();
			Vector4f shotVel = shot.getProjectileVelocity();

			//
			invViewProjMatrix
					.transformProject(StaticData.tmp2.set(MouseHeader.getMouseX(), -MouseHeader.getMouseY(), 1.0F))
					.normalize();
			if (shotVel.w <= 0.0F) {
				shotVel.x = GameHeader.camera.linearVel.x + StaticData.tmp2.x * shotVelocity;
				shotVel.y = GameHeader.camera.linearVel.y + StaticData.tmp2.y * shotVelocity;
				shotVel.z = GameHeader.camera.linearVel.z + StaticData.tmp2.z * shotVelocity;
				shotVel.w = 0.01f;
				if (!firstShot) {
					shotPosition.set(GameHeader.camera.right(tmp3)).mul(shotSeparation).add(GameHeader.camera.position);
					firstShot = true;
				} else {
					shotPosition.set(GameHeader.camera.right(tmp3)).mul(-shotSeparation)
							.add(GameHeader.camera.position);
					firstShot = false;//
				}
			}
			//
			directShots.add(shot);
			// TODO SEND SHOT IN SERVER
			NetworkHandler.session.write(shot);
		} catch (Exception e) {
			e.printStackTrace();
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

		for (Ship ship : localShips) {
			if (ship == null) {
				continue;
			}
			float x = (float) (ship.getPosition().x - GameHeader.camera.position.x);
			float y = (float) (ship.getPosition().y - GameHeader.camera.position.y);
			float z = (float) (ship.getPosition().z - GameHeader.camera.position.z);
			if (frustumIntersection.testSphere(x, y, z, shipRadius)) {
				modelMatrix.translation(x, y, z);
				modelMatrix.scale(shipRadius);

				glUniformMatrix4fv(ship_modelUniform, false, modelMatrix.get(matrixBuffer));
				glDrawArrays(GL_TRIANGLES, 0, shipMesh.numVertices);
			}
		}
		glDisableClientState(GL_NORMAL_ARRAY);
	}

	private void drawParticles() {
		particleVertices.clear();
		int num = 0;

		for (VectorsDataObjectPairD particle : particles) {
			Vector3d particlePosition = particle.getParticlePosition();
			Vector4d particleVelocity = particle.getParticleVelocity();
			if (particleVelocity.w > 0.0F) {
				float x = (float) (particlePosition.x - GameHeader.camera.position.x);
				float y = (float) (particlePosition.y - GameHeader.camera.position.y);
				float z = (float) (particlePosition.z - GameHeader.camera.position.z);
				if (frustumIntersection.testPoint(x, y, z)) {
					float w = (float) particleVelocity.w;
					viewMatrix.transformPosition(StaticData.tmp2.set(x, y, z));
					particleVertices.put(StaticData.tmp2.x - particleSize).put(StaticData.tmp2.y - particleSize)
							.put(StaticData.tmp2.z).put(w).put(-1).put(-1);
					particleVertices.put(StaticData.tmp2.x + particleSize).put(StaticData.tmp2.y - particleSize)
							.put(StaticData.tmp2.z).put(w).put(1).put(-1);
					particleVertices.put(StaticData.tmp2.x + particleSize).put(StaticData.tmp2.y + particleSize)
							.put(StaticData.tmp2.z).put(w).put(1).put(1);
					particleVertices.put(StaticData.tmp2.x + particleSize).put(StaticData.tmp2.y + particleSize)
							.put(StaticData.tmp2.z).put(w).put(1).put(1);
					particleVertices.put(StaticData.tmp2.x - particleSize).put(StaticData.tmp2.y + particleSize)
							.put(StaticData.tmp2.z).put(w).put(-1).put(1);
					particleVertices.put(StaticData.tmp2.x - particleSize).put(StaticData.tmp2.y - particleSize)
							.put(StaticData.tmp2.z).put(w).put(-1).put(-1);
					num++;
				}
			}
		}
		particleVertices.flip();
		if (num > 0) {
			glUseProgram(particleProgram);
			glDepthMask(false);
			glEnable(GL_BLEND);
			glVertexPointer(4, GL_FLOAT, 6 * 4, particleVertices);
			particleVertices.position(4);
			glTexCoordPointer(2, GL_FLOAT, 6 * 4, particleVertices);
			particleVertices.position(0);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glDrawArrays(GL_TRIANGLES, 0, num * 6);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisable(GL_BLEND);
			glDepthMask(true);
		}
	}

	private void drawShots() {
		shotsVertices.clear();
		int num = 0;
		for (Shot shot : directShots) {

			Vector3d projectilePosition = shot.getPosition();
			Vector4f projectileVelocity = shot.getProjectileVelocity();
			if (projectileVelocity.w > 0.0F) {
				float x = (float) (projectilePosition.x - GameHeader.camera.position.x);
				float y = (float) (projectilePosition.y - GameHeader.camera.position.y);
				float z = (float) (projectilePosition.z - GameHeader.camera.position.z);
				if (frustumIntersection.testPoint(x, y, z)) {
					float w = projectileVelocity.w;
					viewMatrix.transformPosition(StaticData.tmp2.set(x, y, z));
					shotsVertices.put(StaticData.tmp2.x - shotSize).put(StaticData.tmp2.y - shotSize)
							.put(StaticData.tmp2.z).put(w).put(-1).put(-1);
					shotsVertices.put(StaticData.tmp2.x + shotSize).put(StaticData.tmp2.y - shotSize)
							.put(StaticData.tmp2.z).put(w).put(1).put(-1);
					shotsVertices.put(StaticData.tmp2.x + shotSize).put(StaticData.tmp2.y + shotSize)
							.put(StaticData.tmp2.z).put(w).put(1).put(1);
					shotsVertices.put(StaticData.tmp2.x + shotSize).put(StaticData.tmp2.y + shotSize)
							.put(StaticData.tmp2.z).put(w).put(1).put(1);
					shotsVertices.put(StaticData.tmp2.x - shotSize).put(StaticData.tmp2.y + shotSize)
							.put(StaticData.tmp2.z).put(w).put(-1).put(1);
					shotsVertices.put(StaticData.tmp2.x - shotSize).put(StaticData.tmp2.y - shotSize)
							.put(StaticData.tmp2.z).put(w).put(-1).put(-1);
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
		glVertex3f(GameHeader.camera.linearVel.x / GameHeader.getMaxLinearVel(),
				GameHeader.camera.linearVel.y / GameHeader.getMaxLinearVel(),
				GameHeader.camera.linearVel.z / GameHeader.getMaxLinearVel());
		glEnd();
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisable(GL_BLEND);
	}

	private void drawHudShotDirection() {
		if (WindowHeader.isLeadEnabled()) {
			try {
				glUseProgram(0);
				for (Ship enemyShip : localShips) {
					if (enemyShip == null) {
						return;
					}
					Vector3d targetOrigin = tmp;
					targetOrigin.set(enemyShip.getPosition().x, enemyShip.getPosition().y, enemyShip.getPosition().z);
					Vector3f interceptorDir = StaticData.intercept(GameHeader.camera.position, shotVelocity,
							targetOrigin, tmp3.set(GameHeader.camera.linearVel).negate(), StaticData.tmp2);
					viewMatrix.transformDirection(interceptorDir);
					if (interceptorDir.z > 0.0) {
						return;
					}
					projMatrix.transformProject(interceptorDir);
					float crosshairSize = 0.01F;
					float xs = crosshairSize * WindowHeader.getHeight() / WindowHeader.getWidth();
					float ys = crosshairSize;
					crosshairVertices.clear();
					crosshairVertices.put(interceptorDir.x - xs).put(interceptorDir.y - ys);
					crosshairVertices.put(interceptorDir.x + xs).put(interceptorDir.y - ys);
					crosshairVertices.put(interceptorDir.x + xs).put(interceptorDir.y + ys);
					crosshairVertices.put(interceptorDir.x - xs).put(interceptorDir.y + ys);
					crosshairVertices.flip();
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
					glVertexPointer(2, GL_FLOAT, 0, crosshairVertices);
					glDrawArrays(GL_QUADS, 0, 4);
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void drawHudShip() {
		if (WindowHeader.isHUDnabled()) {

			glUseProgram(0);
			for (Ship enemyShip : localShips) {

				if (enemyShip == null) {
					return;
				}
				Vector3f targetOrigin = StaticData.tmp2;
				targetOrigin.set((float) (enemyShip.getPosition().x - GameHeader.camera.position.x),
						(float) (enemyShip.getPosition().y - GameHeader.camera.position.y),
						(float) (enemyShip.getPosition().z - GameHeader.camera.position.z));
				tmp3.set(StaticData.tmp2);
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
				float crosshairSize = 0.03f;
				float xs = crosshairSize * WindowHeader.getHeight() / WindowHeader.getWidth();
				float ys = crosshairSize;
				crosshairVertices.clear();
				crosshairVertices.put(targetOrigin.x - xs).put(targetOrigin.y - ys);
				crosshairVertices.put(targetOrigin.x + xs).put(targetOrigin.y - ys);
				crosshairVertices.put(targetOrigin.x + xs).put(targetOrigin.y + ys);
				crosshairVertices.put(targetOrigin.x - xs).put(targetOrigin.y + ys);
				crosshairVertices.flip();
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				glVertexPointer(2, GL_FLOAT, 0, crosshairVertices);
				glDrawArrays(GL_QUADS, 0, 4);
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				// Draw distance text of enemy
				int quads = stb_easy_font_print(0, 0, Integer.toString((int) (tmp3.length())), null, charBuffer);
				glVertexPointer(2, GL_FLOAT, 16, charBuffer);
				glPushMatrix();
				// Scroll
				glTranslatef(targetOrigin.x, targetOrigin.y - crosshairSize * 1.1f, 0F);
				float aspect = (float) WindowHeader.getWidth() / WindowHeader.getHeight();
				glScalef(1.0F / 500.0F, -1.0F / 500.0F * aspect, 0.0F);
				glDrawArrays(GL_QUADS, 0, quads * 4);
				glPopMatrix();
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
				continue;
			}
			particlePosition.set(newPosition);
		}
	}

	private void updateShots(float deltaTime) {
		projectiles:

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
			/* Test against ships */

			for (Ship ship : localShips) {
				if (ship == null) {
					continue;
				}

				if (broadphase(ship.getPosition().x, ship.getPosition().y, ship.getPosition().z,
						shipMesh.boundingSphereRadius, shipRadius, projectilePosition, newPosition)
						&& narrowphase(shipMesh.positions, ship.getPosition().x, ship.getPosition().y,
								ship.getPosition().z, shipRadius, projectilePosition, newPosition, tmp,
								StaticData.tmp2)) {
					projectileVelocity.w = 0.0F;
					continue projectiles;
				}

			}
			/* Test against asteroids */
			for (Asteroid asteroid2 : localAsteroids) {
				if (asteroid2 == null) {
					continue;
				}

				if (broadphase(asteroid2.getPosition().x, asteroid2.getPosition().y, asteroid2.getPosition().z,
						asteroidMesh.boundingSphereRadius, asteroid2.scale, projectilePosition, newPosition)
						&& narrowphase(asteroidMesh.positions, asteroid2.getPosition().x, asteroid2.getPosition().y,
								asteroid2.getPosition().z, asteroid2.scale, projectilePosition, newPosition, tmp,
								StaticData.tmp2) //
				) {

					projectileVelocity.w = 0.0F;
					continue projectiles;
				}

			}
			projectilePosition.set(newPosition);
		}

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
				particleVelocity.w = 0.01f;
				particlePosition.set(p);
				if (c-- == 0) {
					break;
				}
			} // IF
			particles.add(particleDataObjectPairD);
		}
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
	}

	private void loop() {
		logger.info("loop-start");
		while (!glfwWindowShouldClose(WindowHeader.getWindow())) {
			glfwPollEvents();
			glViewport(0, 0, WindowHeader.getFbWidth(), WindowHeader.getFbHeight());
			update();
			render();
			glfwSwapBuffers(WindowHeader.getWindow());
		}
		logger.info("loop-end");
	}

	public void initData() throws IOException {
		StaticData.init();
	}

}

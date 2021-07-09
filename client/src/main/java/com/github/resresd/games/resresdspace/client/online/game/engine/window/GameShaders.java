package com.github.resresd.games.resresdspace.client.online.game.engine.window;

import static org.lwjgl.demo.util.IOUtils.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.demo.util.WavefrontMeshLoader.Mesh;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Asteroid;

import lombok.Getter;
import lombok.Setter;

public class GameShaders {

	public static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	public static FrustumIntersection frustumIntersection = new FrustumIntersection();
	public static Matrix4f projMatrix = new Matrix4f();// WORLD ?
	public static Matrix4f viewMatrix = new Matrix4f();// CAMERA
	public static Matrix4f modelMatrix = new Matrix4f();// WORLD ?

	@Getter
	@Setter
	public static int cubemapProgram;
	@Getter
	@Setter
	public static int cubemap_invViewProjUniform;

	public static void drawCubemap() {
		glUseProgram(GameShaders.getCubemapProgram());
		glVertexPointer(2, GL_FLOAT, 0, quadVertices);
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

	public static int shipProgram;
	public static int ship_viewUniform;
	public static int ship_projUniform;
	public static int ship_modelUniform;

	public static int asteroidPositionVbo;

	public static int asteroidNormalVbo;

	public static void drawAsteroids() {
		glUseProgram(shipProgram);
		glBindBuffer(GL_ARRAY_BUFFER, asteroidPositionVbo);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		glEnableClientState(GL_NORMAL_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, asteroidNormalVbo);
		glNormalPointer(GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		for (SpaceEntity spaceEntity : ClientGameEngine.getSPACE_ENTITIES()) {
			if (spaceEntity == null) {
				continue;
			}
			if (!(spaceEntity instanceof Asteroid)) {
				continue;
			}
			float x = (float) (spaceEntity.getPosition().x - GameHeader.camera.position.x);
			float y = (float) (spaceEntity.getPosition().y - GameHeader.camera.position.y);
			float z = (float) (spaceEntity.getPosition().z - GameHeader.camera.position.z);
			if (frustumIntersection.testSphere(x, y, z, spaceEntity.getScale())) {
				Mesh asteroidMesh = StaticData.getMESHS_MAP().get(spaceEntity.getClass());

				modelMatrix.translation(x, y, z);
				modelMatrix.scale(spaceEntity.getScale());
				glUniformMatrix4fv(ship_modelUniform, false, modelMatrix.get(matrixBuffer));

				glDrawArrays(GL_TRIANGLES, 0, asteroidMesh.numVertices);
			}
		}
		glDisableClientState(GL_NORMAL_ARRAY);
	}

	public static void createCubemapProgram() throws IOException {
		int vshader = createShader("org/lwjgl/demo/game/cubemap.vs", GL_VERTEX_SHADER);
		int fshader = createShader("org/lwjgl/demo/game/cubemap.fs", GL_FRAGMENT_SHADER);
		int program = createProgram(vshader, fshader);
		glUseProgram(program);
		int texLocation = glGetUniformLocation(program, "tex");
		glUniform1i(texLocation, 0);
		cubemap_invViewProjUniform = glGetUniformLocation(program, "invViewProj");
		glUseProgram(0);
		cubemapProgram = program;
	}

	@Getter
	@Setter
	public static ByteBuffer quadVertices;

	public static void createFullScreenQuad() {
		quadVertices = BufferUtils.createByteBuffer(4 * 2 * 6);
		FloatBuffer fv = quadVertices.asFloatBuffer();
		fv.put(-1.0F).put(-1.0F);
		fv.put(1.0F).put(-1.0F);
		fv.put(1.0F).put(1.0F);
		fv.put(1.0F).put(1.0F);
		fv.put(-1.0F).put(1.0F);
		fv.put(-1.0F).put(-1.0F);
	}

	public static int createShader(String resource, int type) throws IOException {
		int shader = glCreateShader(type);
		ByteBuffer source = ioResourceToByteBuffer(resource, 1024);
		PointerBuffer strings = BufferUtils.createPointerBuffer(1);
		IntBuffer lengths = BufferUtils.createIntBuffer(1);
		strings.put(0, source);
		lengths.put(0, source.remaining());
		glShaderSource(shader, strings, lengths);
		glCompileShader(shader);
		int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
		String shaderLog = glGetShaderInfoLog(shader);
		if (shaderLog.trim().length() > 0) {
			System.err.println(shaderLog);
		}
		if (compiled == 0) {
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}

	public static int createProgram(int vshader, int fshader) {
		int program = glCreateProgram();
		glAttachShader(program, vshader);
		glAttachShader(program, fshader);
		glLinkProgram(program);
		int linked = glGetProgrami(program, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(program);
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		return program;
	}

}

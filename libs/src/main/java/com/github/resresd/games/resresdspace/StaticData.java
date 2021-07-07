package com.github.resresd.games.resresdspace;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joml.GeometryUtils;
import org.joml.Intersectiond;
import org.joml.Intersectionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.demo.util.WavefrontMeshLoader;
import org.lwjgl.demo.util.WavefrontMeshLoader.Mesh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Asteroid;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;

import lombok.Getter;

public class StaticData {

	private StaticData() {
	}

	static Logger logger = LoggerFactory.getLogger(StaticData.class);

	public static SecureRandom secureRandomObj;

	public static float shotVelocity = 150.0F;
	public static int shotOpponentMilliseconds = 200;// ПЕРЕЗАРЯДКА У ДРУГИХ

	// public static Mesh shipMesh;
	public static int shipPositionVbo;
	public static int shipNormalVbo;

	public static Mesh sphereMesh;

	public static void initSecurity() throws NoSuchAlgorithmException {
		logger.info("initSecurity");
		Security.setProperty("crypto.policy", "unlimited");
		Security.addProvider(new BouncyCastleProvider());
		secureRandomObj = new SecureRandom();
		logger.info("initSecurity-end");
	}

	/**
	 * ИСПОЛЬЗУЕТСЯ В УПРАВЛЕНИИ ДЛЯ ПОВОРОТОВ
	 */
	@Getter
	public static Vector3f usedForNarmal = new Vector3f();

	public static Vector3d tmpUsedForPossition = new Vector3d();

	// WTF
	public static Vector3f tmp3 = new Vector3f();

	public static Vector3f intercept(Vector3d shotOrigin, float shotSpeed, Vector3d targetOrigin, Vector3f targetVel,
			Vector3f out) {
		float dirToTargetX = (float) (targetOrigin.x - shotOrigin.x);
		float dirToTargetY = (float) (targetOrigin.y - shotOrigin.y);
		float dirToTargetZ = (float) (targetOrigin.z - shotOrigin.z);
		float len = (float) Math
				.sqrt(dirToTargetX * dirToTargetX + dirToTargetY * dirToTargetY + dirToTargetZ * dirToTargetZ);
		dirToTargetX /= len;
		dirToTargetY /= len;
		dirToTargetZ /= len;
		float targetVelOrthDot = targetVel.x * dirToTargetX + targetVel.y * dirToTargetY + targetVel.z * dirToTargetZ;
		float targetVelOrthX = dirToTargetX * targetVelOrthDot;
		float targetVelOrthY = dirToTargetY * targetVelOrthDot;
		float targetVelOrthZ = dirToTargetZ * targetVelOrthDot;
		float targetVelTangX = targetVel.x - targetVelOrthX;
		float targetVelTangY = targetVel.y - targetVelOrthY;
		float targetVelTangZ = targetVel.z - targetVelOrthZ;

		float shotVelSpeed = (float) Math.sqrt(
				targetVelTangX * targetVelTangX + targetVelTangY * targetVelTangY + targetVelTangZ * targetVelTangZ);

		if (shotVelSpeed > shotSpeed) {
			return null;
		}

		float shotSpeedOrth = (float) Math.sqrt(shotSpeed * shotSpeed - shotVelSpeed * shotVelSpeed);
		float shotVelOrthX = dirToTargetX * shotSpeedOrth;
		float shotVelOrthY = dirToTargetY * shotSpeedOrth;
		float shotVelOrthZ = dirToTargetZ * shotSpeedOrth;
		return out.set(shotVelOrthX + targetVelTangX, shotVelOrthY + targetVelTangY, shotVelOrthZ + targetVelTangZ)
				.normalize();
	}

	public static boolean narrowphase(FloatBuffer data, double x, double y, double z, float scale, Vector3d pOld,
			Vector3d pNew, Vector3d intersectionPoint, Vector3f normal) {
		StaticData.usedForNarmal.set(tmpUsedForPossition.set(pOld).sub(x, y, z)).div(scale);
		tmp3.set(tmpUsedForPossition.set(pNew).sub(x, y, z)).div(scale);
		data.clear();
		boolean intersects = false;
		while (data.hasRemaining() && !intersects) {
			float v0X = data.get();
			float v0Y = data.get();
			float v0Z = data.get();
			float v1X = data.get();
			float v1Y = data.get();
			float v1Z = data.get();
			float v2X = data.get();
			float v2Y = data.get();
			float v2Z = data.get();
			if (Intersectionf.intersectLineSegmentTriangle(StaticData.usedForNarmal.x, StaticData.usedForNarmal.y,
					StaticData.usedForNarmal.z, tmp3.x, tmp3.y, tmp3.z, v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z,
					1E-6f, StaticData.usedForNarmal)) {
				intersectionPoint.x = StaticData.usedForNarmal.x * scale + x;
				intersectionPoint.y = StaticData.usedForNarmal.y * scale + y;
				intersectionPoint.z = StaticData.usedForNarmal.z * scale + z;
				GeometryUtils.normal(v0X, v0Y, v0Z, v1X, v1Y, v1Z, v2X, v2Y, v2Z, normal);
				intersects = true;
			}
		}
		data.clear();
		return intersects;
	}

	private static final @Getter ConcurrentHashMap<Class<?>, Mesh> MESHS_MAP = new ConcurrentHashMap<>();

	public static void init() throws IOException {

		// LOAD MESHs
		WavefrontMeshLoader loader = new WavefrontMeshLoader();

		MESHS_MAP.put(Ship.class, loader.loadMesh("org/lwjgl/demo/game/ship.obj.zip"));
		MESHS_MAP.put(Asteroid.class, loader.loadMesh("org/lwjgl/demo/game/asteroid.obj.zip"));
		// TODO MESHS_MAP.put(Shot.class,
		// loader.loadMesh("org/lwjgl/demo/game/asteroid.obj.zip"));

		sphereMesh = loader.loadMesh("org/lwjgl/demo/game/sphere.obj.zip");
		// LOAD MESHs
	}

	public static boolean broadphase(double x, double y, double z, float boundingRadius, float scale, Vector3d pOld,
			Vector3d pNew) {
		return Intersectiond.testLineSegmentSphere(pOld.x, pOld.y, pOld.z, pNew.x, pNew.y, pNew.z, x, y, z,
				boundingRadius * boundingRadius * scale * scale);
	}
}

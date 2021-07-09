package com.github.resresd.games.resresdspace.server.engine;

import static com.github.resresd.games.resresdspace.StaticData.broadphase;
import static com.github.resresd.games.resresdspace.StaticData.narrowphase;
import static com.github.resresd.games.resresdspace.StaticData.tmpUsedForPossition;

import java.lang.invoke.MethodHandles;
import java.nio.FloatBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.GeometryUtils;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.demo.util.WavefrontMeshLoader.Mesh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.event.space.EmitExplosionPacket;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDamageEvent;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDestroyEvent;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Asteroid;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;
import com.github.resresd.games.resresdspace.server.config.AsteroidsConfig;
import com.github.resresd.games.resresdspace.server.config.ServerConfig;
import com.github.resresd.games.resresdspace.server.config.ShipsConfig;
import com.github.resresd.games.resresdspace.server.header.ServerHeader;
import com.github.resresd.games.resresdspace.server.header.network.NetWorkHeader;
import com.github.resresd.utils.NumberUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class ServerEngine extends Thread {
	// ########################################################
	private static final @Getter CopyOnWriteArrayList<SpaceEntity> SPACE_ENTITIES = new CopyOnWriteArrayList<>();

	private static final @Getter CopyOnWriteArrayList<Shot> directShots = new CopyOnWriteArrayList<>();
	private static float maxShotLifetime = 4.0F;
	Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	@Getter
	@Setter
	boolean active = true;

	@Getter
	@Setter
	private long lastTime = System.nanoTime();

	// ########################################################
	// ########################################################ПОДГОТОВКА

	public void init() {
		logger.info("init-start");

		ServerConfig serverConfig = ServerHeader.getServerConfig();
		AsteroidsConfig asteroidsConfig = serverConfig.getAsteroidsConfig();

		for (int i = 0; i < asteroidsConfig.getCount(); i++) {
			logger.info("Ast:{}", i);
			try {

				float minAsteroidRadius = asteroidsConfig.getMinAsteroidRadius();
				float maxAsteroidRadius = asteroidsConfig.getMaxAsteroidRadius();

				double minX = asteroidsConfig.getMinX();
				double maxX = asteroidsConfig.getMaxX();

				double minY = asteroidsConfig.getMinY();
				double maxY = asteroidsConfig.getMaxY();

				double minZ = asteroidsConfig.getMinZ();
				double maxZ = asteroidsConfig.getMaxZ();

				Asteroid asteroid = new Asteroid();
				asteroid.setScale(Asteroid.generateSize(minAsteroidRadius, maxAsteroidRadius));

				asteroid.genHealth();

				asteroid.getPosition().x = NumberUtils.randomDoubleInRange(minX, maxX);
				asteroid.getPosition().y = NumberUtils.randomDoubleInRange(minY, maxY);
				asteroid.getPosition().z = NumberUtils.randomDoubleInRange(minZ, maxZ);

				SPACE_ENTITIES.add(asteroid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		new Thread(((Runnable) () -> {
			logger.info("localShips started");
			while (active) {
				try {
					ShipsConfig shipConfig = ServerHeader.getServerConfig().getShipsConfig();
					CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();

					for (Object object : SPACE_ENTITIES) {
						if (object instanceof Ship) {
							localShips.add((Ship) object);
						}
					}

					if (localShips.size() > shipConfig.getMaxCount()) {
						Thread.sleep(100);
						continue;
					}
					Ship ship = new Ship();
					ship.setLastShotTime(0);

					// TEST
					ship.setScale(4.0F);

					double x = NumberUtils.randomDoubleInRange(shipConfig.getMinX(), shipConfig.getMaxX());
					double y = NumberUtils.randomDoubleInRange(shipConfig.getMinY(), shipConfig.getMaxY());
					double z = NumberUtils.randomDoubleInRange(shipConfig.getMinZ(), shipConfig.getMaxZ());

					ship.getPosition().x = x;
					ship.getPosition().y = y;
					ship.getPosition().z = z;

					NetWorkHeader.sendBroadcastNetty(ship);
					SPACE_ENTITIES.add(ship);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}), "localShips respawn").start();

		logger.info("init-end");
	}

	private void loop() {
		logger.info("loop-start");
		while (active) {
			long thisTime = System.nanoTime();
			double deltaTime = (thisTime - lastTime) / 1E9D;
			lastTime = thisTime;

			update(deltaTime);

			CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();
			for (Object object : SPACE_ENTITIES) {
				if (object instanceof Ship) {
					localShips.add((Ship) object);
				}
			}
			for (Ship localShip : localShips) {
				shootFromShip(thisTime, localShip);
			}

		}
		logger.info("loop-end");
	}

	// ########################################################
	@Override
	public void run() {
		logger.info("run-start");
		loop();
		logger.info("run-end");
	}

	// SHOOT
	private void shootFromShip(long thisTime, @NonNull Ship ship) {
		try {

			if (thisTime - ship.lastShotTime < 1E6 * StaticData.shotOpponentMilliseconds) {
				return;
			}

			ship.lastShotTime = thisTime;

			CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();

			for (Object object : SPACE_ENTITIES) {
				if (object instanceof Ship) {
					localShips.add((Ship) object);
				}
			}

			if (localShips.size() <= 1) {
				return;
			}
			// WTF
			Vector3f tmp3 = new Vector3f();
			Vector3f tmp4 = new Vector3f();

			// TARGET
			Ship targetShip = localShips.get(NumberUtils.randomIntInRange(0, localShips.size() - 1));

			Vector3d position = targetShip.getPosition();
			Vector3f linearVel = targetShip.getLinearVel();

			// getRadius
			float shipRadius = targetShip.getScale();

			Vector3d shotPos = tmpUsedForPossition.set(ship.getPosition().x, ship.getPosition().y, ship.getPosition().z)
					.sub(position).negate().normalize().mul(1.01f * shipRadius)
					.add(ship.getPosition().x, ship.getPosition().y, ship.getPosition().z);
			Vector3f icept = StaticData.intercept(shotPos, StaticData.shotVelocity, position, linearVel,
					StaticData.usedForNarmal);

			if (icept == null) {
				return;
			} // jitter the direction a bit

			GeometryUtils.perpendicular(icept, tmp3, tmp4);
			icept.fma(((float) Math.random() * 2.0F - 1.0F) * 0.01f, tmp3);
			icept.fma(((float) Math.random() * 2.0F - 1.0F) * 0.01f, tmp4);
			icept.normalize();

			Shot newShot = new Shot();
			Vector3d projectilePosition = newShot.getPosition();
			Vector4f projectileVelocity = newShot.getProjectileVelocity();
			if (projectileVelocity.w <= 0.0F) {
				projectilePosition.set(shotPos);
				projectileVelocity.x = StaticData.usedForNarmal.x * StaticData.shotVelocity;
				projectileVelocity.y = StaticData.usedForNarmal.y * StaticData.shotVelocity;
				projectileVelocity.z = StaticData.usedForNarmal.z * StaticData.shotVelocity;
				projectileVelocity.w = 0.01f;
			}
			directShots.add(newShot);
			NetWorkHeader.sendBroadcastNetty(newShot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// SHOOT

	// UPDATE
	private void update(double deltaTime) {
		updateShots(deltaTime);
		updateRockets(deltaTime);
	}

	// updateRockets
	private void updateRockets(double deltaTime) {
	}

	private void updateShots(double deltaTime) {
		projectiles:

		for (Shot shot : directShots) {
			Vector4f projectileVelocity = shot.getProjectileVelocity();
			double damage = shot.getDamage();

			if (projectileVelocity.w <= 0.0F) {
				directShots.remove(shot);
				SpaceEntityDestroyEvent spaceEntityDestroyEvent = new SpaceEntityDestroyEvent();
				spaceEntityDestroyEvent.setTargetEntity(shot);

				NetWorkHeader.sendBroadcastNetty(spaceEntityDestroyEvent);

				continue;
			}
			projectileVelocity.w += deltaTime;
			Vector3d projectilePosition = shot.getPosition();
			Vector3d newPosition = new Vector3d();

			newPosition.set(projectileVelocity.x, projectileVelocity.y, projectileVelocity.z).mul(deltaTime)
					.add(projectilePosition);
			if (projectileVelocity.w > maxShotLifetime) {
				projectileVelocity.w = 0.0F;
				directShots.remove(shot);
				SpaceEntityDestroyEvent spaceEntityDestroyEvent = new SpaceEntityDestroyEvent();
				spaceEntityDestroyEvent.setTargetEntity(shot);

				NetWorkHeader.sendBroadcastNetty(spaceEntityDestroyEvent);
				continue;
			}

			for (SpaceEntity spaceEntity : SPACE_ENTITIES) {
				if (spaceEntity == null) {
					logger.warn("For:spaceEntity is null");
					continue;
				}
				Vector3d spaceEntityPosition = spaceEntity.getPosition();
				double posX = spaceEntityPosition.x();
				double posY = spaceEntityPosition.y();
				double posZ = spaceEntityPosition.z();

				Mesh mesh = StaticData.getMESHS_MAP().get(spaceEntity.getClass());
				float boundingSphereRadius = mesh.boundingSphereRadius;
				FloatBuffer meshPos = mesh.positions;

				float scale = spaceEntity.getScale();

				if (broadphase(posX, posY, posZ, boundingSphereRadius, scale, projectilePosition, newPosition)
						&& narrowphase(meshPos, posX, posY, posZ, scale, projectilePosition, newPosition,
								tmpUsedForPossition, StaticData.usedForNarmal)) {
					if (false) {
						System.err.println("for SPACE_ENTITIES broadphase&&narrowphase: " + spaceEntity);
					}
					if (spaceEntity.damage(damage)) {
						SPACE_ENTITIES.remove(spaceEntity);

						SpaceEntityDestroyEvent spaceEntityDestroyEvent = new SpaceEntityDestroyEvent();
						spaceEntityDestroyEvent.setTargetEntity(spaceEntity);

						NetWorkHeader.sendBroadcastNetty(spaceEntityDestroyEvent);
					} else {
						SpaceEntityDamageEvent spaceEntityDamageEvent = new SpaceEntityDamageEvent();
						spaceEntityDamageEvent.setTargetEntity(spaceEntity);
						spaceEntityDamageEvent.setDamage(damage);
						NetWorkHeader.sendBroadcastNetty(spaceEntityDamageEvent);
					}
					EmitExplosionPacket emitExplosionPacket = new EmitExplosionPacket();
					emitExplosionPacket.setPosition(tmpUsedForPossition);
					emitExplosionPacket.setNormal(StaticData.usedForNarmal);
					NetWorkHeader.sendBroadcastNetty(emitExplosionPacket);

					projectileVelocity.w = 0.0F;
				}
			}

			projectilePosition.set(newPosition);
		}
	}
	// updateRockets

	// UPDATE
}

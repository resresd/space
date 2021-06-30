package com.github.resresd.games.resresdspace.server.engine;

import static com.github.resresd.games.resresdspace.StaticData.asteroidMesh;
import static com.github.resresd.games.resresdspace.StaticData.broadphase;
import static com.github.resresd.games.resresdspace.StaticData.narrowphase;
import static com.github.resresd.games.resresdspace.StaticData.shipMesh;
import static com.github.resresd.games.resresdspace.StaticData.tmp;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.GeometryUtils;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.event.space.EmitExplosionPacket;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDestroyEvent;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Asteroid;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;
import com.github.resresd.games.resresdspace.server.header.network.NetWorkHeader;
import com.github.resresd.utils.NumberUtils;

import lombok.Getter;
import lombok.Setter;

public class ServerEngine extends Thread {
	Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Getter
	@Setter
	boolean active = true;
	@Getter
	@Setter
	private long lastTime = System.nanoTime();

	// ########################################################
	public static CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Asteroid> localAsteroids = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Shot> directShots = new CopyOnWriteArrayList<>();

	private static float shipRadius = 4.0F;
	private static float maxShotLifetime = 4.0F;

	static float minAsteroidRadius = 1F;
	static float maxAsteroidRadius = 330F;
	static int a = 3000;
	static int b = 210;

	// ########################################################
	// ########################################################ПОДГОТОВКА

	public void init() {
		logger.info("init-start");

		for (int i = 0; i < 4000; i++) {
			logger.info("Ast:" + i);
			try {
				Asteroid asteroid = new Asteroid();
				asteroid.setScale(Asteroid.generateSize(minAsteroidRadius, maxAsteroidRadius));
				asteroid.getPosition().x = NumberUtils.randomDoubleInRange(-10000, 10000);
				asteroid.getPosition().y = NumberUtils.randomDoubleInRange(-10000, 10000);
				asteroid.getPosition().z = NumberUtils.randomDoubleInRange(-10000, 10000);
				localAsteroids.add(asteroid);

				StringBuilder msg = new StringBuilder();
				msg.append("size:").append(asteroid.getScale());
				msg.append(" X:").append(asteroid.getPosition().x);
				msg.append(" Y:").append(asteroid.getPosition().y);
				msg.append(" Z:").append(asteroid.getPosition().z);
				logger.info(msg.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("localShips started");
				while (active) {
					try {
						if (localShips.size() > 3) {
							Thread.sleep(100);
							continue;
						}
						Ship ship = new Ship();
						ship.setLastShotTime(0);
						ship.getPosition().x = NumberUtils.randomDoubleInRange(-50, 50);
						ship.getPosition().y = NumberUtils.randomDoubleInRange(-50, 50);
						ship.getPosition().z = NumberUtils.randomDoubleInRange(-50, 50);

						NetWorkHeader.sendBroadcast(ship);
						localShips.add(ship);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}, "aaa").start();

		logger.info("init-end");
	}

	// ########################################################
	@Override
	public void run() {
		logger.info("run-start");
		loop();
		logger.info("run-end");
	}

	private void loop() {
		logger.info("loop-start");
		while (active) {
			long thisTime = System.nanoTime();
			double deltaTime = (thisTime - lastTime) / 1E9D;
			lastTime = thisTime;

			update(deltaTime);
			for (Ship localShip : localShips) {
				shootFromShip(thisTime, localShip);
			}
		}
		logger.info("loop-end");
	}

	// SHOOT
	private void shootFromShip(long thisTime, Ship ship) {
		try {
			if (ship == null) {
				return;
			}

			if (thisTime - ship.lastShotTime < 1E6 * StaticData.shotOpponentMilliseconds) {
				return;
			}

			// WTF
			Vector3f tmp3 = new Vector3f();
			Vector3f tmp4 = new Vector3f();

			ship.lastShotTime = thisTime;

			if (localShips.size() <= 1) {
				return;
			}
			Ship targetShip = localShips.get(NumberUtils.randomIntInRange(0, localShips.size() - 1));

			Vector3d position = targetShip.getPosition();
			Vector3f linearVel = targetShip.getLinearVel();

			Vector3d shotPos = tmp.set(ship.getPosition().x, ship.getPosition().y, ship.getPosition().z).sub(position)
					.negate().normalize().mul(1.01f * shipRadius)
					.add(ship.getPosition().x, ship.getPosition().y, ship.getPosition().z);
			Vector3f icept = StaticData.intercept(shotPos, StaticData.shotVelocity, position, linearVel,
					StaticData.tmp2);

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
				projectileVelocity.x = StaticData.tmp2.x * StaticData.shotVelocity;
				projectileVelocity.y = StaticData.tmp2.y * StaticData.shotVelocity;
				projectileVelocity.z = StaticData.tmp2.z * StaticData.shotVelocity;
				projectileVelocity.w = 0.01f;
			}
			directShots.add(newShot);
			NetWorkHeader.sendBroadcast(newShot);
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

	private void updateShots(double deltaTime) {
		projectiles:

		for (Shot shot : directShots) {
			Vector4f projectileVelocity = shot.getProjectileVelocity();
			if (projectileVelocity.w <= 0.0F) {
				directShots.remove(shot);
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
				continue;
			}
			/* Test against ships */

			for (Ship ship : localShips) {
				if (ship == null) {
					continue;
				}
				// СТОЛКНОВЕНИЕ СНАРЯДА С Кораблями
				if (broadphase(ship.getPosition().x, ship.getPosition().y, ship.getPosition().z,
						shipMesh.boundingSphereRadius, shipRadius, projectilePosition, newPosition)
						&& narrowphase(shipMesh.positions, ship.getPosition().x, ship.getPosition().y,
								ship.getPosition().z, shipRadius, projectilePosition, newPosition, tmp,
								StaticData.tmp2)) {

					if (ship.damage(1.0D)) {
						localShips.remove(ship);
						System.err.println(ship + " уничтожен");
						SpaceEntityDestroyEvent spaceEntityDestroyEvent = new SpaceEntityDestroyEvent();
						spaceEntityDestroyEvent.setTargetEntity(ship);

						NetWorkHeader.sendBroadcast(spaceEntityDestroyEvent);
					}

					EmitExplosionPacket emitExplosionPacket = new EmitExplosionPacket();
					emitExplosionPacket.setP(tmp);
					emitExplosionPacket.setNormal(null);
					NetWorkHeader.sendBroadcast(emitExplosionPacket);

					projectileVelocity.w = 0.0F;
					continue projectiles;
				} // СТОЛКНОВЕНИЕ СНАРЯДА С Кораблями
			}
			/* Test against asteroids */
			for (Asteroid asteroid2 : localAsteroids) {
				if (asteroid2 == null) {
					continue;
				}
				// СТОЛКНОВЕНИЕ СНАРЯДА С АСТЕРОЙДОМ
				if (broadphase(asteroid2.getPosition().x, asteroid2.getPosition().y, asteroid2.getPosition().z,
						asteroidMesh.boundingSphereRadius, asteroid2.scale, projectilePosition, newPosition)
						&& narrowphase(asteroidMesh.positions, asteroid2.getPosition().x, asteroid2.getPosition().y,
								asteroid2.getPosition().z, asteroid2.scale, projectilePosition, newPosition, tmp,
								StaticData.tmp2)) {

					EmitExplosionPacket emitExplosionPacket = new EmitExplosionPacket();
					emitExplosionPacket.setP(tmp);
					emitExplosionPacket.setNormal(StaticData.tmp2);// FIXME
					NetWorkHeader.sendBroadcast(emitExplosionPacket);

					projectileVelocity.w = 0.0F;
					continue projectiles;
				} // СТОЛКНОВЕНИЕ СНАРЯДА С АСТЕРОЙДОМ

			}
			projectilePosition.set(newPosition);
		}
	}

	// updateRockets
	private void updateRockets(double deltaTime) {
	}
	// updateRockets

	// UPDATE
}

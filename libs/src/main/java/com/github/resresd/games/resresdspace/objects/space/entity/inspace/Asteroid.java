package com.github.resresd.games.resresdspace.objects.space.entity.inspace;

import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Vector3d;

import com.github.resresd.games.resresdspace.api.server.entities.EntitiesApi;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.utils.NumberUtils;

public class Asteroid extends SpaceEntity {

	private static final long serialVersionUID = 1265659135993575513L;

	public static float generateSize(float min, float max) {
		return NumberUtils.randomFloatInRange(min, max);
	}

	public static float generateCoord(float size, float a, float b) {
		return NumberUtils.randomFloatInRange(0F, a / b * size);
	}

	@Override
	public void spawnResource(SpaceEntity entity) {
		float scale = getScale();
		float scaleCurrent = scale;
		CopyOnWriteArrayList<Float> scales = new CopyOnWriteArrayList<>();
		while (scaleCurrent > 1F) {
			float tmpScale = NumberUtils.randomFloatInRange(1F, scaleCurrent / 100F * 10F);
			scales.add(tmpScale);
			scaleCurrent = scaleCurrent - tmpScale;
		}

		for (Float scaleC : scales) {
			Asteroid asteroid = new Asteroid();
			asteroid.setScale(scaleC);
			asteroid.genHealth();
			Vector3d entityPos = entity.getPosition();
			double rx = entityPos.x();
			double ry = entityPos.y();
			double rz = entityPos.z();

			double xmin = rx - scaleC * 5D - scaleCurrent;
			double ymin = ry - scaleC * 5D - scaleCurrent;
			double zmin = rz - scaleC * 5D - scaleCurrent;

			double xmax = rx + scaleC * 5D + scaleCurrent;
			double ymax = ry + scaleC * 5D + scaleCurrent;
			double zmax = rz + scaleC * 5D + scaleCurrent;

			double x = NumberUtils.randomDoubleInRange(xmin, xmax);
			double y = NumberUtils.randomDoubleInRange(ymin, ymax);
			double z = NumberUtils.randomDoubleInRange(zmin, zmax);
			Vector3d asteroidPosition = asteroid.getPosition();
			asteroidPosition.x = x;
			asteroidPosition.y = y;
			asteroidPosition.z = z;
			EntitiesApi.spawnEntity(asteroid);
		}

	}

	public void genHealth() {
		setHealthMax((double) (getScale() * 50F));
		setHealth(getHealthMax());
	}

}

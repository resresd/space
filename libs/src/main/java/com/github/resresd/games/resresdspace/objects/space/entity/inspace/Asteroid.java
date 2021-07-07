package com.github.resresd.games.resresdspace.objects.space.entity.inspace;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.utils.NumberUtils;

public class Asteroid extends SpaceEntity {

	private static final long serialVersionUID = 1265659135993575513L;

	public Asteroid() {
		super();
	}

	public static float generateSize(float min, float max) {
		return NumberUtils.randomFloatInRange(min, max);
	}

	public static float generateCoord(float size, float a, float b) {
		return NumberUtils.randomFloatInRange(0, a / b * size);
	}
}
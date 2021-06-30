package com.github.resresd.games.resresdspace.objects.space.entity.inspace;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.utils.NumberUtils;

import lombok.Getter;
import lombok.Setter;

public class Asteroid extends SpaceEntity {

	public Asteroid() {
		super();
	}

	@Getter
	@Setter
	public float scale;

	public static float generateSize(float min, float max) {
		return NumberUtils.randomFloatInRange(min, max);
	}

	public static float generateCoord(float size, float a, float b) {
		return NumberUtils.randomFloatInRange(0, a / b * size);
	}
}
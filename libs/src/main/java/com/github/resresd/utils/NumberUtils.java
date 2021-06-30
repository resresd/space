package com.github.resresd.utils;

import com.github.resresd.games.resresdspace.StaticData;

public final class NumberUtils {

	private NumberUtils() {
	}

	public static double randomDoubleInRange(double min, double max) {
		return min + (max - min) * StaticData.secureRandomObj.nextDouble();
	}

	public static int randomIntInRange(int min, int max) {
		return StaticData.secureRandomObj.nextInt((max - min) + 1) + min;
	}

	public static float randomFloatInRange(float min, float max) {
		return min + (max - min) * StaticData.secureRandomObj.nextFloat();
	}
}

package com.github.resresd.games.resresdspace.gui.menu;

public class CoordUtils {
	public static float forGlCoordByReal(float realCoord, int realWindowLen) {
		return 2 * realCoord / realWindowLen - 1;
	}

	public static float forRealCoordByGl(float glCoord, int realWindowLen) {
		return (realWindowLen * glCoord + realWindowLen) / 2;
	}
}

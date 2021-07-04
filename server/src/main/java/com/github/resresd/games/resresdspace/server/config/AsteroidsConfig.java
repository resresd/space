package com.github.resresd.games.resresdspace.server.config;

import lombok.Getter;
import lombok.Setter;

public class AsteroidsConfig {

	private @Getter @Setter int count = 4000;
	private @Getter @Setter float minAsteroidRadius = 1F;
	private @Getter @Setter float maxAsteroidRadius = 330F;

	private @Getter @Setter double minX = -10000D;
	private @Getter @Setter double maxX = 10000D;

	private @Getter @Setter double minY = -10000D;
	private @Getter @Setter double maxY = 10000D;

	private @Getter @Setter double minZ = -10000D;
	private @Getter @Setter double maxZ = 10000D;
}

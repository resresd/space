package com.github.resresd.games.resresdspace.server.config;

import lombok.Getter;
import lombok.Setter;

public class ShipsConfig {
	private @Getter @Setter int maxCount = 30;

	private @Getter @Setter double minX = -250D;
	private @Getter @Setter double maxX = 250D;

	private @Getter @Setter double minY = -250D;
	private @Getter @Setter double maxY = 250D;

	private @Getter @Setter double minZ = -250D;
	private @Getter @Setter double maxZ = 250D;

}

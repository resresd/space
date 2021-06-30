package com.github.resresd.games.resresdspace.server.config.network;

import lombok.Getter;
import lombok.Setter;

public class NetworkConfig {

	@Getter
	@Setter
	private int serverPort = 23000;
	@Getter
	@Setter
	private int timeOut = 10000;
}

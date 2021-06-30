package com.github.resresd.games.resresdspace.client.online.game.configs;

import lombok.Getter;
import lombok.Setter;

public class NetworkConfig {

	@Getter
	@Setter
	private String serverHost = "host.zone";

	@Getter
	@Setter
	private int serverPort = 23000;

}

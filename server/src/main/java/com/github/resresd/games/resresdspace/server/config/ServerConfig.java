package com.github.resresd.games.resresdspace.server.config;

import com.github.resresd.games.resresdspace.server.config.network.NetworkConfig;

import lombok.Getter;
import lombok.Setter;

public class ServerConfig {
	@Getter
	@Setter
	private boolean prepare;
	@Getter
	@Setter
	private NetworkConfig networkConfig = new NetworkConfig();
}

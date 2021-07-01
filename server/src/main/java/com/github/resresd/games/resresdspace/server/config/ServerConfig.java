package com.github.resresd.games.resresdspace.server.config;

import com.github.resresd.games.resresdspace.server.config.network.NetworkConfig;

import lombok.Getter;
import lombok.Setter;

public class ServerConfig {

	private @Getter @Setter boolean prepare;

	private @Getter @Setter NetworkConfig networkConfig = new NetworkConfig();

	private @Getter @Setter AsteroidsConfig asteroidsConfig = new AsteroidsConfig();
	private @Getter @Setter ShipsConfig shipsConfig = new ShipsConfig();
}

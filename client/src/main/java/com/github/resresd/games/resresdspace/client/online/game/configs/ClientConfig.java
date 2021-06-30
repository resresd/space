package com.github.resresd.games.resresdspace.client.online.game.configs;

import com.github.resresd.games.resresdspace.players.Player;

import lombok.Getter;
import lombok.Setter;

public class ClientConfig {
	@Getter
	@Setter
	private boolean prepare;
	@Getter
	@Setter
	private WindowConfig windowConfig = new WindowConfig();
	@Getter
	@Setter
	private NetworkConfig networkConfig = new NetworkConfig();

	@Getter
	@Setter
	private Player player = new Player();
}

package com.github.resresd.games.resresdspace.network.packets;

import com.github.resresd.games.resresdspace.players.Player;

import lombok.Getter;
import lombok.Setter;

public class ReadyPacket extends Packet {
	private @Getter @Setter Player player;
}

package com.github.resresd.games.resresdspace.network.packets;

import lombok.Getter;
import lombok.Setter;

public class PingPacket extends Packet {
	@Getter
	@Setter
	boolean pong;
}

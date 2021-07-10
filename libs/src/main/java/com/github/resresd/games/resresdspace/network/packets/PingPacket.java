package com.github.resresd.games.resresdspace.network.packets;

import lombok.Getter;
import lombok.Setter;

public class PingPacket extends Packet {

	private static final long serialVersionUID = -7599169674534294143L;
	@Getter
	@Setter
	boolean pong;
}

package com.github.resresd.games.resresdspace.event.space;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.github.resresd.games.resresdspace.network.packets.Packet;

import lombok.Getter;
import lombok.Setter;

public class EmitExplosionPacket extends Packet {
	private static final long serialVersionUID = 2391827501487854889L;
	@Getter
	@Setter
	Vector3d position;
	@Getter
	@Setter
	Vector3f normal;
}

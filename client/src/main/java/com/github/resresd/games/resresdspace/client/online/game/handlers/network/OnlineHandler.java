package com.github.resresd.games.resresdspace.client.online.game.handlers.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.event.space.EmitExplosionPacket;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDestroyEvent;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Asteroid;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;

public class OnlineHandler extends IoHandlerAdapter {
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof Ship) {
			ClientGameEngine.localShips.add((Ship) message);
		}
		if (message instanceof Asteroid) {
			Asteroid asteroid = (Asteroid) message;
			ClientGameEngine.localAsteroids.add(asteroid);
		}
		if (message instanceof Shot) {
			Shot shot = (Shot) message;
			ClientGameEngine.directShots.add(shot);
		}
		if (message instanceof EmitExplosionPacket) {
			EmitExplosionPacket emitExplosionPacket = (EmitExplosionPacket) message;
			GameHeader.onlinegame.emitExplosion(emitExplosionPacket.getPosition(), emitExplosionPacket.getNormal());
		}
		if (message instanceof SpaceEntityDestroyEvent) {
			SpaceEntityDestroyEvent spaceEntityDestroyEvent = (SpaceEntityDestroyEvent) message;
			if (spaceEntityDestroyEvent.getTargetEntity().removeFromList(ClientGameEngine.localShips)) {
			}
		}
	}
}

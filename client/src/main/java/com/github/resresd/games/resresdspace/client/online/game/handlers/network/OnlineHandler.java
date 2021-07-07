package com.github.resresd.games.resresdspace.client.online.game.handlers.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.event.space.EmitExplosionPacket;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDamageEvent;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDestroyEvent;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;

public class OnlineHandler extends IoHandlerAdapter {
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof SpaceEntity) {
			if (message instanceof Shot) {
				Shot shot = (Shot) message;
				ClientGameEngine.directShots.add(shot);
				return;
			}
			ClientGameEngine.getSPACE_ENTITIES().add((SpaceEntity) message);
		}

		if (message instanceof EmitExplosionPacket) {
			System.err.println("EmitExplosionPacket");
			EmitExplosionPacket emitExplosionPacket = (EmitExplosionPacket) message;
			GameHeader.onlinegame.emitExplosion(emitExplosionPacket.getPosition(), emitExplosionPacket.getNormal());
		}
		if (message instanceof SpaceEntityDamageEvent) {
			SpaceEntityDamageEvent spaceEntityDamageEvent = (SpaceEntityDamageEvent) message;
			SpaceEntity entityInEngine = SpaceEntity.getFromEngine(ClientGameEngine.getSPACE_ENTITIES(),
					spaceEntityDamageEvent.getTargetEntity());

			if (entityInEngine != null) {
				entityInEngine.damage(spaceEntityDamageEvent.getDamage());
			} else {
				System.err.println("OnlineHandler:messageReceived:SpaceEntityDamageEvent:null");
			}

		}
		if (message instanceof SpaceEntityDestroyEvent) {
			SpaceEntityDestroyEvent spaceEntityDestroyEvent = (SpaceEntityDestroyEvent) message;
			SpaceEntity entity = spaceEntityDestroyEvent.getTargetEntity();

			if (entity.removeFromList(ClientGameEngine.getSPACE_ENTITIES())) {
			}
		}

	}
}

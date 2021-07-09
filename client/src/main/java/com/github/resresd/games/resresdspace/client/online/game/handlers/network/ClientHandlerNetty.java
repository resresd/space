package com.github.resresd.games.resresdspace.client.online.game.handlers.network;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.event.space.EmitExplosionPacket;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDamageEvent;
import com.github.resresd.games.resresdspace.event.space.SpaceEntityDestroyEvent;
import com.github.resresd.games.resresdspace.network.packets.ReadyPacket;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandlerNetty extends ChannelInboundHandlerAdapter {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ReadyPacket readyPacket = new ReadyPacket();
		readyPacket.setPlayer(GameHeader.getClientConfig().getPlayer());
		ctx.writeAndFlush(readyPacket);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof SpaceEntity) {
			if (msg instanceof Shot) {
				Shot shot = (Shot) msg;
				ClientGameEngine.directShots.add(shot);
				return;
			}
			ClientGameEngine.getSPACE_ENTITIES().add((SpaceEntity) msg);
		}

		if (msg instanceof EmitExplosionPacket) {
			EmitExplosionPacket emitExplosionPacket = (EmitExplosionPacket) msg;
			Vector3d position = emitExplosionPacket.getPosition();
			Vector3f normal = emitExplosionPacket.getNormal();
			GameHeader.onlinegame.emitExplosion(position, normal);
		}
		if (msg instanceof SpaceEntityDamageEvent) {
			SpaceEntityDamageEvent spaceEntityDamageEvent = (SpaceEntityDamageEvent) msg;
			SpaceEntity entityInEngine = SpaceEntity.getFromEngine(ClientGameEngine.getSPACE_ENTITIES(),
					spaceEntityDamageEvent.getTargetEntity());

			if (entityInEngine != null) {
				entityInEngine.damage(spaceEntityDamageEvent.getDamage());
			} else {
				System.err.println("OnlineHandler:msgReceived:SpaceEntityDamageEvent:null");
			}

		}
		if (msg instanceof SpaceEntityDestroyEvent) {
			SpaceEntityDestroyEvent spaceEntityDestroyEvent = (SpaceEntityDestroyEvent) msg;
			SpaceEntity entity = spaceEntityDestroyEvent.getTargetEntity();
			if (entity.removeFromList(ClientGameEngine.getDirectShots())) {
			}
			if (entity.removeFromList(ClientGameEngine.getSPACE_ENTITIES())) {
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("network context:{} exception:{}", ctx, cause);
	}
}

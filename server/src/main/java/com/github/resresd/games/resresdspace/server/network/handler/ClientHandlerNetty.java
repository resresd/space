package com.github.resresd.games.resresdspace.server.network.handler;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.network.packets.ReadyPacket;
import com.github.resresd.games.resresdspace.network.packets.connections.ConnectionClosePacket;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;
import com.github.resresd.games.resresdspace.players.Player;
import com.github.resresd.games.resresdspace.server.engine.ServerEngine;
import com.github.resresd.games.resresdspace.server.header.network.NetWorkHeader;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandlerNetty extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandlerNetty.class.getSimpleName());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NetWorkHeader.getPlayers().putIfAbsent(ctx, new Player());
		int size = NetWorkHeader.getPlayers().size();
		SocketAddress addr = ctx.channel().remoteAddress();
		Thread.currentThread().setName("client: " + addr);
		LOGGER.info("Session opened. Current:{}", size);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		NetWorkHeader.getPlayers().remove(ctx);
		int size = NetWorkHeader.getPlayers().size();

		LOGGER.info("Session closed. Current:{}", size);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ConnectionClosePacket) {
			ctx.close();
		}

		if (msg instanceof Shot) {
			Shot shot = (Shot) msg;

			ServerEngine.getDirectShots().add(shot);
			NetWorkHeader.sendBroadcastExcludeNetty(ctx, msg);
		} else if (msg instanceof ReadyPacket) {

			new Thread(() -> {
				// send last pos
				ReadyPacket packet = (ReadyPacket) msg;
				Player player = packet.getPlayer();
				LOGGER.info("TODO Send last pos for {}", player);
				LOGGER.info("TODO Send Relation for {}", player);

				ServerEngine.getSPACE_ENTITIES().parallelStream().forEachOrdered(entity -> ctx.writeAndFlush(entity));
			}).start();
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.info("error context:{} error:{}", ctx, cause);
	}

}

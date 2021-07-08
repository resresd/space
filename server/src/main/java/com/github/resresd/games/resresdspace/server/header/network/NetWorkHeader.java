package com.github.resresd.games.resresdspace.server.header.network;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.players.Player;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

public class NetWorkHeader {

	private static @Getter ConcurrentHashMap<ChannelHandlerContext, Player> players = new ConcurrentHashMap<>();
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	public static void sendBroadcastExcludeNetty(ChannelHandlerContext context, Object object) {
		if (!players.isEmpty()) {
			players.entrySet().parallelStream()
					// filter
					.filter(k -> !(k.getKey() == context))
					// forEach
					.forEachOrdered(k -> k.getKey().writeAndFlush(object));
		}
	}

	public static void sendBroadcastNetty(Object object) {
		if (!players.isEmpty()) {
			players.entrySet().parallelStream().forEachOrdered(k -> k.getKey().writeAndFlush(object));
		}
	}

	private NetWorkHeader() {
	}

}

package com.github.resresd.games.resresdspace.client.online.game.handlers.network;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.event.Event;
import com.github.resresd.games.resresdspace.network.packets.PingPacket;
import com.github.resresd.games.resresdspace.network.packets.connections.ConnectionClosePacket;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public final class Network {
	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	static EventLoopGroup group = new NioEventLoopGroup();

	public static Bootstrap clientBootstrap = new Bootstrap();
	static ChannelFuture channelFuture;

	public static void exit() throws InterruptedException {
		ConnectionClosePacket connectionClosePacket = new ConnectionClosePacket();

		channelFuture.channel().writeAndFlush(connectionClosePacket);
		channelFuture.channel().closeFuture().await(5, TimeUnit.SECONDS);
		group.shutdownGracefully();
	}

	public static void initNetwork() {
		logger.info("initNetwork-start");
		clientBootstrap.group(group);
		clientBootstrap.channel(NioSocketChannel.class);
		clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ObjectEncoder());

				ClassLoader libClassLoader = Event.class.getClassLoader();
				ClassResolver classResolver = ClassResolvers.softCachingConcurrentResolver(libClassLoader);
				ObjectDecoder objectDecoder = new ObjectDecoder(classResolver);
				ch.pipeline().addLast(objectDecoder);

				ch.pipeline().addLast(new ClientHandlerNetty());
			}
		});
		logger.info("initNetwork-end");
	}

	public static void send(Shot shot) {
		channelFuture.channel().writeAndFlush(shot);
	}

	public static void startNetwork() throws InterruptedException {
		logger.info("startNetwork-start");

		String host = GameHeader.getClientConfig().getNetworkConfig().getServerHost();
		int port = GameHeader.getClientConfig().getNetworkConfig().getServerPort();

		clientBootstrap.remoteAddress(new InetSocketAddress(host, port));
		channelFuture = clientBootstrap.connect().sync();

		new Thread(() -> {
			while (!ClientGameEngine.rune) {
				try {
					PingPacket ping = new PingPacket();
					channelFuture.channel().writeAndFlush(ping);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
		}).start();
		logger.info("startNetwork-end");
	}

	private Network() {
	}

}

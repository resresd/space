package com.github.resresd.games.resresdspace.server;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.ServiceConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.api.server.entities.EntitiesApi;
import com.github.resresd.games.resresdspace.api.server.network.NetWorkApi;
import com.github.resresd.games.resresdspace.api.server.player.PlayerApi;
import com.github.resresd.games.resresdspace.event.Event;
import com.github.resresd.games.resresdspace.server.config.ServerConfig;
import com.github.resresd.games.resresdspace.server.engine.ServerEngine;
import com.github.resresd.games.resresdspace.server.header.ServerHeader;
import com.github.resresd.games.resresdspace.server.header.network.NetWorkHeader;
import com.github.resresd.games.resresdspace.server.network.handler.ClientHandlerNetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;

public class Server {

	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private @Getter ServerBootstrap serverBootstrap = new ServerBootstrap();

	private @Getter EventLoopGroup serverWorkgroup = new NioEventLoopGroup();

	private @Getter ChannelFuture channelFuture;

	public void initApi() {
		// ENTITIES
		EntitiesApi.setEntityList(t -> ServerEngine.getSPACE_ENTITIES());

		// players
		PlayerApi.getPlayerDir = call -> ServerHeader.getPlayersDir();

		// network
		NetWorkApi.setBroadCastFunction(NetWorkHeader::sendBroadcastNetty);

	}

	public void initConfig() throws IOException {
		logger.info("initConfig-start");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		File configFile = ServerHeader.getConfigFile();
		if (!configFile.exists()) {
			logger.info("root dir is created :{}", configFile.getParentFile().mkdirs());
			logger.info("config file is created :{}", configFile.createNewFile());

			mapper.writeValue(configFile, ServerHeader.getServerConfig());
			throw new ServiceConfigurationError("please edit " + configFile.getAbsolutePath());
		} else {
			// READ
			ServerHeader.setServerConfig(mapper.readValue(configFile, ServerConfig.class));
			if (!ServerHeader.getServerConfig().isPrepare()) {
				throw new ServiceConfigurationError("please edit " + configFile.getAbsolutePath());
			}
		}
		logger.info("initConfig-end");
	}

	public void initData() throws IOException {
		logger.info("initData-start");
		StaticData.init();
		logger.info("initData-end");
	}

	public void initGame() {
		logger.info("initGame-start");
		ServerHeader.getServerEngine().init();
		logger.info("initGame-end");
	}

	public void initNetwork() {
		logger.info("initNetwork-start");

		ServerConfig serverConfig = ServerHeader.getServerConfig();
		int port = serverConfig.getNetworkConfig().getServerPort();

		serverBootstrap.group(serverWorkgroup).channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(port));
		serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
		serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(
						new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(Event.class.getClassLoader())));
				ch.pipeline().addLast(new ObjectEncoder());
				ch.pipeline().addLast(new ClientHandlerNetty());
			}
		});
		logger.info("initNetwork-end");
	}

	public void startGame() {
		logger.info("startGame-start");
		ServerHeader.getServerEngine().start();
		ServerHeader.getAI_ENGINE().start();
		logger.info("startGame-end");
	}

	public void startNetwork() {
		logger.info("startNetwork-start");
		this.channelFuture = serverBootstrap.bind();
		logger.info("startNetwork-end");
	}

}

package com.github.resresd.games.resresdspace.server;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.ServiceConfigurationError;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.server.config.ServerConfig;
import com.github.resresd.games.resresdspace.server.header.ServerHeader;
import com.github.resresd.games.resresdspace.server.header.network.NetWorkHeader;

public class Server {
	private int port;

	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public void initConfig() throws IOException {
		logger.info("initConfig-start");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		// CHECK and create
		if (!ServerHeader.getConfigFile().exists()) {
			if (!ServerHeader.getConfigFile().getParentFile().exists()) {
				ServerHeader.getConfigFile().getParentFile().mkdirs();
			}
			if (ServerHeader.getConfigFile().createNewFile()) {

			}
			mapper.writeValue(ServerHeader.getConfigFile(), ServerHeader.getServerConfig());
			throw new ServiceConfigurationError("please edit " + ServerHeader.getConfigFile().getAbsolutePath());
		}
		// READ
		ServerHeader.setServerConfig(mapper.readValue(ServerHeader.getConfigFile(), ServerConfig.class));
		if (!ServerHeader.getServerConfig().isPrepare()) {
			throw new ServiceConfigurationError("please edit " + ServerHeader.getConfigFile().getAbsolutePath());
		}
		logger.info("initConfig-end");
	}

	public void initNetwork() {
		logger.info("initNetwork-start");

		this.port = ServerHeader.getServerConfig().getNetworkConfig().getServerPort();

		ObjectSerializationCodecFactory oscf = new ObjectSerializationCodecFactory();
		oscf.setDecoderMaxObjectSize(Integer.MAX_VALUE);

		NetWorkHeader.getTcpHandler().getAcceptor().setHandler(NetWorkHeader.getTcpHandler());
		NetWorkHeader.getTcpHandler().getAcceptor().getFilterChain().addLast("codec", new ProtocolCodecFilter(oscf));
		NetWorkHeader.getTcpHandler().getAcceptor().getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

		logger.info("initNetwork-end");
	}

	public void startNetwork() throws IOException {
		logger.info("startNetwork-start");
		NetWorkHeader.getTcpHandler().getAcceptor().bind(new InetSocketAddress(port));
		logger.info("Server:startNetwork:TCP:STARTED");

		logger.info("startNetwork-end");
	}

	public void initGame() {
		logger.info("initGame-start");
		ServerHeader.getServerEngine().init();
		logger.info("initGame-end");
	}

	public void startGame() {
		logger.info("startGame-start");
		ServerHeader.getServerEngine().start();
		logger.info("startGame-end");
	}

	public void initData() throws IOException {
		logger.info("initData-start");
		StaticData.init();
		logger.info("initData-end");
	}

}

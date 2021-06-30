package com.github.resresd.games.resresdspace.client.online.game.handlers.network;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.client.online.game.engine.ClientGameEngine;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;
import com.github.resresd.games.resresdspace.client.online.game.header.network.NetworkHeader;
import com.github.resresd.games.resresdspace.network.packets.PingPacket;
import com.github.resresd.games.resresdspace.network.packets.ReadyPacket;
import com.github.resresd.games.resresdspace.network.packets.connections.ConnectionClosePacket;

public final class NetworkHandler {
	private NetworkHandler() {
	}

	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static IoSession session;

	public static void initNetwork() {
		logger.info("initNetwork-start");

		ObjectSerializationCodecFactory oscf = new ObjectSerializationCodecFactory();
		oscf.setDecoderMaxObjectSize(Integer.MAX_VALUE);

		NetworkHeader.getNioSocketConnector().setHandler(NetworkHeader.getOnlineHandler());
		// NetworkHeader.getNioSocketConnector().getFilterChain().addLast("logger", new
		// LoggingFilter());
		NetworkHeader.getNioSocketConnector().getFilterChain().addLast("codec", new ProtocolCodecFilter(oscf));
		NetworkHeader.getNioSocketConnector().getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

		logger.info("initNetwork-end");
	}

	public static void startNetwork() throws InterruptedException {
		logger.info("startNetwork-start");

		for (;;) {
			try {
				ConnectFuture future = NetworkHeader.getNioSocketConnector()
						.connect(new InetSocketAddress(GameHeader.getClientConfig().getNetworkConfig().getServerHost(),
								GameHeader.getClientConfig().getNetworkConfig().getServerPort()));
				future.awaitUninterruptibly();
				session = future.getSession();
				session.write(new ReadyPacket());
				break;
			} catch (RuntimeIoException e) {
				logger.error("Failed to connect.", e);
				Thread.sleep(5000);
			}

		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!ClientGameEngine.rune) {
					try {
						if (session != null) {
							session.write(new PingPacket());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		logger.info("startNetwork-end");
	}

	public static void exit() {
		NetworkHeader.getNioSocketConnector().broadcast(new ConnectionClosePacket());
		NetworkHeader.getNioSocketConnector().dispose();

	}

}

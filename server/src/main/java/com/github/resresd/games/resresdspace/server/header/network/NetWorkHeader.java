package com.github.resresd.games.resresdspace.server.header.network;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.github.resresd.games.resresdspace.server.network.handler.PostHandler;
import com.github.resresd.games.resresdspace.server.network.handler.tcp.TCPHandler;

import lombok.Getter;

public class NetWorkHeader {
	private NetWorkHeader() {
	}

	@Getter
	static TCPHandler tcpHandler = new TCPHandler();
	@Getter
	static PostHandler postHandler = new PostHandler();

	public static void sendBroadcast(Object object) {
		NioSocketAcceptor tcpAcceptor = tcpHandler.getAcceptor();

		if (tcpAcceptor != null) {
			tcpAcceptor.broadcast(object);
		}

	}

}

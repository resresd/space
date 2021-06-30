package com.github.resresd.games.resresdspace.client.online.game.header.network;

import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.github.resresd.games.resresdspace.client.online.game.handlers.network.OnlineHandler;
import com.github.resresd.games.resresdspace.client.online.game.handlers.network.tcp.TCPHandler;

import lombok.Getter;

public class NetworkHeader {
	private NetworkHeader() {
	}

	@Getter
	static NioDatagramConnector nioDatagramConnector = new NioDatagramConnector();
	@Getter
	static NioSocketConnector nioSocketConnector = new NioSocketConnector();

	@Getter
	static TCPHandler tcpHandler = new TCPHandler();
	@Getter
	static OnlineHandler onlineHandler = new OnlineHandler();

}

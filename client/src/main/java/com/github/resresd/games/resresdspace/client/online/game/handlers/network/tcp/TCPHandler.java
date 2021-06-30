package com.github.resresd.games.resresdspace.client.online.game.handlers.network.tcp;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.github.resresd.games.resresdspace.client.online.game.header.network.NetworkHeader;

public class TCPHandler extends IoHandlerAdapter {
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		NetworkHeader.getOnlineHandler().messageReceived(session, message);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		session.closeNow();
	}
}

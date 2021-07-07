package com.github.resresd.games.resresdspace.server.network.handler;

import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.network.packets.ReadyPacket;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Shot;
import com.github.resresd.games.resresdspace.server.engine.ServerEngine;

public class PostHandler extends IoHandlerAdapter {
	static Logger logger = LoggerFactory.getLogger(PostHandler.class);

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof Shot) {
			Shot shot = (Shot) message;
			ServerEngine.directShots.add(shot);
			Map<Long, IoSession> sessionsExc = session.getService().getManagedSessions();
			for (Long key : sessionsExc.keySet()) {
				if (session.getId() != key) {
					IoSession sessExc = sessionsExc.get(key);
					sessExc.write(message);
				}
			}

		} else if (message instanceof ReadyPacket) {
			// TODO READY
			new Thread(new Runnable() {

				@Override
				public void run() {
					for (SpaceEntity spaceEntity : ServerEngine.getSPACE_ENTITIES()) {
						session.write(spaceEntity);
					}

				}
			}).start();
		}

	}

}

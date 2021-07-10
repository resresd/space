package com.github.resresd.games.resresdspace.server.engine;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.server.header.ServerHeader;

public class AIEngine extends Thread {

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		currentThread.setName("Game:AIEngine");
		Thread.setDefaultUncaughtExceptionHandler(StaticData.EXCEPTION_HANDLER);

		while (ServerHeader.getServerEngine().active) {
			// ACTIONS

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				StaticData.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e);
			}
		}
	}
}

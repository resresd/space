package com.github.resresd.games.resresdspace.api.server.entities;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.UnaryOperator;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.api.server.network.NetWorkApi;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

public class EntitiesApi {

	public static UnaryOperator<CopyOnWriteArrayList<SpaceEntity>> entityList;

	public static void spawnEntity(SpaceEntity entity) {
		while (entityList == null) {
			if (entityList != null) {
				break;
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				StaticData.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e);
			}
		}
		entityList.apply(null).add(entity);
		if (NetWorkApi.broadCastFunction != null) {
			NetWorkApi.sendBroadCast(entity);
		}
	}

}

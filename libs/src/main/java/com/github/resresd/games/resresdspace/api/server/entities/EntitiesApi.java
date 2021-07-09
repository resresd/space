package com.github.resresd.games.resresdspace.api.server.entities;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.UnaryOperator;

import com.github.resresd.games.resresdspace.api.server.network.NetWorkApi;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

public class EntitiesApi {

	public static UnaryOperator<CopyOnWriteArrayList<SpaceEntity>> entityList;

	public static void spawnEntity(SpaceEntity entity) {
		while (entity == null) {
			System.err.println("ggggggggggggggggggggggggggggggggggggggggggggggggg");
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		entityList.apply(null).add(entity);
		NetWorkApi.sendBroadCast(entity);
	}

}

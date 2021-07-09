package com.github.resresd.games.resresdspace.api.server.network;

import java.util.function.UnaryOperator;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

public class NetWorkApi {

	public static UnaryOperator<Object> broadCastFunction;

	public static void sendBroadCast(SpaceEntity entity) {
		broadCastFunction.apply(entity);
	}

}

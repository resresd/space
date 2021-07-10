package com.github.resresd.games.resresdspace.api.server.network;

import java.util.function.UnaryOperator;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class NetWorkApi {

	private static @Getter @Setter UnaryOperator<Object> broadCastFunction;

	public static void sendBroadCast(SpaceEntity entity) {
		broadCastFunction.apply(entity);
	}

}

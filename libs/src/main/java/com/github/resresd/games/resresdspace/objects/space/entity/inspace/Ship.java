package com.github.resresd.games.resresdspace.objects.space.entity.inspace;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class Ship extends SpaceEntity {

	private static final long serialVersionUID = 8106186193197505136L;

	public Ship() {
		super();
	}

	private @Getter @Setter float shipRadius = 4.0F;

	@Getter
	@Setter
	public long lastShotTime;

}
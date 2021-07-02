package com.github.resresd.games.resresdspace.objects.space.entity.inspace;

import java.security.NoSuchAlgorithmException;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class Ship extends SpaceEntity {
	public Ship() throws NoSuchAlgorithmException {
		super();
	}

	private @Getter @Setter float shipRadius = 4.0F;

	@Getter
	@Setter
	public long lastShotTime;

}
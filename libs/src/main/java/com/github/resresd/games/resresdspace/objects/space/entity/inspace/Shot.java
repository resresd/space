package com.github.resresd.games.resresdspace.objects.space.entity.inspace;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class Shot extends SpaceEntity {

	private static final long serialVersionUID = -5159995027566648173L;

	public Shot() {
		super();
	}

	private @Getter @Setter double damage = 13.34D;
}

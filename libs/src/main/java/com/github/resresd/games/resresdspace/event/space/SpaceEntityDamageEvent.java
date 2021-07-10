package com.github.resresd.games.resresdspace.event.space;

import lombok.Getter;
import lombok.Setter;

public class SpaceEntityDamageEvent extends SpaceEntityEvent {

	private static final long serialVersionUID = 1204414733045062462L;
	private @Getter @Setter Double damage;
}

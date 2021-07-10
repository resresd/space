package com.github.resresd.games.resresdspace.players;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class PlayerData implements Serializable {

	private static final long serialVersionUID = -8406769079642010791L;

	private @Getter @Setter String uuid;
	private @Getter @Setter String name;

	private @Getter @Setter float money;
}

package com.github.resresd.games.resresdspace.relations;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class Relation implements Serializable {
	private @Getter @Setter String point1;
	private @Getter @Setter String point2;

	private static final long serialVersionUID = -5383678347763771553L;

	public static enum relationType {
		OWN, FRIEND, NEITRAL, ENEMY, NA
	}
}

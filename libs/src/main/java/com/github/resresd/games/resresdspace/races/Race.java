package com.github.resresd.games.resresdspace.races;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class Race {
	private @Getter String uuid = UUID.randomUUID().toString();
	private @Getter @Setter String name;
}

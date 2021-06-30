package com.github.resresd.games.resresdspace.event.space;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class SpaceEntityEvent extends SpaceEvent {
	@Getter
	@Setter
	SpaceEntity targetEntity;
}

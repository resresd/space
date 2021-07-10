package com.github.resresd.games.resresdspace.event.space;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class SpaceEntityEvent extends SpaceEvent {

	private static final long serialVersionUID = 1774641412778662380L;
	@Getter
	@Setter
	SpaceEntity targetEntity;
}

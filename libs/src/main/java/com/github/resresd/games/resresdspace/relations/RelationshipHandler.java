package com.github.resresd.games.resresdspace.relations;

import java.util.concurrent.ConcurrentHashMap;

import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.games.resresdspace.players.Player;
import com.github.resresd.games.resresdspace.races.Race;
import com.github.resresd.games.resresdspace.relations.Relation.relationType;

import lombok.Getter;
import lombok.Setter;

public class RelationshipHandler {
	private @Getter @Setter ConcurrentHashMap<String, Relation> relData;

	public RelationshipHandler(ConcurrentHashMap<String, Relation> realtionsMap) {
		relData = realtionsMap;
	}

	public relationType checkRel(Player player, SpaceEntity ship) {
		String shipOwner = ship.getOwner();
		// корабль игрока
		if (player.getUuid().equals(shipOwner)) {
			return Relation.relationType.OWN;
		}

		Race playerRase = player.getRace();

		return relationType.NA;
	}
}

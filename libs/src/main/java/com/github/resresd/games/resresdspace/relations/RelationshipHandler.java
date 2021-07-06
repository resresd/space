package com.github.resresd.games.resresdspace.relations;

import java.util.concurrent.ConcurrentHashMap;

import com.github.resresd.games.resresdspace.objects.space.entity.inspace.Ship;
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

	public relationType checkRel(Player player, Ship ship) {
		String shipOwner = ship.getOwner();
		// корабль игрока
		if (player.equals(shipOwner)) {
			return Relation.relationType.OWN;
		}

		Race playerRase = player.getRace();

		return relationType.NA;
	}

}

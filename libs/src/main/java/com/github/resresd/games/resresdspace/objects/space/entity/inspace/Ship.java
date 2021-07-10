package com.github.resresd.games.resresdspace.objects.space.entity.inspace;

import org.joml.Vector3d;

import com.github.resresd.games.resresdspace.StaticData;
import com.github.resresd.games.resresdspace.api.server.entities.EntitiesApi;
import com.github.resresd.games.resresdspace.objects.inship.Item;
import com.github.resresd.games.resresdspace.objects.space.entity.basic.SpaceEntity;
import com.github.resresd.utils.NumberUtils;

import lombok.Getter;
import lombok.Setter;

public class Ship extends SpaceEntity {

	private static final long serialVersionUID = 8106186193197505136L;

	@Getter
	@Setter
	public long lastShotTime;

	@Override
	public void spawnResource(SpaceEntity entity) {
		for (Item item : entity.getInventory().returnAllItems()) {
			try {
				Box box = new Box();

				Vector3d pos = entity.getPosition();

				double o = 0.4D;
				double x = NumberUtils.randomDoubleInRange(pos.x - o, pos.x + o);
				double y = NumberUtils.randomDoubleInRange(pos.y - o, pos.y + o);
				double z = NumberUtils.randomDoubleInRange(pos.z - o, pos.z + o);

				box.setPosition(x, y, z);
				box.getInventory().setMaxSize(item.getSize());
				box.getInventory().addItem(item);

				// TODO create data for Box (mesh , etc)
				EntitiesApi.spawnEntity(box);
			} catch (Exception e) {
				StaticData.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e);
			}
		}
	}
}

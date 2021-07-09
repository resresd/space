package com.github.resresd.games.resresdspace.objects.space.entity.basic;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.github.resresd.games.resresdspace.objects.inside.Inventory;
import com.github.resresd.utils.NumberUtils;

import lombok.Getter;
import lombok.Setter;

public class SpaceEntity implements Serializable, SpaceEntityInterface {

	private static final long serialVersionUID = -3968104750159381671L;

	private @Getter @Setter int id;
	private @Getter @Setter String owner;
	private @Getter Inventory inventory = new Inventory();

	public SpaceEntity() {
		setId(NumberUtils.randomIntInRange(1, Integer.MAX_VALUE));
	}

	private @Getter @Setter float scale;
	private @Getter @Setter Double healthMax = 100.0D;
	private @Getter @Setter Double health = (healthMax);

	private @Getter Vector3d position = new Vector3d(0, 0, 10);
	private @Getter @Setter Vector3f linearVel = new Vector3f();

	@Getter
	Vector4f projectileVelocity = new Vector4f(0, 0, 0, 0);

	//
	@Override
	public void updatePosition(double x, double y, double z) {
		this.position.x = this.position.x + x;
		this.position.y = this.position.y + y;
		this.position.z = this.position.z + z;
	}

	@Override
	public void setPosition(double x, double y, double z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	@Override
	public void spawn() {

	}

	@Override
	public void destroy() {
		if (health != 0D) {
			health = 0D;
		}
		spawnResource(this);
	}

	@Override
	public boolean damage(Double dmg) {
		this.health = this.health - dmg;
		if (health <= 0.0D) {
			destroy();
			return true;
		}
		return false;
	}

	public boolean removeFromList(CopyOnWriteArrayList<? extends SpaceEntity> copyOnWriteArrayList) {
		return copyOnWriteArrayList.remove(getFromEngine(copyOnWriteArrayList, this));
	}

	public static SpaceEntity getFromEngine(CopyOnWriteArrayList<? extends SpaceEntity> localShips,
			SpaceEntity targetEntity) {
		for (SpaceEntity ship : localShips) {
			if (targetEntity.getId() == ship.getId()) {
				return ship;
			}
		}
		return null;
	}

	@Override
	public void spawnResource(SpaceEntity entity) {
		// TODO Auto-generated method stub

	}

}

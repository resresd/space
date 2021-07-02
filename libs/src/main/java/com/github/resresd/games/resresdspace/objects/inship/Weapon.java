package com.github.resresd.games.resresdspace.objects.inship;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class Weapon implements Serializable {

	private static final long serialVersionUID = 734610425336374022L;

	@Getter
	@Setter
	private int weaponPossition;
	@Getter
	@Setter
	private float shotVelocity = 450.0F;// скорость
	@Getter
	@Setter
	private float shotSeparation = 0.8f;// разброс
	@Getter
	@Setter
	private int shotMilliseconds = 20;// ПЕРЕЗАРЯДКА
}

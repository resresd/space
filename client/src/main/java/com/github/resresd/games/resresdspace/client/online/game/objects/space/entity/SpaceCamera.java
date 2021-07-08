package com.github.resresd.games.resresdspace.client.online.game.objects.space.entity;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class SpaceCamera {
	// линейное ускорение
	public Vector3f linearAcc = new Vector3f();
	// линейная скорость
	public Vector3f linearVel = new Vector3f();
	// линейное торможение
	public float linearDamping = 0.08f;

	/** ALWAYS rotation about the local XYZ axes of the camera! */
	// угловое ускорение
	public Vector3f angularAcc = new Vector3f();
	// угловая скорость
	public Vector3f angularVel = new Vector3f();
	// угловое торможение
	public float angularDamping = 0.5f;

	public Vector3d position = new Vector3d(0, 0, 10);
	public Quaternionf rotation = new Quaternionf();

	public SpaceCamera update(float deltaTime) {
		// update linear velocity based on linear acceleration
		// update angular velocity based on angular acceleration
		// update the rotation based on the angular velocity
		linearVel.fma(deltaTime, linearAcc);
		angularVel.fma(deltaTime, angularAcc);
		rotation.integrate(deltaTime, angularVel.x, angularVel.y, angularVel.z);

		angularVel.mul(1.0f - angularDamping * deltaTime);

		// TODO если камера привязана к кораблю
		// TODO получить место корабля, получить позицию в мире, получить углы, (и
		// проверить на допустимые углы)

		// update position based on linear velocity
		position.fma(deltaTime, linearVel);

		float mul = 1.0f - linearDamping * deltaTime;

		linearVel.mul(mul);
		return this;
	}

	public Vector3f right(Vector3f dest) {
		return rotation.positiveX(dest);
	}

	public Vector3f up(Vector3f dest) {
		return rotation.positiveY(dest);
	}

	public Vector3f forward(Vector3f dest) {
		return rotation.positiveZ(dest).negate();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpaceCamera [linearAcc=");
		builder.append(linearAcc);
		builder.append(", linearVel=");
		builder.append(linearVel);
		builder.append(", linearDamping=");
		builder.append(linearDamping);
		builder.append(", angularAcc=");
		builder.append(angularAcc);
		builder.append(", angularVel=");
		builder.append(angularVel);
		builder.append(", angularDamping=");
		builder.append(angularDamping);
		builder.append(", position=");
		builder.append(position);
		builder.append(", rotation=");
		builder.append(rotation);
		builder.append("]");
		return builder.toString();
	}

}
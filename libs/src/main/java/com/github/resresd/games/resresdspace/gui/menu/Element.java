package com.github.resresd.games.resresdspace.gui.menu;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.Serializable;

import org.lwjgl.system.NativeType;

import lombok.Setter;

public class Element implements Serializable {
	public Element() {

	}

	public Element(float px1, float py1, float px2, float py2) {
		x1 = px1;
		x2 = px2;
		y1 = py1;
		y2 = py2;
	}

	private static final long serialVersionUID = 1567822315194134058L;
	private int program;
	private @Setter int realWindowHeight;
	private @Setter int realWindowWidth;

	public void setProg(@NativeType("GLuint") int prog) {
		program = prog;
	}

	float x1;
	float y1;
	float x2;
	float y2;

	public void draw() {
		float tx1 = x1;
		float ty1 = y1;
		float tx2 = x2;
		float ty2 = y2;

		tx1 = CoordUtils.forGlCoordByReal(tx1, realWindowWidth);
		ty1 = CoordUtils.forGlCoordByReal(ty1, realWindowHeight);

		tx2 = CoordUtils.forGlCoordByReal(tx2, realWindowWidth);
		ty2 = CoordUtils.forGlCoordByReal(ty2, realWindowHeight);
		drawRaw(tx1, ty1, tx2, ty2);

	}

	private void drawRaw(float tx1, float ty1, float tx2, float ty2) {
		glBegin(GL_LINES);
		glVertex2f(tx1, ty1);

		glVertex2f(tx2, ty2);
		glEnd();
	}

	public void setFpos(float x, float y) {
		x1 = x;
		y1 = y;
	}

	public void setSpos(float x, float y) {
		x2 = x;
		y2 = y;
	}
}

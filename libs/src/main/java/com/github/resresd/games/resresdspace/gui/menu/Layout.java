package com.github.resresd.games.resresdspace.gui.menu;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.system.NativeType;

import lombok.Getter;
import lombok.Setter;

public class Layout implements Serializable {

	private static final long serialVersionUID = -6380663922599538103L;

	private @Getter final ConcurrentHashMap<Integer, Element> elementsHashMap = new ConcurrentHashMap<>();

	private int program;
	private @Setter int realWindowHeight;
	private @Setter int realWindowWidth;

	public void setProg(@NativeType("GLuint") int prog) {
		program = prog;
	}

	public void draw() {
		for (Map.Entry<Integer, Element> layoutEntry : elementsHashMap.entrySet()) {
			Element element = layoutEntry.getValue();
			element.setProg(program);
			element.setRealWindowHeight(realWindowHeight);
			element.setRealWindowWidth(realWindowWidth);
			element.draw();
		}
	}
}

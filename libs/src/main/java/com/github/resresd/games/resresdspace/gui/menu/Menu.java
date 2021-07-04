package com.github.resresd.games.resresdspace.gui.menu;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

public class Menu implements Serializable {

	private static final long serialVersionUID = 6440313206059117660L;
	private @Getter final ConcurrentHashMap<Integer, Layout> layoutsHashMap = new ConcurrentHashMap<>();
	private @Setter int program;
	private @Setter int realWindowHeight;
	private @Setter int realWindowWidth;

	public void draw() {
		for (Map.Entry<Integer, Layout> layoutEntry : layoutsHashMap.entrySet()) {
			Layout layout = layoutEntry.getValue();
			layout.setProg(program);
			layout.setRealWindowHeight(realWindowHeight);
			layout.setRealWindowWidth(realWindowWidth);
			layout.draw();
		}
	}

}

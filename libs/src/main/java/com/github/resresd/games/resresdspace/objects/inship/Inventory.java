package com.github.resresd.games.resresdspace.objects.inship;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.LimitExceededException;

import lombok.Getter;
import lombok.Setter;

public class Inventory implements Serializable {
	@Getter
	@Setter
	private int maxSize;
	private CopyOnWriteArrayList<Item> items = new CopyOnWriteArrayList<>();

	public void addItem(Item item) throws LimitExceededException {
		if (items.size() >= maxSize) {
			throw new LimitExceededException();
		}
		items.add(item);
	}

	public CopyOnWriteArrayList<Item> returnAllItems() {
		return new CopyOnWriteArrayList<>(items);
	}
}

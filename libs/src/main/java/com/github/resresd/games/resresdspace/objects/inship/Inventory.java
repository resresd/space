package com.github.resresd.games.resresdspace.objects.inship;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.LimitExceededException;

import lombok.Getter;
import lombok.Setter;

public class Inventory implements Serializable {

	private static final long serialVersionUID = -7735171814549017233L;
	@Getter
	@Setter
	private int maxSize;
	private CopyOnWriteArrayList<Item> items = new CopyOnWriteArrayList<>();

	public void addItem(Item item) throws LimitExceededException {
		float currentSize = returnCurrentSize(this);
		if (currentSize >= maxSize) {
			throw new LimitExceededException();
		}
		items.add(item);
	}

	public static float returnCurrentSize(Inventory inventory) {
		float answ = 0F;
		for (Item inventItem : inventory.items) {
			answ = answ + inventItem.getSize();
		}
		return answ;
	}

	public CopyOnWriteArrayList<Item> returnAllItems() {
		return items;
	}
}

package com.example.instructions;

import net.minecraft.item.Item;

public class InstructionBlock {
	// item to set to active hand
	public final Item item;
	
	// row and column to paint
	public final Point point;
	
	// left click (0), right click (1), or no click (-1)
	public final int clickType;
	
	public static final int NO_CLICK = -1;
	public static final int LEFT_CLICK = 0;
	public static final int RIGHT_CLICK = 1;
	
	public InstructionBlock(Item item, Point point, int clickType)
	{
		this.item = item;
		this.point = point;
		this.clickType = clickType;
	}
	
	public boolean isRightClick() { return clickType == 1; }
	public boolean isLeftClick() { return clickType == 0;}
	public boolean isNoClick()	{ return !(isRightClick() || isLeftClick()); }
	
	public int row() { return point.x; }
	public int col() { return point.y; }
}

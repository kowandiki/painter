package com.example.actions;

import com.example.FabricPainterClient;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;

public class ShiftInv {

	/*
	 * Takes the item you want in your active hand as a parameter
	 * searches inventory for that item, if it finds it, it moves it to the current main hand slot
	 * or, if the item is in the hotbar, it swaps to that slot instead of moving item
	 */
	public static boolean setItemInActiveHand(Item item)
	{
//		int index = FabricPainterClient.mc.player.getInventory().indexOf(item.getDefaultStack());
//		int index = FabricPainterClient.mc.player.getInventory()
		int index = getItemIndex(item, FabricPainterClient.mc.player.getInventory());
		
		System.out.println(index);
		// not in inventory
		if (index == -1)
			return false;
		
		// already selected, do nothing
		if (index == FabricPainterClient.mc.player.getInventory().getSelectedSlot())
			return true;
		
		// swap active hand
		if (index < 9)
		{
			// item is in hotbar, so change active slot to that
			FabricPainterClient.mc.player.getInventory().setSelectedSlot(index);
			return true;
		}
		
		//otherwise, swap slots
//		FabricPainterClient.mc.interactionManager.pickFromInventory(index);
		FabricPainterClient.mc.interactionManager.clickSlot(
			FabricPainterClient.mc.player.playerScreenHandler.syncId,
			index,
			FabricPainterClient.mc.player.getInventory().getSelectedSlot(),
			SlotActionType.SWAP,
			FabricPainterClient.mc.player
		);
		
		return true;
	}
	
	private static int getItemIndex(Item item, PlayerInventory inventory) {
		
		for (int i = 0; i < PlayerInventory.MAIN_SIZE; i++) {
			
			if (inventory.getStack(i).getItem() == item) {
				return i;
			}
		}
		
		return -1;
	}
}

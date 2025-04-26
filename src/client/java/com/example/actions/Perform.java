package com.example.actions;

import com.example.FabricPainterClient;
import com.example.instructions.InstructionBlock;

public class Perform {

	public static boolean attack() {
		
		if (FabricPainterClient.mc.targetedEntity != null) {
			
			FabricPainterClient.mc.interactionManager.attackEntity(FabricPainterClient.mc.player, FabricPainterClient.mc.targetedEntity);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Start using the item the player is holding on the targeted entity
	 * @return true if interacts with entity, false otherwise
	 */
	public static boolean startUse() {
		
		if (FabricPainterClient.mc.targetedEntity != null && FabricPainterClient.mc.player != null) {
			
			FabricPainterClient.mc.interactionManager.interactEntity(
					FabricPainterClient.mc.player, 
					FabricPainterClient.mc.targetedEntity,
					FabricPainterClient.mc.player.getActiveHand());
			
			return true;
		}
		
		return false;
	}
	
	public static boolean stopUse() {
		
		if (FabricPainterClient.mc.player == null) {
			return false;
		}
		
		FabricPainterClient.mc.interactionManager.stopUsingItem(FabricPainterClient.mc.player);
		
		return true;
	}
	
	public static void doInstruction(InstructionBlock instruction) {
		
		if (instruction == null) {
			return;
		}
		
		if (instruction.item != null) {
			ShiftInv.setItemInActiveHand(instruction.item);
		}
		
		if (instruction.point != null)
		{
			FabricPainterClient.mc.player.setYaw((float)  Positions.getOrientation(instruction.point.y, instruction.point.x, Positions.getPlayerCardinality()).yaw);
			FabricPainterClient.mc.player.setPitch((float)Positions.getOrientation(instruction.point.y, instruction.point.x, Positions.getPlayerCardinality()).pitch);
		}
		
		switch (instruction.clickType)
		{
		case InstructionBlock.LEFT_CLICK:
			attack();
			break;
		case InstructionBlock.RIGHT_CLICK:
			startUse();
			break;
		}
	}
	
	public static void doMoveInstruction(InstructionBlock instruction) {
		
		if (instruction == null)
			return;
		
		if (instruction.item != null) 
			ShiftInv.setItemInActiveHand(instruction.item);
		
		if (instruction.point != null) {
			
			FabricPainterClient.mc.player.setYaw((float)  Positions.getOrientation(instruction.point.y, instruction.point.x, Positions.getPlayerCardinality()).yaw);
			FabricPainterClient.mc.player.setPitch((float)Positions.getOrientation(instruction.point.y, instruction.point.x, Positions.getPlayerCardinality()).pitch);
		}
	}
	
	public static void doClickInstruction(InstructionBlock instruction) {
		
		if (instruction == null)
			return;
		
		if (instruction.item != null)
			ShiftInv.setItemInActiveHand(instruction.item);
		
		switch (instruction.clickType) {
		case InstructionBlock.LEFT_CLICK:
			attack();
			break;
		case InstructionBlock.RIGHT_CLICK:
			startUse();
			break;
		}
	}
}

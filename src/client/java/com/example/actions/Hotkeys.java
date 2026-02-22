package com.example.actions;

import org.lwjgl.glfw.GLFW;

import com.example.FabricPainter;
import com.example.FabricPainterClient;
import com.example.gui.GuiBase;
import com.example.gui.GuiScreen;
import com.example.instructions.InstructionBlock;
import com.example.instructions.Point;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Hotkeys {
	
	private static KeyBinding startstop;
	private static KeyBinding openGUI;
	
	private static KeyBinding right;
	private static KeyBinding left;
	private static KeyBinding up;
	private static KeyBinding down;
	
	public static int row = 0;
	private static int col = 0;
	
	public static boolean toggle = false;
	public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(FabricPainter.MOD_ID, "painter"));
	
	public static boolean getToggle() {
		return toggle;
	}
	
	public Hotkeys() {
		
		startstop = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Start/Stop Painting",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_I,
				CATEGORY
			));
		
		openGUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Open GUI",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_O,
				CATEGORY
			));
		
		right = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"increments col by 1",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT,
				CATEGORY
				));
		
		left = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"decrements col by 1",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_LEFT,
				CATEGORY
				));
		
		up = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"decrements row by 1",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UP,
				CATEGORY
				));
		
		down = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"increments row by 1",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_DOWN,
				CATEGORY
				));

	}

	
	private void updateOrientation(MinecraftClient client) {
		
		if (Queue.instMan != null && !Queue.instMan.isColorGridNull()) {
//			Perform.doInstruction(new InstructionBlock(Queue.instMan.getColorAtPoint(new Point(col,row)).item, new Point(col, row), InstructionBlock.NO_CLICK));
			Perform.doInstruction(new InstructionBlock(null, new Point(col, row), InstructionBlock.NO_CLICK));
			client.player.sendMessage(Text.literal(Queue.instMan.getColorAtPoint(new Point(col,row)).toString()), true);
		} else {
			
			Perform.doInstruction(new InstructionBlock(null, new Point(col, row), InstructionBlock.NO_CLICK));
		}
	}
	
	public void listen() {
		
		FabricPainter.LOGGER.info("Hotkeys started listening");
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			
			while(left.wasPressed()) {
				
				if (--col < 0) {
					col = 31;
				}
				updateOrientation(client);
			}
			
			while(right.wasPressed()) {
				
				if (++col > 31) {
					col = 0;
				}
				updateOrientation(client);
			}
			
			while(up.wasPressed()) {
				
				if (--row < 0) {
					row = 31;
				}
				updateOrientation(client);
			}
			
			while(down.wasPressed()) {
				
				if (++row > 31) {
					row = 0;
				}
				updateOrientation(client);
			}
			
			while(startstop.wasPressed()) {
				
				
				if (toggle = !toggle) {
					
					client.player.sendMessage(Text.literal("started"), false);
					
					Vec3d playerPos = new Vec3d(
							FabricPainterClient.mc.player.getX(),
							FabricPainterClient.mc.player.getY(),
							FabricPainterClient.mc.player.getZ());
					
					Queue.position = playerPos;
					
				} else {
					
					client.player.sendMessage(Text.literal("stopped"), false);
					
					Queue.position = null;
					Queue.instMan.nullSponge();
					Queue.resetCounter();
					
				}
				
			}
			
			while(openGUI.wasPressed()) {
				
//				FabricPainterClient.mc.getInstance().setScreen(new GuiScreen(new GuiBase()));
				MinecraftClient.getInstance().setScreen(new GuiScreen(new GuiBase()));
			}
		});
	}
}

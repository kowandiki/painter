package com.example;

import com.example.actions.Hotkeys;
import com.example.actions.Queue;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class FabricPainterClient implements ClientModInitializer {
	
	public static final Hotkeys hotkeys = new Hotkeys();
	
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		
		FabricPainter.LOGGER.info("Client init");
		
		hotkeys.listen();
		Hotkeys.toggle = false;
		Queue.startCounter();
	}
}
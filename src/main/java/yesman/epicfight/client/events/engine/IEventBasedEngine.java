package yesman.epicfight.client.events.engine;

import yesman.epicfight.platform.fabric.event.SimpleEventBus;

public interface IEventBasedEngine {
	public void gameEventBus(SimpleEventBus gameEventBus);
	
	public void modEventBus(SimpleEventBus modEventBus);
	
	public static void init(SimpleEventBus gameEventBus) {
		RenderEngine.getInstance().gameEventBus(gameEventBus);
		ControlEngine.getInstance().gameEventBus(gameEventBus);
	}
}

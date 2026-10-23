package yesman.epicfight.platform.fabric.client.event;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.platform.fabric.event.FabricEvent;

public abstract class InputEvent extends FabricEvent {
    public static class MouseScrollingEvent extends InputEvent {
        private final double scrollDeltaX, scrollDeltaY;

        public MouseScrollingEvent(double scrollDeltaX, double scrollDeltaY) {
            this.scrollDeltaX = scrollDeltaX;
            this.scrollDeltaY = scrollDeltaY;
        }

        public double getScrollDeltaX() { return this.scrollDeltaX; }
        public double getScrollDeltaY() { return this.scrollDeltaY; }
    }

    public static class InteractionKeyMappingTriggered extends InputEvent {
        private final int button;
        private final KeyMapping keyMapping;
        private final InteractionHand hand;
        private boolean handSwing = true;

        public InteractionKeyMappingTriggered(int button, KeyMapping keyMapping, InteractionHand hand) {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        public int getButton() { return this.button; }
        public KeyMapping getKeyMapping() { return this.keyMapping; }
        public InteractionHand getHand() { return this.hand; }
        public boolean shouldSwingHand() { return this.handSwing; }
        public void setSwingHand(boolean value) { this.handSwing = value; }
        public boolean isAttack() { return this.button == 0; }
        public boolean isUseItem() { return this.button == 1; }
        public boolean isPickBlock() { return this.button == 2; }
    }
}

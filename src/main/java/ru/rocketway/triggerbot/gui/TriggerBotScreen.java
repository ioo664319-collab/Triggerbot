package ru.rocketway.triggerbot.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import ru.rocketway.triggerbot.RocketTriggerBot;

public class TriggerBotScreen extends Screen {
    public TriggerBotScreen() { super(Text.literal("Rocket TriggerBot")); }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 90; // Сдвинули вверх, чтобы всё влезло

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("TriggerBot: " + (RocketTriggerBot.ENABLED ? "ON" : "OFF")),
                b -> {
                    RocketTriggerBot.ENABLED = !RocketTriggerBot.ENABLED;
                    b.setMessage(Text.literal("TriggerBot: " + (RocketTriggerBot.ENABLED ? "ON" : "OFF")));
                }
        ).dimensions(cx - 100, y, 200, 20).build());

        this.addDrawableChild(new SliderWidget(cx - 100, y + 25, 200, 20,
                Text.literal("Base Delay: " + RocketTriggerBot.DELAY_MS + "ms"), (RocketTriggerBot.DELAY_MS - 50) / 450.0) {
            @Override protected void updateMessage() { setMessage(Text.literal("Base Delay: " + (int)(50 + value * 450) + "ms")); }
            @Override protected void applyValue() { RocketTriggerBot.DELAY_MS = (int)(50 + value * 450); }
        });

        this.addDrawableChild(new SliderWidget(cx - 100, y + 50, 200, 20,
                Text.literal("Range: " + RocketTriggerBot.RANGE + "m"), (RocketTriggerBot.RANGE - 1) / 9) {
            @Override protected void updateMessage() { setMessage(Text.literal("Range: " + String.format("%.1f", 1 + value * 9) + "m")); }
            @Override protected void applyValue() { RocketTriggerBot.RANGE = 1 + value * 9; }
        });

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Ignore mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "YES" : "NO")),
                b -> {
                    RocketTriggerBot.IGNORE_MOBS = !RocketTriggerBot.IGNORE_MOBS;
                    b.setMessage(Text.literal("Ignore mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "YES" : "NO")));
                }
        ).dimensions(cx - 100, y + 75, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Smart Crits: " + (RocketTriggerBot.SMART_CRITS ? "ON" : "OFF")),
                b -> {
                    RocketTriggerBot.SMART_CRITS = !RocketTriggerBot.SMART_CRITS;
                    b.setMessage(Text.literal("Smart Crits: " + (RocketTriggerBot.SMART_CRITS ? "ON" : "OFF")));
                }
        ).dimensions(cx - 100, y + 100, 200, 20).build());

        // НОВАЯ КНОПКА РАНДОМА
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Randomize Delay: " + (RocketTriggerBot.RANDOM_DELAY ? "ON" : "OFF")),
                b -> {
                    RocketTriggerBot.RANDOM_DELAY = !RocketTriggerBot.RANDOM_DELAY;
                    b.setMessage(Text.literal("Randomize Delay: " + (RocketTriggerBot.RANDOM_DELAY ? "ON" : "OFF")));
                }
        ).dimensions(cx - 100, y + 125, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> this.close()).dimensions(cx - 100, y + 155, 200, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }
    @Override public boolean shouldPause() { return false; }
}

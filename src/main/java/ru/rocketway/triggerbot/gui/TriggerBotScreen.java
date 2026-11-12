package ru.rocketway.triggerbot.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import ru.rocketway.triggerbot.RocketTriggerBot;

public class TriggerBotScreen extends Screen {
    public TriggerBotScreen() { 
        super(Text.literal("Dark Trigger")); 
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 85; 

        // 1. Вкл/Выкл Триггербота
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("TriggerBot: " + (RocketTriggerBot.ENABLED ? "§aON" : "§cOFF")),
                b -> {
                    RocketTriggerBot.ENABLED = !RocketTriggerBot.ENABLED;
                    b.setMessage(Text.literal("TriggerBot: " + (RocketTriggerBot.ENABLED ? "§aON" : "§cOFF")));
                }
        ).dimensions(cx - 100, y, 200, 20).build());

        // 2. Вкл/Выкл Aim Assist
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Aim Assist: " + (RocketTriggerBot.AIM_ASSIST ? "§aON" : "§cOFF")),
                b -> {
                    RocketTriggerBot.AIM_ASSIST = !RocketTriggerBot.AIM_ASSIST;
                    b.setMessage(Text.literal("Aim Assist: " + (RocketTriggerBot.AIM_ASSIST ? "§aON" : "§cOFF")));
                }
        ).dimensions(cx - 100, y + 25, 200, 20).build());

        // 3. Задержка удара
        this.addDrawableChild(new SliderWidget(cx - 100, y + 50, 200, 20,
                Text.literal("Delay: " + RocketTriggerBot.DELAY_MS + "ms"), (RocketTriggerBot.DELAY_MS - 50) / 450.0) {
            @Override protected void updateMessage() { setMessage(Text.literal("Delay: " + (int)(50 + value * 450) + "ms")); }
            @Override protected void applyValue() { RocketTriggerBot.DELAY_MS = (int)(50 + value * 450); }
        });

        // 4. Сила наведения (Aim Speed)
        this.addDrawableChild(new SliderWidget(cx - 100, y + 75, 200, 20,
                Text.literal("Aim Speed: " + String.format("%.1f", RocketTriggerBot.AIM_SPEED)), (RocketTriggerBot.AIM_SPEED - 0.1) / 9.9) {
            @Override protected void updateMessage() { setMessage(Text.literal("Aim Speed: " + String.format("%.1f", 0.1 + value * 9.9))); }
            @Override protected void applyValue() { RocketTriggerBot.AIM_SPEED = 0.1 + value * 9.9; }
        });

        // 5. Радиус
        this.addDrawableChild(new SliderWidget(cx - 100, y + 100, 200, 20,
                Text.literal("Range: " + RocketTriggerBot.RANGE + "m"), (RocketTriggerBot.RANGE - 1) / 9) {
            @Override protected void updateMessage() { setMessage(Text.literal("Range: " + String.format("%.1f", 1 + value * 9) + "m")); }
            @Override protected void applyValue() { RocketTriggerBot.RANGE = 1 + value * 9; }
        });

        // 6. Игнор мобов
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Ignore mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "§aYES" : "§cNO")),
                b -> {
                    RocketTriggerBot.IGNORE_MOBS = !RocketTriggerBot.IGNORE_MOBS;
                    b.setMessage(Text.literal("Ignore mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "§aYES" : "§cNO")));
                }
        ).dimensions(cx - 100, y + 125, 200, 20).build());

        // 7. Рандом задержки
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Random Delay: " + (RocketTriggerBot.RANDOM_DELAY ? "§aON" : "§cOFF")),
                b -> {
                    RocketTriggerBot.RANDOM_DELAY = !RocketTriggerBot.RANDOM_DELAY;
                    b.setMessage(Text.literal("Random Delay: " + (RocketTriggerBot.RANDOM_DELAY ? "§aON" : "§cOFF")));
                }
        ).dimensions(cx - 100, y + 150, 200, 20).build());

        // 8. Закрыть
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§cClose Menu"), b -> this.close()).dimensions(cx - 100, y + 180, 200, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x99000000); 
        ctx.drawCenteredTextWithShadow(this.textRenderer, "§l§cDARK TRIGGER", this.width / 2, this.height / 2 - 125, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "§7Premium TriggerBot", this.width / 2, this.height / 2 - 110, 0xAAAAAA);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}

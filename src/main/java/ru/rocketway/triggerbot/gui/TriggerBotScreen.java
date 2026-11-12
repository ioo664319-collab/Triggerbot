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
        int startY = this.height / 2 - 80;

        // Вкл/Выкл Триггербота
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7TriggerBot: " + (RocketTriggerBot.ENABLED ? "§c[ ON ]" : "§8[ OFF ]")),
                b -> {
                    RocketTriggerBot.ENABLED = !RocketTriggerBot.ENABLED;
                    b.setMessage(Text.literal("§7TriggerBot: " + (RocketTriggerBot.ENABLED ? "§c[ ON ]" : "§8[ OFF ]")));
                }
        ).dimensions(cx - 105, startY, 210, 18).build());

        // Вкл/Выкл Aim Assist
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Aim Assist: " + (RocketTriggerBot.AIM_ASSIST ? "§c[ ON ]" : "§8[ OFF ]")),
                b -> {
                    RocketTriggerBot.AIM_ASSIST = !RocketTriggerBot.AIM_ASSIST;
                    b.setMessage(Text.literal("§7Aim Assist: " + (RocketTriggerBot.AIM_ASSIST ? "§c[ ON ]" : "§8[ OFF ]")));
                }
        ).dimensions(cx - 105, startY + 22, 210, 18).build());

        // Слайдер задержки
        this.addDrawableChild(new SliderWidget(cx - 105, startY + 44, 210, 18,
                Text.literal("§7Delay: §f" + RocketTriggerBot.DELAY_MS + "ms"), (RocketTriggerBot.DELAY_MS - 50) / 450.0) {
            @Override protected void updateMessage() { setMessage(Text.literal("§7Delay: §f" + (int)(50 + value * 450) + "ms")); }
            @Override protected void applyValue() { RocketTriggerBot.DELAY_MS = (int)(50 + value * 450); }
        });

        // Слайдер Aim Speed
        this.addDrawableChild(new SliderWidget(cx - 105, startY + 66, 210, 18,
                Text.literal("§7Aim Speed: §f" + String.format("%.1f", RocketTriggerBot.AIM_SPEED)), (RocketTriggerBot.AIM_SPEED - 0.1) / 9.9) {
            @Override protected void updateMessage() { setMessage(Text.literal("§7Aim Speed: §f" + String.format("%.1f", 0.1 + value * 9.9))); }
            @Override protected void applyValue() { RocketTriggerBot.AIM_SPEED = 0.1 + value * 9.9; }
        });

        // Слайдер радиуса
        this.addDrawableChild(new SliderWidget(cx - 105, startY + 88, 210, 18,
                Text.literal("§7Range: §f" + RocketTriggerBot.RANGE + "m"), (RocketTriggerBot.RANGE - 1) / 9) {
            @Override protected void updateMessage() { setMessage(Text.literal("§7Range: §f" + String.format("%.1f", 1 + value * 9) + "m")); }
            @Override protected void applyValue() { RocketTriggerBot.RANGE = 1 + value * 9; }
        });

        // Игнор мобов
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Ignore Mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "§c[ ON ]" : "§8[ OFF ]")),
                b -> {
                    RocketTriggerBot.IGNORE_MOBS = !RocketTriggerBot.IGNORE_MOBS;
                    b.setMessage(Text.literal("§7Ignore Mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "§c[ ON ]" : "§8[ OFF ]")));
                }
        ).dimensions(cx - 105, startY + 110, 210, 18).build());

        // Рандом задержки
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Random Delay: " + (RocketTriggerBot.RANDOM_DELAY ? "§c[ ON ]" : "§8[ OFF ]")),
                b -> {
                    RocketTriggerBot.RANDOM_DELAY = !RocketTriggerBot.RANDOM_DELAY;
                    b.setMessage(Text.literal("§7Random Delay: " + (RocketTriggerBot.RANDOM_DELAY ? "§c[ ON ]" : "§8[ OFF ]")));
                }
        ).dimensions(cx - 105, startY + 132, 210, 18).build());

        // Кнопка закрытия
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§c✕ Close"), b -> this.close()).dimensions(cx - 105, startY + 160, 210, 18).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Тёмный фон
        ctx.fill(0, 0, this.width, this.height, 0xCC000000); 
        
        // Отрисовка панели меню (красивая рамка)
        int panelWidth = 230;
        int panelHeight = 250;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2 - 10;

        // Задний фон панели
        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF141414);
        // Красная рамка
        ctx.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCC0000);
        
        // Заголовок
        ctx.drawCenteredTextWithShadow(this.textRenderer, "§l§cDARK TRIGGER", this.width / 2, panelY + 15, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "§8V1.0 PULSE", this.width / 2, panelY + 28, 0x888888);
        
        // Декоративная линия под заголовком
        ctx.fill(panelX + 20, panelY + 40, panelX + panelWidth - 20, panelY + 41, 0xFFCC0000);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}

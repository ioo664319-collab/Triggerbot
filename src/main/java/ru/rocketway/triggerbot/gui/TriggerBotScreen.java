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
        int y = this.height / 2 - 95;

        // 1. Триггербот
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7TriggerBot: " + (RocketTriggerBot.ENABLED ? "§a[ ON ]" : "§c[ OFF ]")),
                b -> {
                    RocketTriggerBot.ENABLED = !RocketTriggerBot.ENABLED;
                    b.setMessage(Text.literal("§7TriggerBot: " + (RocketTriggerBot.ENABLED ? "§a[ ON ]" : "§c[ OFF ]")));
                }
        ).dimensions(cx - 105, y, 210, 18).build());

        // 2. Aim Assist
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Aim Assist: " + (RocketTriggerBot.AIM_ASSIST ? "§a[ ON ]" : "§c[ OFF ]")),
                b -> {
                    RocketTriggerBot.AIM_ASSIST = !RocketTriggerBot.AIM_ASSIST;
                    b.setMessage(Text.literal("§7Aim Assist: " + (RocketTriggerBot.AIM_ASSIST ? "§a[ ON ]" : "§c[ OFF ]")));
                }
        ).dimensions(cx - 105, y + 22, 210, 18).build());

        // 3. CPS Ползунок (от 1 до 15 CPS)
        this.addDrawableChild(new SliderWidget(cx - 105, y + 44, 210, 18,
                Text.literal("§7CPS: §f" + String.format("%.1f", RocketTriggerBot.CPS)), (RocketTriggerBot.CPS - 1.0) / 14.0) {
            @Override protected void updateMessage() { setMessage(Text.literal("§7CPS: §f" + String.format("%.1f", 1.0 + value * 14.0))); }
            @Override protected void applyValue() { RocketTriggerBot.CPS = 1.0 + value * 14.0; }
        });

        // 4. Aim Speed
        this.addDrawableChild(new SliderWidget(cx - 105, y + 66, 210, 18,
                Text.literal("§7Aim Speed: §f" + String.format("%.1f", RocketTriggerBot.AIM_SPEED)), (RocketTriggerBot.AIM_SPEED - 0.1) / 9.9) {
            @Override protected void updateMessage() { setMessage(Text.literal("§7Aim Speed: §f" + String.format("%.1f", 0.1 + value * 9.9))); }
            @Override protected void applyValue() { RocketTriggerBot.AIM_SPEED = 0.1 + value * 9.9; }
        });

        // 5. Range
        this.addDrawableChild(new SliderWidget(cx - 105, y + 88, 210, 18,
                Text.literal("§7Range: §f" + String.format("%.1f", RocketTriggerBot.RANGE) + "m"), (RocketTriggerBot.RANGE - 1) / 9) {
            @Override protected void updateMessage() { setMessage(Text.literal("§7Range: §f" + String.format("%.1f", 1 + value * 9) + "m")); }
            @Override protected void applyValue() { RocketTriggerBot.RANGE = 1 + value * 9; }
        });

        // 6. Ignore Mobs
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Ignore Mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "§a[ ON ]" : "§c[ OFF ]")),
                b -> {
                    RocketTriggerBot.IGNORE_MOBS = !RocketTriggerBot.IGNORE_MOBS;
                    b.setMessage(Text.literal("§7Ignore Mobs: " + (RocketTriggerBot.IGNORE_MOBS ? "§a[ ON ]" : "§c[ OFF ]")));
                }
        ).dimensions(cx - 105, y + 110, 210, 18).build());

        // 7. Auto-Refill
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Auto-Refill: " + (RocketTriggerBot.AUTO_REFILL ? "§a[ ON ]" : "§c[ OFF ]")),
                b -> {
                    RocketTriggerBot.AUTO_REFILL = !RocketTriggerBot.AUTO_REFILL;
                    b.setMessage(Text.literal("§7Auto-Refill: " + (RocketTriggerBot.AUTO_REFILL ? "§a[ ON ]" : "§c[ OFF ]")));
                }
        ).dimensions(cx - 105, y + 132, 210, 18).build());

        // 8. Anti-AFK
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Anti-AFK: " + (RocketTriggerBot.ANTI_AFK ? "§a[ ON ]" : "§c[ OFF ]")),
                b -> {
                    RocketTriggerBot.ANTI_AFK = !RocketTriggerBot.ANTI_AFK;
                    b.setMessage(Text.literal("§7Anti-AFK: " + (RocketTriggerBot.ANTI_AFK ? "§a[ ON ]" : "§c[ OFF ]")));
                }
        ).dimensions(cx - 105, y + 154, 210, 18).build());

        // 9. Закрыть
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§c✕ Close"), b -> this.close()).dimensions(cx - 105, y + 180, 210, 18).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Отрисовка градиента на весь экран (от черного к темно-серому)
        ctx.fillGradient(0, 0, this.width, this.height, 0xDD000000, 0xDD1A1A1A);
        
        // Панель меню
        int panelWidth = 230;
        int panelHeight = 270;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2 - 10;

        // Градиентный фон панели
        ctx.fillGradient(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF141414, 0xFF2A2A2A);
        // Красная обводка
        ctx.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCC0000);
        
        // Заголовок
        ctx.drawCenteredTextWithShadow(this.textRenderer, "§l§cDARK TRIGGER", this.width / 2, panelY + 15, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "§8V2.0 GRADIENT", this.width / 2, panelY + 28, 0x888888);
        
        // Декоративная линия
        ctx.fill(panelX + 20, panelY + 40, panelX + panelWidth - 20, panelY + 41, 0xFFCC0000);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}

/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.test.chat;

import com.google.inject.Inject;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;
import org.spongepowered.test.LoadableModule;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

@Plugin("chattest")
public class ChatTest implements LoadableModule {
    private static final BossBar INFO_BAR = BossBar.bossBar(Component.translatable("chattest.bars.info"), 1f, BossBar.Color.PINK,
                                                      BossBar.Overlay.PROGRESS);

    private final Game game;
    private final PluginContainer container;
    private boolean barVisible;

    @Inject
    ChatTest(final Game game, final PluginContainer container) {
        this.game = game;
        this.container = container;
    }

    @Listener
    public void constructed(final ConstructPluginEvent event) {
        // Register localization keys
        final TranslationRegistry lang = TranslationRegistry.create(Key.key(this.container.getMetadata().getId(), "translations"));
        Arrays.asList(Locales.EN_US, new Locale("en", "UD")).forEach(it ->
                lang.registerAll(it, ResourceBundle.getBundle("org.spongepowered.test.chat.messages", it,
                                                              UTF8ResourceBundleControl.get()), false));
        GlobalTranslator.get().addSource(lang);
    }

    @Override
    public void enable(final CommandContext ctx) {
        this.game.getEventManager().registerListeners(this.container, new Listeners());
    }

    @Listener
    public void registerCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        // /togglebossbar
        event.register(this.container, Command.builder()
                .setPermission("chattest.togglebossbar")
                .setExecutor(ctx -> {
                    if (this.barVisible) {
                        this.game.getServer().hideBossBar(ChatTest.INFO_BAR);
                    } else {
                        this.game.getServer().showBossBar(ChatTest.INFO_BAR);
                    }
                    this.barVisible = !this.barVisible;
                    return CommandResult.success();
                })
                .build(), "togglebossbar");

        event.register(this.container, Command.builder()
                      .setPermission("chattest.sendbook")
                      .setExecutor(ctx -> {
                          ctx.getCause().getAudience().openBook(Book.builder()
                                                                        .title(Component.text("A story"))
                                                                        .author(Component.text("You"))
                                                                        .pages(Component.translatable("chattest.book.1"),
                                                                               Component.translatable("chattest.book.2")));
                          return CommandResult.success();
                      }).build(), "sendbook");
    }

    public class Listeners {

        @Listener(order = Order.LAST)
        public void onChat(final PlayerChatEvent event, final @Root ServerPlayer player) {
            ChatTest.this.game.getServer().sendMessage(Component.translatable("chattest.response.chat",
                                                                              event.getMessage(),
                                                                              player.require(Keys.DISPLAY_NAME)
                                                                                      .decorate(TextDecoration.BOLD)
                                                                                      .colorIfAbsent(NamedTextColor.AQUA))
                                                               .color(NamedTextColor.DARK_AQUA));
        }
    }
}

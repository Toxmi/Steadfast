package com.toxmi.steadfast.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.toxmi.steadfast.modules.customenchants.CustomManager;
import com.toxmi.steadfast.core.utils.ItemBuilder;
import com.toxmi.steadfast.core.utils.Keys;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCustomCommand extends BaseCommand {


    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("givecustom")
                .executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    player.sendMessage(Component.text("Invalid syntax").color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("custom", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            plugin.getCustomListener().getCustomsList().stream()
                                    .filter(s -> s.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            String custom = StringArgumentType.getString(ctx, "custom");
                            if (!plugin.getCustomListener().getCustomsList().contains(custom)) {
                                player.sendMessage(Component.text("Custom not found").color(NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }
                            ItemStack item = ItemBuilder.get().customItem(CustomManager.get().getMaterial(custom.toLowerCase()))
                                    .lore(custom)
                                    .pdcString(Keys.customKey, custom.toLowerCase())
                                    .build();
                            player.give(item);

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }
}

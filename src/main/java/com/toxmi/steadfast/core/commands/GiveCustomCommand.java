package com.toxmi.steadfast.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.toxmi.steadfast.core.utils.ItemBuilder;
import com.toxmi.steadfast.core.utils.Keys;
import com.toxmi.steadfast.modules.customenchants.CustomManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.toxmi.steadfast.core.utils.Str.cm;

public class GiveCustomCommand extends BaseCommand {


    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("givecustom")
                .executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    player.sendMessage(cm(mm.get("general.invalid-syntax")));
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
                                player.sendMessage(cm(mm.get("customs.not-found")));
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

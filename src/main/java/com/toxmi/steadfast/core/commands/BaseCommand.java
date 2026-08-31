package com.toxmi.steadfast.core.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.toxmi.steadfast.Steadfast;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class BaseCommand {
    protected final Steadfast plugin = Steadfast.get();


    public abstract LiteralCommandNode<CommandSourceStack> node();

    protected static Player requirePlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        if (source.getExecutor() instanceof Player p) return p;
        source.getSender().sendMessage("Players only.");
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();
    }

    protected OfflinePlayer getOfflinePlayer(PlayerProfile profile) {
        return plugin.getServer().getOfflinePlayer(profile.getId());
    }
}

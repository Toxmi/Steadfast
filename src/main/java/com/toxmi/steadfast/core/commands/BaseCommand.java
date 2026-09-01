package com.toxmi.steadfast.core.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.managers.MessageManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public abstract class BaseCommand {
    protected final Steadfast plugin = Steadfast.get();
    protected MessageManager mm = MessageManager.get();


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
    protected boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}

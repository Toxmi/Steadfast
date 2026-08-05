package com.toxmi.steadfast.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.toxmi.steadfast.Steadfast;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

public abstract class BaseCommand {
    protected final Steadfast plugin = Steadfast.get();


    public abstract LiteralCommandNode<CommandSourceStack> node();

    protected static Player requirePlayer(CommandSourceStack source) throws CommandSyntaxException {
        if (source.getExecutor() instanceof Player p) return p;
        source.getSender().sendMessage("Players only.");
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();
    }
}

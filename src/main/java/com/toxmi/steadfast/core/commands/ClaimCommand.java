package com.toxmi.steadfast.core.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.toxmi.steadfast.core.utils.ItemBuilder;
import com.toxmi.steadfast.core.utils.Keys;
import com.toxmi.steadfast.modules.claims.Claim;
import com.toxmi.steadfast.modules.claims.ClaimManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

import static com.toxmi.steadfast.core.utils.Str.cc;
import static com.toxmi.steadfast.core.utils.Str.cm;

public class ClaimCommand extends BaseCommand {

    private final ClaimManager claimManager = ClaimManager.get();
    private final ItemBuilder ib = ItemBuilder.get();

    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("claim")
                .executes(ctx -> {
                    Player player = requirePlayer(ctx);
                    handleHelp(player, 1);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("ally")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim ally <teamName></White>"));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("teamname", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    String teamName = StringArgumentType.getString(ctx, "teamname");
                                    Claim claim = claimManager.getClaim(teamName);
                                    if (claim == null) {
                                        player.sendMessage(cm(String.format("<Gray>▪ </Gray><Red>Team by name or player %s not found", teamName)));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    // TODO - ALLIANCES IMPLEMENTATION
                                    player.sendMessage(cm(String.format("<Gray>▪ </Gray><White>Ally request sent to <#D22B2B>%s</#D22B2B>",teamName)));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("allychat")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't use this command!"));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("bed")
                        .executes(ctx -> { // TODO - BED MENU AND METHODS
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't use this command!"));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("buychunk")
                        .executes(ctx -> { // TODO - BUYCHUNK METHODS
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't use this command!"));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("chat")
                        .executes(ctx -> { // TODO - CLAIM CHAT IMPLEMENTATION
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't use this command!"));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("message", StringArgumentType.string())
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't use this command!"));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )

                )
                .then(Commands.literal("create")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            handleCreate(player);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("deletechunk")
                        .executes(ctx -> { // TODO - DELETECHUNK METHODS
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't use this command!"));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("demote")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim demote <player></White>"));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    final PlayerProfileListResolver profilesResolver = ctx.getArgument("player", PlayerProfileListResolver.class);
                                    PlayerProfile profile = (PlayerProfile) profilesResolver.resolve(ctx.getSource()).toArray()[0];
                                    if (profile == null) {
                                        player.sendMessage(cm(String.format("<Gray>▪ </Gray><White>Player <Red>%s</Red> not found!",ctx.getArgument("player", String.class))));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).build();
    }


    private final List<Component> helpComponents = List.of(
            cm("""
                    <br>
                    <#D22B2B><b>Claim Usage </b></#D22B2B><Gray>(Page 1 out of 3)</Gray><br>
                    <#D22B2B> | </#D22B2B><White>/claim create</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim disband</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim rename <name></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim invite <player></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim uninvite <player></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim join <player></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim leave</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim kick <player></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim leader <player></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim promote <player></White><br>
                    <Gray><st>           </st><click:run_command:/claim help 2><Green>Next [›]</Green></click><st>           <br>
                    """),
            cm("""
                    <br>
                    <#D22B2B><b>Claim Usage </b></#D22B2B><Gray>(Page 2 out of 3)</Gray><br>
                    <#D22B2B> | </#D22B2B><White>/claim demote <player></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim buychunk</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim deletechunk</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim info [teamName]</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim permissions</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim chat [message]</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim allychat [message]</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim teleport</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim findchest</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim ally <teamName></White><br>
                    <Gray><st>           </st><click:run_command:/claim help 1><Red>[‹] Back</Red></click><st>     </st><Yellow>2</Yellow><st>     </st><click:run_command:/claim help 3><Green>Next [›]</Green></click><st>           <br>
                    
                    """),
            cm("""
                    <br>
                    <#D22B2B><b>Claim Usage </b></#D22B2B><Gray>(Page 3 out of 3)</Gray><br>
                    <#D22B2B> | </#D22B2B><White>/claim unally</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim focus <player></White><br>
                    <#D22B2B> | </#D22B2B><White>/claim unfocus</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim top</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim bed</White><br>
                    <#D22B2B> | </#D22B2B><White>/claim help [page]</White><br>
                    <Gray><st>           </st><click:run_command:/claim help 2><Red>[‹] Back</Red></click><st>           <br>
                    """)
    );

    private void handleHelp(Player player, int page) {
        player.sendMessage(helpComponents.get(page - 1));
    }

    private void handleCreate(Player player) {
        ItemStack claimChest = ib.customItem(Material.CHEST)
                .displayName(cm("<Yellow><b>Claim Chest</b></Yellow>"))
                .lore(cm("""
                        <Gray>This is a unique item that you <br>
                        can use to protect and manage <br>
                        your claimed land.</Gray>
                        """
                ))
                .pdcString(Keys.itemKey, "claimchest")
                .build();
        if (player.give(claimChest).drops().isEmpty()) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have enough inventory space!"));
            return;
        }
        player.sendMessage(cm("<Gray>▪ </Gray><White>To <#D22B2B>Claim Land </#D22B2B>you need to place a <#D22B2B>Claim Chest </#D22B2B> in the chunk you want to claim."));
        player.sendMessage(cm("<Gray>▪ </Gray><White>Place it somewhere safe, if it is <#D22B2B><b>DESTROYED</b></#D22B2B>, your claim is <#D22B2B><b>LOST.</b></#D22B2B>"));
    }




}

package com.toxmi.steadfast.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.toxmi.steadfast.core.utils.ItemBuilder;
import com.toxmi.steadfast.core.utils.Keys;
import com.toxmi.steadfast.modules.claims.Claim;
import com.toxmi.steadfast.modules.claims.ClaimManager;
import com.toxmi.steadfast.modules.claims.enums.ClaimRole;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

import static com.toxmi.steadfast.core.utils.Str.cc;
import static com.toxmi.steadfast.core.utils.Str.cm;

public class ClaimCommand extends BaseCommand {

    private final ClaimManager claimManager = ClaimManager.get();
    private final ItemBuilder ib = ItemBuilder.get();
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
                                    player.sendMessage(cm(String.format("<Gray>▪ </Gray><White>Ally request sent to <#D22B2B>%s</#D22B2B>", teamName)));
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
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    OfflinePlayer target = plugin.getServer().getOfflinePlayer(StringArgumentType.getString(ctx, "player"));
                                    if (!target.hasPlayedBefore()) {
                                        player.sendMessage(cm(String.format("<Gray>▪ </Gray><White>Player <Red>%s</Red> not found!", ctx.getArgument("player", String.class))));
                                    }
                                    handleDemote(player, target);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )

                )
                .then(Commands.literal("disband")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            handleDisband(player);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("findchest")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            handleFindChest(player);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("focus")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            // TODO - TEAM FOCUS IMPLEMENTATION
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("help")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            handleHelp(player, 1);
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("page", IntegerArgumentType.integer(1, 3))
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    handleHelp(player, IntegerArgumentType.getInteger(ctx, "page"));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("home")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            // TODO - HOMES IMPLEMENTATION
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("info")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            handleInfo(player);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("invite")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim invite <player></White>"));
                            return Command.SINGLE_SUCCESS;
                        }).then(Commands.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    plugin.getServer().getOnlinePlayers().stream()
                                            .filter(p -> !isVanished(p) && p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(e -> builder.suggest(e.getName()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    String target = StringArgumentType.getString(ctx, "player");
                                    OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(target);
                                    if (!targetPlayer.hasPlayedBefore()) {
                                        player.sendMessage(cm("<Gray>▪ </Gray><White>Player <Red><target></Red> not found!", Placeholder.component("target", cc(target))));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    handleInvite(player, targetPlayer);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("join")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim join <teamName></White>"));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("teamname", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    handleJoin(player, StringArgumentType.getString(ctx, "teamname"));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("kick")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim kick <player></White>"));
                            return Command.SINGLE_SUCCESS;
                        }).then(Commands.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    plugin.getServer().getOnlinePlayers().stream()
                                            .filter(p -> !isVanished(p) && p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(e -> builder.suggest(e.getName()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    String target = StringArgumentType.getString(ctx, "player");
                                    OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(target);
                                    if (!targetPlayer.hasPlayedBefore()) {
                                        player.sendMessage(cm("<Gray>▪ </Gray><White>Player <Red><target></Red> not found!", Placeholder.component("target", cc(target))));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    handleKick(player, targetPlayer);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("leader")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim leader <player></White>"));
                            return Command.SINGLE_SUCCESS;
                        }).then(Commands.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    plugin.getServer().getOnlinePlayers().stream()
                                            .filter(p -> !isVanished(p) && p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(e -> builder.suggest(e.getName()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    String target = StringArgumentType.getString(ctx, "player");
                                    OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(target);
                                    if (!targetPlayer.hasPlayedBefore()) {
                                        player.sendMessage(cm("<Gray>▪ </Gray><White>Player <Red><target></Red> not found!", Placeholder.component("target", cc(target))));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    handleLeader(player, targetPlayer);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("leave")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            handleLeave(player);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("permissions")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("promote")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim promote <player></White>"));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    OfflinePlayer target = plugin.getServer().getOfflinePlayer(StringArgumentType.getString(ctx, "player"));
                                    if (!target.hasPlayedBefore()) {
                                        player.sendMessage(cm(String.format("<Gray>▪ </Gray><White>Player <Red>%s</Red> not found!", ctx.getArgument("player", String.class))));
                                    }
                                    handlePromote(player, target);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("rename")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim rename <name></White>"));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    handleRename(player, StringArgumentType.getString(ctx, "name"));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("teleport")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            // TODO - HOMES IMPLEMENTATION
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("top")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            // TODO - TEAM TOP IMPLEMENTATION
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("unally")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            // TODO - ALLIANCES IMPLEMENTATION
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("unfocus")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            // TODO - TEAM FOCUS IMPLEMENTATION
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("uninvite")
                        .executes(ctx -> {
                            Player player = requirePlayer(ctx);
                            player.sendMessage(cm("<Gray>▪ </Gray><#D22B2B>Usage: </#D22B2B><White>/claim uninvite <player></White>"));
                            return Command.SINGLE_SUCCESS;
                        }).then(Commands.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    plugin.getServer().getOnlinePlayers().stream()
                                            .filter(p -> !isVanished(p) && p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(e -> builder.suggest(e.getName()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    Player player = requirePlayer(ctx);
                                    String target = StringArgumentType.getString(ctx, "player");
                                    OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(target);
                                    if (!targetPlayer.hasPlayedBefore()) {
                                        player.sendMessage(cm("<Gray>▪ </Gray><White>Player <Red><target></Red> not found!", Placeholder.component("target", cc(target))));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    handleUnInvite(player, targetPlayer);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }

    private void handleHelp(@NonNull Player player, int page) {
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

    private void handleDemote(Player demoter, OfflinePlayer target) {
        Claim claim = claimManager.getClaim(demoter);
        if (claim == null) {
            demoter.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (!claim.isMember(target.getUniqueId())) {
            demoter.sendMessage(cm("<Gray>▪ </Gray><Red>This player is not in your claim!"));
            return;
        }
        if (target.getUniqueId() == demoter.getUniqueId()) {
            demoter.sendMessage(cm("<Gray>▪ </Gray><Red>You cannot demote yourself"));
            return;
        }
        if (claim.getRole(demoter).getPermission() > 3) {
            demoter.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to demote this player!"));
            return;
        }
        ClaimRole role = claim.getRole(target);
        if (role.getPermission() <= claim.getRole(demoter).getPermission()) {
            demoter.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to demote this player!"));
            return;
        }
        if (role.getPermission() == 5) {
            demoter.sendMessage(cm("<Gray>▪ </Gray><Red>This player is already a Limited Member"));
            return;
        }

        claim.changeRole(target.getUniqueId(), role.next());

        for (UUID member : claim.getMembers()) {
            Player p = plugin.getServer().getPlayer(member);
            if (p != null && p != demoter && p.getUniqueId() != target.getUniqueId()) {
                p.sendMessage(cm(
                        "<Gray>▪ </Gray><Red><target> was demoted to <role> by <demoter>!",
                        Placeholder.component("target", cc(target.getName())),
                        Placeholder.component("role", cc(role.next().getDisplayName())),
                        Placeholder.component("demoter", cc(demoter.getName()))
                ));
            }
        }
        demoter.sendMessage(cm(
                "<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> was demoted to <#D22B2B><role></#D22B2B>",
                Placeholder.component("target", cc(target.getName())),
                Placeholder.component("role", cc(role.next().getDisplayName()))
        ));

        if (target.getPlayer() != null) {
            target.getPlayer().sendMessage(cm(
                    "<Gray>▪ </Gray><White>You were demoted to <#D22B2B><role></#D22B2B> by <#D22B2B><demoter></#D22B2B>",
                    Placeholder.component("demoter", demoter.name()),
                    Placeholder.component("role", cc(role.next().getDisplayName()))
            ));
        }
    }

    private void handlePromote(Player promoter, OfflinePlayer target) {
        Claim claim = claimManager.getClaim(promoter);
        if (claim == null) {
            promoter.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (!claim.isMember(target.getUniqueId())) {
            promoter.sendMessage(cm("<Gray>▪ </Gray><Red>This player is not in your claim!"));
            return;
        }
        if (target.getUniqueId() == promoter.getUniqueId()) {
            promoter.sendMessage(cm("<Gray>▪ </Gray><Red>You cannot promote yourself"));
            return;
        }
        if (claim.getRole(promoter).getPermission() >= 3) {
            promoter.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to promote this player!"));
            return;
        }
        ClaimRole role = claim.getRole(target);
        if (role.getPermission() <= claim.getRole(promoter).getPermission()) {
            promoter.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to promote this player!"));
            return;
        }
        if (role.getPermission() == 2) {
            promoter.sendMessage(cm("<Gray>▪ </Gray><Red>Cannot promote this player to Leader. Use <White>/claim leader <player></White>"));
            return;
        }

        claim.changeRole(target.getUniqueId(), role.previous());

        for (UUID member : claim.getMembers()) {
            Player p = plugin.getServer().getPlayer(member);
            if (p != null && p != promoter && p.getUniqueId() != target.getUniqueId()) {
                p.sendMessage(cm(
                        "<Gray>▪ </Gray><Red><target> was promoted to <role> by <promoter>!",
                        Placeholder.component("target", cc(target.getName())),
                        Placeholder.component("role", cc(role.previous().getDisplayName())),
                        Placeholder.component("promoter", cc(promoter.getName()))
                ));
            }
        }

        promoter.sendMessage(cm(
                "<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> was promoted to <#D22B2B><role></#D22B2B>",
                Placeholder.component("target", cc(target.getName())),
                Placeholder.component("role", cc(role.previous().getDisplayName()))
        ));

        if (target.getPlayer() != null) {
            target.getPlayer().sendMessage(cm(
                    "<Gray>▪ </Gray><White>You were promoted to <#D22B2B><role></#D22B2B> by <#D22B2B><promoter></#D22B2B>",
                    Placeholder.component("promoter", promoter.name()),
                    Placeholder.component("role", cc(role.previous().getDisplayName()))
            ));
        }
    }

    private void handleDisband(Player player) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }

        if (!claim.getRole(player).equals(ClaimRole.OWNER)) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to disband this claim!"));
            return;
        }

        if (plugin.isFloodGatePlayer(player)) {
            CustomForm form = CustomForm.builder()
                    .title("Disband claim?")
                    .label("By clicking confirm your claim will be disbanded")
                    .validResultHandler(response -> claimManager.disbandClaim(claim))
                    .closedOrInvalidResultHandler(() -> {
                    })
                    .build();
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        } else {
            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(cc("Disband Claim").color(NamedTextColor.RED))
                            .body(List.of(
                                    DialogBody.plainMessage(cm("<Gray>Are you sure you want to disband this claim?"))
                            )).build()

                    )
                    .type(DialogType.confirmation(
                            ActionButton.create(
                                    cc("Confirm"),
                                    cc("Click to confirm"),
                                    100,
                                    DialogAction.customClick((view, audience) -> {
                                                claimManager.disbandClaim(claim);

                                            }, ClickCallback.Options.builder()
                                                    .uses(1)
                                                    .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                                    .build()
                                    )
                            ),
                            ActionButton.create(
                                    Component.text("Cancel"),
                                    Component.text("Click to cancel"),
                                    100,
                                    DialogAction.customClick((view, audience) -> {
                                            },
                                            ClickCallback.Options.builder()
                                                    .uses(1)
                                                    .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                                    .build()
                                    )
                            )
                    ))
            );
            player.showDialog(dialog);
        }
    }

    private void handleFindChest(Player player) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        Location loc = claim.getClaimChestLoc();
        player.sendMessage(cm(
                "<Gray>▪ </Gray><White>Your Claim is at <#D22B2B><xcoord>x</#D22B2B>, <#D22B2B><ycoord>y</#D22B2B>, <#D22B2B><zcoord>z</#D22B2B>",
                Placeholder.component("xcoord", cc(loc.getBlockX())),
                Placeholder.component("ycoord", cc(loc.getBlockY())),
                Placeholder.component("zcoord", cc(loc.getBlockZ()))
        ));
    }

    private void handleInfo(Player player) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        Location loc = claim.getClaimChestLoc();
        player.sendMessage(cm(
                """
                        <br>
                        <Green><b><claimname></b></Green><br>
                        <br>
                        <Yellow><b>Info:</b></Yellow><br>
                        <Gray>▪ </Gray><White>Claim Chest: <Yellow><xcoord><Gray>, </Gray><ycoord><Gray>, </Gray><zcoord></Yellow></White><br>
                        <Gray>▪ </Gray><White>Claims: </White><Yellow><claimcount></Yellow><br>
                        <Gray>▪ </Gray><White>Power: </White><Yellow><power></Yellow><Dark_Gray>{#<rank>}</Dark_Gray><br>
                        <br>
                        <Dark_green><b>Members:</b></Dark_green><Gray> [<membercount>/<maxmembers>]</Gray><br>
                        <Gray>▪ </Gray><Gold>Leaders</Gold><Gray>:</Gray> <claimowner><br>
                        <Gray>▪ </Gray><Yellow>Co-Leaders</Yellow><Gray>:</Gray> <claimcoleaders><br>
                        <Gray>▪ </Gray><Aqua>Officers</Aqua><Gray>:</Gray> <claimofficers><br>
                        <Gray>▪ </Gray><Green>Members</Green><Gray>:</Gray> <claimmembers><br>
                        <Gray>▪ Limited-Members:</Gray> <claimlimitedmembers><br>
                        <br>
                        """,
                Placeholder.component("claimname", cc(claim.getClaimName())),
                Placeholder.component("xcoord", cc(loc.getBlockX())),
                Placeholder.component("ycoord", cc(loc.getBlockY())),
                Placeholder.component("zcoord", cc(loc.getBlockZ())),
                Placeholder.component("claimcount", cc(claim.getChunkCount())),
                Placeholder.component("power", cc(claim.getPower())),
                Placeholder.component("rank", cc(claimManager.getClaimRank(claim))),
                Placeholder.component("membercount", cc(claim.getMembers().size())),
                Placeholder.component("maxmembers", cc(claimManager.getMaxMembers())),
                Placeholder.component("claimowner", cc(getMemberString(claim.getOwner()))),
                Placeholder.component("claimcoleaders", cc(getMemberTypeString(claim, ClaimRole.CO_LEADER))),
                Placeholder.component("claimofficers", cc(getMemberTypeString(claim, ClaimRole.OFFICER))),
                Placeholder.component("claimmembers", cc(getMemberTypeString(claim, ClaimRole.MEMBER))),
                Placeholder.component("claimlimitedmembers", cc(getMemberTypeString(claim, ClaimRole.LIMITED_MEMBER)))
        ));
    }

    private String getMemberString(UUID uuid) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
        if (player.isOnline()) {
            return String.format("<Green>%s</Green>", player.getName());
        } else {
            return String.format("<Gray>%s</Gray>", player.getName());
        }
    }

    private String getMemberTypeString(Claim claim, ClaimRole role) {
        List<UUID> members = claim.getMembersByRole(role);
        if (members.isEmpty()) return "<Gray>None</Gray>";
        return String.join(", ", members.stream().map(this::getMemberString).toList());
    }

    private void handleInvite(Player inviter, OfflinePlayer target) {
        Claim claim = claimManager.getClaim(inviter);
        if (claim == null) {
            inviter.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (claim.isMember(target.getUniqueId())) {
            inviter.sendMessage(cm("<Gray>▪ </Gray><Red><name> is already in your team", Placeholder.component("name", cc(target.getName()))));
            return;
        }
        if (claim.getRole(inviter).getPermission() > 3) {
            inviter.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to invite players"));
            return;
        }
        if (claim.getInvites().asMap().containsKey(target.getUniqueId())) {
            inviter.sendMessage(cm("<Gray>▪ </Gray><Red><name> has already been invited", Placeholder.component("name", cc(target.getName()))));
            return;
        }
        if (claim.getMembers().size() + claim.getInvites().size() >= claimManager.getMaxMembers()) {
            inviter.sendMessage(cm("<Gray>▪ </Gray><Red>Your team is full!"));
            return;
        }

        claim.addInvite(target.getUniqueId());
        claim.sendMessageToAll(cm("<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> has been invited to <Yellow>Claim</Yellow>.</White>", Placeholder.component("target", cc(target.getName()))));
        if (target.isOnline()) {
            ((Player) target).sendMessage(cm(
                    """
                            <br>
                            <White>You have been invited to join <Yellow><claimname></Yellow></White><br>
                            <Yellow><click:run_command /claim join <claimname>>[Click here to accept]</click></Yellow><br>
                            <br>
                            """,
                    Placeholder.component("claimname", cc(claim.getClaimName()))
            ));
        }
    }

    private void handleUnInvite(Player player, OfflinePlayer target) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (!claim.getInvites().asMap().containsKey(target.getUniqueId())) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red><name> is not invited.", Placeholder.component("name", cc(target.getName()))));
            return;
        }
        if (claim.getRole(player).getPermission() > 3) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to uninvite players"));
            return;
        }

        claim.removeInvite(target.getUniqueId());
        player.sendMessage(cm("<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> has been un-invited from the <#D22B2B>Claim Team</#D22B2B>.", Placeholder.component("target", cc(target.getName()))));
    }

    private void handleJoin(Player player, String target) {
        Claim claim = claimManager.getClaim(player);
        if (claim != null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You're already in a Team."));
            return;
        }
        Claim targetClaim = claimManager.getClaim(target);
        if (targetClaim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>Cannot find Claim <#D22B2B><claimname></#D22B2B>.", Placeholder.component("claimname", cc(target))));
            return;
        }
        if (!targetClaim.getInvites().asMap().containsKey(player.getUniqueId())) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You have not been invited to join this team"));
            return;
        }

        targetClaim.removeInvite(player.getUniqueId());
        targetClaim.addMember(player.getUniqueId());
        targetClaim.sendMessageToAll(cm("<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> has joined the team", Placeholder.component("target", cc(player.getName()))));
    }

    private void handleKick(Player player, OfflinePlayer target) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (!claim.isMember(target.getUniqueId())) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>This player is not in your claim!"));
            return;
        }
        if (target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You cannot kick yourself"));
            return;
        }
        if (claim.getRole(player).getPermission() > 3) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to kick this player!"));
            return;
        }
        ClaimRole role = claim.getRole(target);
        if (role.getPermission() <= claim.getRole(player).getPermission()) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to kick this player!"));
            return;
        }

        claim.removeMember(target.getUniqueId());
        claim.sendMessageToAll(cm("<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> has been kicked from the team", Placeholder.component("target", cc(target.getName()))));
        if (target.isOnline()) {
            ((Player) target).sendMessage(cm("<Gray>▪ </Gray><White>You have been kicked from <#D22B2B><claimname></#D22B2B>", Placeholder.component("claimname", cc(claim.getClaimName()))));
        }
    }

    private void handleLeader(Player player, OfflinePlayer target) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (!claim.isMember(target.getUniqueId())) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>This player is not in your claim!"));
            return;
        }
        if (claim.getRole(player).equals(ClaimRole.OWNER)) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>Only the Claim Leader can change the Leader!"));
            return;
        }
        if (target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't transfer leadership to yourself"));
            return;
        }
        claim.changeRole(player.getUniqueId(), ClaimRole.CO_LEADER);
        claim.changeRole(target.getUniqueId(), ClaimRole.OWNER);
        claim.sendMessageToAll(cm("<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> is now the Claim Leader", Placeholder.component("target", cc(target.getName()))));
    }

    private void handleLeave(Player player) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (claim.getRole(player).equals(ClaimRole.OWNER)) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You can't leave as <Gold>Leader</Gold>, you must either transfer the ownership or disband the claim."));
            return;
        }
        claim.removeMember(player);
        player.sendMessage(cm("<Gray>▪ </Gray><White>You have left <#D22B2B><claimname></#D22B2B>", Placeholder.component("claimname", cc(claim.getClaimName()))));
        claim.sendMessageToAll(cm("<Gray>▪ </Gray><White><#D22B2B><target></#D22B2B> has left the team.", Placeholder.component("target", cc(player.getName()))));
    }

    private void handleRename(Player player, String name) {
        Claim claim = claimManager.getClaim(player);
        if (claim == null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You are not in a Claim!"));
            return;
        }
        if (claim.getRole(player).getPermission() > 2) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red>You do not have permission to rename the claim"));
            return;
        }
        if (claimManager.getClaim(name) != null) {
            player.sendMessage(cm("<Gray>▪ </Gray><Red><claimname> is already in use", Placeholder.component("claimname", cc(name))));
            return;
        }
        name = name.replace("&", "");
        claim.setClaimName(name);
        claim.sendMessageToAll(cm("<Gray>▪ </Gray><White>Changed <#D22B2B>Claim</#D22B2B> name to <#D22B2B><claimname></#D22B2B>", Placeholder.component("claimname", cc(name))));
    }
}

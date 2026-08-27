package com.toxmi.steadfast.modules.claims.menus;

import com.toxmi.steadfast.core.menu.Button;
import com.toxmi.steadfast.core.menu.Menu;
import com.toxmi.steadfast.core.utils.TimeFormatter;
import com.toxmi.steadfast.modules.claims.Claim;
import com.toxmi.steadfast.modules.claims.ClaimManager;
import com.toxmi.steadfast.modules.claims.enums.Artifact;
import com.toxmi.steadfast.modules.claims.enums.ClaimRole;
import com.toxmi.steadfast.modules.claims.enums.PowerSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.toxmi.steadfast.core.utils.Str.cc;
import static com.toxmi.steadfast.core.utils.Str.cm;

public class ClaimMenu extends Menu {

    private final Claim claim;
    private final ClaimManager claimManager = ClaimManager.get();

    public ClaimMenu(Claim claim) {
        this.claim = claim;
    }

    @Override
    public void onOpen(@Nullable Player player) {
        fill();

        setButton(11, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return ib.customItem(Material.SHIELD)
                        .displayName(cc("Manage Shield").color(NamedTextColor.AQUA))
                        .lore(
                                cm(
                                        "<!i><br>" +
                                                "<White>Info:<br>" +
                                                "<Gray>▪ </Gray>Current Charge: <Aqua><time></Aqua> <br>" +
                                                "<Gray>▪ </Gray>Mode: <Aqua><mode></Aqua> <br>" +
                                                "<Gray>▪ </Gray>Status: <color:<statuscolor>><status><br>" +
                                                "<br>" +
                                                "<Aqua>➡ Click to manage your shield",
                                        Placeholder.component("time", cc(TimeFormatter.getFormattedTime(claim.getShieldCharge()))),
                                        Placeholder.component("mode", cc(claim.getShieldMode().toString())),
                                        Placeholder.component("statuscolor", cc(claim.getShieldState().getColor())),
                                        Placeholder.component("status", cc(claim.getShieldState().toString()))
                                )
                        ).build();
            }
        });


        setButton(13, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return ib.customItem(Material.HOPPER)
                        .displayName(cc("Manage Claim").color(NamedTextColor.YELLOW))
                        .lore(
                                cm(
                                        "<!i><br>" +
                                                "<White>General:<br>" +
                                                "<Gray>▪ </Gray>Claims: <Green><claimcount>/49</Green><br>" +
                                                "<br>" +
                                                "Artifacts:<br>" +
                                                "<Gray>▪ </Gray>Slot 1: " + (claim.getArtifacts().get(1) == null ? "<Red>Locked</Red>" : getArtifactString(claim.getArtifacts().get(1))) + "<br>" +
                                                "<Gray>▪ </Gray>Slot 2: " + (claim.getArtifacts().get(2) == null ? "<Red>Locked</Red>" : getArtifactString(claim.getArtifacts().get(2))) + "<br>" +
                                                "<Gray>▪ </Gray>Slot 3: " + (claim.getArtifacts().get(3) == null ? "<Red>Locked</Red>" : getArtifactString(claim.getArtifacts().get(3))) + "<br>" +
                                                "<br>" +
                                                String.format("Power: <Dark_red>%s</Dark_red> <Dark_gray>(#%s)</Dark_gray> <br>", claim.getPower(), claimManager.getClaimRank(claim)) +
                                                String.format("<Gray>▪ </Gray>Spawners: <Dark_red>+%s</Dark_red><br>", claim.getPowerFromASource(PowerSource.SPAWNER)) +
                                                String.format("<Gray>▪ </Gray>Artifacts: <Dark_red>+%s</Dark_red><br>", claim.getPowerFromASource(PowerSource.ARTIFACT)) +
                                                String.format("<Gray>▪ </Gray>Wealth: <Dark_red>+%s</Dark_red><br>", claim.getPowerFromASource(PowerSource.WEALTH)) +
                                                "<br>" +
                                                "<Yellow>➡ Click to manage your team",
                                        Placeholder.component("claimcount", cc(claim.getChunkCount()))
                                )
                        )
                        .build();
            }

            @Override
            public void onButtonClick(Player player, ClickType clickType, InventoryClickEvent event) {

            }
        });


        List<String> members = getMembersByRole(ClaimRole.MEMBER);
        List<String> coLeaders = getMembersByRole(ClaimRole.CO_LEADER);
        List<String> officers = getMembersByRole(ClaimRole.OFFICER);
        List<String> limitedMembers = getMembersByRole(ClaimRole.LIMITED_MEMBER);

        setButton(15, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                var ref = new Object() {
                    String lore = "<!i><br>" +
                            String.format("<White>Members: <Gray>(%s/14)</Gray><br>", claim.getMembers().size()) +
                            String.format("<Gray>▪ <Gold>Leader</Gold>: </Gray>%s<br>", plugin.getServer().getOfflinePlayer(claim.getOwner()).getName()) +
                            String.format("<Gray>▪ <Yellow>Co-Leaders</Yellow>: </Gray>%s<br>", coLeaders.isEmpty() ? "<Red>None</Red>" : String.join(", ", coLeaders)) +
                            String.format("<Gray>▪ <Aqua>Officers</Aqua>: </Gray>%s<br>", officers.isEmpty() ? "<Red>None</Red>" : String.join(", ", officers)) +
                            String.format("<Gray>▪ <Green>Members</Green>: </Gray>%s<br>", members.isEmpty() ? "<Red>None</Red>" : String.join(", ", members)) +
                            String.format("<Gray>▪ Limited-Members: </Gray>%s<br>", limitedMembers.isEmpty() ? "<Red>None</Red>" : String.join(", ", limitedMembers)) +
                            "<br>" +
                            String.format("Invited: <Gray>(%s Invites Left)</Gray><br>", 14 - claim.getMembers().size() - claim.getInvites().entrySet().stream().filter(entry -> {
                                return entry.getValue() + 60000L > System.currentTimeMillis();
                            }).count());
                };
                claim.getInvites().entrySet().stream().filter(entry -> entry.getValue() + 60000L < System.currentTimeMillis()).forEach(entry -> {
                    ref.lore = ref.lore + "<Gray>▪ </Gray> " + plugin.getServer().getOfflinePlayer(entry.getKey()).getName() + "<br>";
                });
                ref.lore = ref.lore + "<Green>➡ Click to manage your members</Green>";
                return ib.customItem(Material.BOOK)
                        .displayName(cm("<Green>Manage Members</Green>"))
                        .lore(cm(ref.lore)
                        ).build();
            }
        });
    }

    @Override
    public Component getTitle(@Nullable Player player) {
        return cc("Manage " + claim.getClaimName());
    }

    @Override
    public int getSize(@Nullable Player player) {
        return 45;
    }

    private String getArtifactString(Artifact artifact) {
        return "<Gold>" + artifact + "</Gold>";
    }

    private List<String> getMembersByRole(ClaimRole role) {
        List<String> members = new ArrayList<>();
        for (Map.Entry<UUID, ClaimRole> entry : claim.getMembersMap().entrySet()) {
            if (entry.getValue() == role) members.add(plugin.getServer().getOfflinePlayer(entry.getKey()).getName());
        }
        return members;
    }
}

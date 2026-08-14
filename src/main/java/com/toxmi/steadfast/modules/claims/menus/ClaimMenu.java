package com.toxmi.steadfast.modules.claims.menus;

import com.toxmi.steadfast.core.menu.Button;
import com.toxmi.steadfast.core.menu.Menu;
import com.toxmi.steadfast.core.utils.TimeFormatter;
import com.toxmi.steadfast.modules.claims.Claim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClaimMenu extends Menu {

    private final Claim claim;

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
                        .displayName(Component.text("Manage Shield").color(NamedTextColor.AQUA))
                        .lore(
                                MiniMessage.miniMessage().deserialize(
                                        "<!i><br>" +
                                                "<White>Info:<br>" +
                                                "<Gray>▪ </Gray>Current Charge: <Aqua><time></Aqua> <br>" +
                                                "<Gray>▪ </Gray>Mode: <Aqua><mode></Aqua> <br>" +
                                                "<Gray>▪ </Gray>Status: <color:<statuscolor>><status><br>" +
                                                "<br>" +
                                                "<Aqua>➡ Click to manage your shield",
                                        Placeholder.component("time", Component.text(TimeFormatter.getFormattedTime(claim.getShieldCharge()))),
                                        Placeholder.component("mode", Component.text(claim.getShieldMode().toString())),
                                        Placeholder.component("statuscolor", Component.text(claim.getShieldState().getColor())),
                                        Placeholder.component("status", Component.text(claim.getShieldState().toString()))
                                )
                        ).build();
            }
        });
    }

    @Override
    public Component getTitle(@Nullable Player player) {
        return Component.text("Manage " + claim.getClaimName());
    }

    @Override
    public int getSize(@Nullable Player player) {
        return 45;
    }
}

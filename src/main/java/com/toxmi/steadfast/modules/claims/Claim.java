package com.toxmi.steadfast.modules.claims;

import com.toxmi.steadfast.Steadfast;
import org.bukkit.Location;

import java.util.*;

public class Claim {
    private final Steadfast plugin;
    private UUID claimID;
    private String claimName;
    private Location claimChestLoc;

    private final Map<UUID, ClaimRole> MEMBERS = new HashMap<>();
    private final Set<String> CHUNKS = new HashSet<>();
    private final Map<UUID, Long> INVITES = new HashMap<>();

    private boolean inCombat = false;
    private long combatDuration = 0L;
    private double claimChestHealth = 100;
    private double claimChestMaxHealth = 100;

    private double power = 0.0;


    public Claim(UUID claimID) {
        this.plugin = Steadfast.get();
    }
}

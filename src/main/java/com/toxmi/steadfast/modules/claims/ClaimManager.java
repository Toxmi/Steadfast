package com.toxmi.steadfast.modules.claims;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimManager {
    private static ClaimManager instance;
    private final Map<UUID, Claim> claims = new ConcurrentHashMap<>();


    public ClaimManager() {
        instance = this;
    }

    public synchronized static ClaimManager get() {
        if (instance == null) {
            instance = new ClaimManager();
        }
        return instance;
    }

    public int getClaimRank(Claim claim) {
        List<Claim> sorted = claims.values().stream()
                .sorted(Comparator.comparingInt(Claim::getPower).reversed())
                .toList();

        int index = sorted.indexOf(claim);

        return index == -1 ? -1 : index + 1;
    }
}

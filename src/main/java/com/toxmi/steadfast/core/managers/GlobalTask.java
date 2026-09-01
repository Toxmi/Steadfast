package com.toxmi.steadfast.core.managers;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.utils.Scheduler;
import com.toxmi.steadfast.modules.claims.ClaimManager;

public final class GlobalTask extends Scheduler {
    private final ClaimManager claimManager;

    public GlobalTask(Steadfast plugin) {
        super(plugin);
        this.claimManager = ClaimManager.get();
    }

    public void start() {
        globalRegion(() -> {
            claimManager.tickClaims();
        },100,20);
    }
}

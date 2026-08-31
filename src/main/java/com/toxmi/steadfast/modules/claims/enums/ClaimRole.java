package com.toxmi.steadfast.modules.claims.enums;

public enum ClaimRole {
    OWNER(1, "Owner"),
    CO_LEADER(2, "Co-Leader"),
    OFFICER(3, "Officer"),
    MEMBER(4, "Member"),
    LIMITED_MEMBER(5, "Limited Member");

    private final int permission;
    private final String displayName;
    private static final ClaimRole[] VALUES = values();

    ClaimRole(int i, String displayName) {
        this.permission = i;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPermission() {
        return permission;
    }
    public ClaimRole next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
    public ClaimRole previous() {
        return VALUES[(this.ordinal() - 1 + VALUES.length) % VALUES.length];
    }
}

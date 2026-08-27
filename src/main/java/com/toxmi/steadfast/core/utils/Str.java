package com.toxmi.steadfast.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Str {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public static Component cm(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static Component cm(String text, TagResolver... placeholders) {
        return MiniMessage.miniMessage().deserialize(text, placeholders);
    }

    public static Component cc(String text) {
        return LEGACY.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }
    public static Component cc(int text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }
}

package syrena.foxo_origins.client.key.handler;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class FoxoOriginsKeyMapping {
    public static final String CATEGORY = "key.categories.foxo-origins";
    public static final KeyMapping VIEW_ORIGINS_KEYMAPPING = new KeyMapping(
            "key.foxo-origins.view_origin",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_O,
            CATEGORY
    );
    public static final KeyMapping PRIMARY_ACTIVE_KEYMAPPING = new KeyMapping(
            "key.foxo-origins.primary_active",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            CATEGORY
    );
    public static final KeyMapping SECONDARY_ACTIVE_KEYMAPPING = new KeyMapping(
            "key.foxo-origins.secondary_active",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_H,
            CATEGORY
    );
    public static final KeyMapping TERTIARY_ACTIVE_KEYMAPPING = new KeyMapping(
            "key.foxo-origins.tertiary_active",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_B,
            CATEGORY
    );
    public static final KeyMapping QUATERNARY_ACTIVE_KEYMAPPING = new KeyMapping(
            "key.foxo-origins.quaternary_active",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V,
            CATEGORY
    );
    public static void registerKeyMappings(){
        KeyBindingHelper.registerKeyBinding(VIEW_ORIGINS_KEYMAPPING);
        KeyBindingHelper.registerKeyBinding(PRIMARY_ACTIVE_KEYMAPPING);
        KeyBindingHelper.registerKeyBinding(SECONDARY_ACTIVE_KEYMAPPING);
        KeyBindingHelper.registerKeyBinding(TERTIARY_ACTIVE_KEYMAPPING);
        KeyBindingHelper.registerKeyBinding(QUATERNARY_ACTIVE_KEYMAPPING);
    }
}

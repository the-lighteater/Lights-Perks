package dot.lighteater.upgrade_scrolls;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    public static final String CATEGORY =
            "key.categories.upgrade_scrolls";

    public static final KeyMapping OPEN_PERKS =
            new KeyMapping(
                    "key.upgrade_scrolls.open_perks",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_B,
                    CATEGORY
            );
}
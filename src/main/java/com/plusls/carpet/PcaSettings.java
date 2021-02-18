package com.plusls.carpet;

import carpet.settings.Rule;
import carpet.settings.RuleCategory;

public class PcaSettings {
    //    @Rule(desc = "Support", category = "misc")
//    public static int intSetting = 10;
//
//    @Rule(
//            desc = "Support sync entity and blockEntity from server",
//            options = {"PCA", "bar", "baz"},
//            extra = {
//                    "This can take multiple values",
//                    "that you can tab-complete in chat",
//                    "but it can take any value you want"
//            },
//            category = "misc",
//            strict = false
//    )
//    public static String stringSetting = "foo";
    public static final String PCA = "PCA";
    public static final String PROTOCOL = "protocol";

    // protocol
    @Rule(
            desc = "Support sync entity and blockEntity from server",
            category = {PCA, PROTOCOL}
    )
    public static boolean pcaSyncProtocol = false;

    public enum PCA_SYNC_PLAYER_ENTITY_OPTIONS {
        NOBODY, BOT, OPS, OPS_AND_SELF, EVERYONE
    }

    @Rule(
            desc = "Which player entity can be sync",
            extra = {
                    "NOBODY: nobody will be sync",
                    "BOT: carpet bot will be sync",
                    "OPS: carpet bot will be sync, and op can sync everyone's player entity data.",
                    "OPS_AND_SELF: carpet bot and self data will be sync, and op can sync everyone's player entity data.",
                    "EVERYONE: everyone's player entity will be sync",
            },
            category = {PCA, PROTOCOL}
    )
    public static PCA_SYNC_PLAYER_ENTITY_OPTIONS pcaSyncPlayerEntity = PCA_SYNC_PLAYER_ENTITY_OPTIONS.OPS;

    // feature
    @Rule(
            desc = "Empty shulker boxes stack",
            extra = {
                    "empty shulker boxes can stack in a player's inventory or hand",
                    "empty shulker boxes will not stack in other inventories, such as chests or hoppers"
            },
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean emptyShulkerBoxStack = false;

    @Rule(
            desc = "Shulker boxes are renewable; 1.17 feature (backported)",
            category = {PCA, RuleCategory.FEATURE, RuleCategory.EXPERIMENTAL}
    )
    public static boolean shulkerRenewable = false;

    @Rule(
            desc = "Shulker box items will now drop their items when destroyed; 1.17 feature (backported)",
            category = {PCA, RuleCategory.FEATURE, RuleCategory.EXPERIMENTAL}
    )
    public static boolean shulkerBoxQuickUnpack = false;

    @Rule(
            desc = "Rail no broken by fluid; 1.17 feature (backported)",
            category = {PCA, RuleCategory.FEATURE, RuleCategory.EXPERIMENTAL}
    )
    public static boolean railNoBrokenByFluid = false;

    @Rule(
            desc = "Dyes can be used on shulker boxes, empty potion will clean color",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean useDyeOnShulkerBox = false;

    @Rule(
            desc = "Players can flip and rotate blocks when holding Totem Of Undying",
            extra = {
                    "Doesn't cause block updates when rotated/flipped",
                    "When Totem Of Undying in main hand,  offhand is empty will flip block",
                    "When Totem Of Undying in main hand,  offhand is not empty, will place flipped block",
            },
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean flippingTotemOfUndying = false;

    // debug
    @Rule(
            desc = "pcaDebug mode",
            category = {PCA}
    )
    public static boolean pcaDebug = false;
}

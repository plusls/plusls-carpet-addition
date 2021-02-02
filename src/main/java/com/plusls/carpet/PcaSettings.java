package com.plusls.carpet;

import carpet.settings.Rule;
import carpet.settings.RuleCategory;

public class PcaSettings {
    //    public enum Option {
//        OPTION_A, OPTION_B, OPTION_C
//    }
//
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

    // feature
    @Rule(
            desc = "Empty shulker boxes stack",
            extra = {
                    "empty shulker boxes can stack on the ground and in a player's inventory or hand",
                    "empty shulker boxes will not stack in other inventories, such as chests or hoppers"
            },
            category = {PCA, RuleCategory.FEATURE, RuleCategory.EXPERIMENTAL}
    )
    public static boolean emptyShulkerBoxStack = false;

    @Rule(
            desc = "Empty shulker boxes stack in inventories",
            extra = {
                    "empty shulker boxes can stack in inventories like chests or hoppers",
                    "this feature requires emptyShulkerBoxStack to be enabled"
            },
            category = {PCA, RuleCategory.FEATURE, RuleCategory.EXPERIMENTAL}
    )
    public static boolean emptyShulkerBoxStackInInventory = false;

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
            desc = "Dyes can be used on shulker boxes",
            category = {PCA, RuleCategory.FEATURE, RuleCategory.EXPERIMENTAL}
    )
    public static boolean useDyeOnShulkerBox = false;

    // debug
    @Rule(
            desc = "pcaDebug mode",
            category = {PCA}
    )
    public static boolean pcaDebug = false;

//    @Rule(
//            desc = "Example enum setting",
//            extra = {"This is another string-type option", "that conveniently parses and validates for you"},
//            category = "misc")
//    public static Option optionSetting = Option.OPTION_A;
//
//    @Rule(desc = "Example bool setting", category = "misc")
//    public static boolean boolSetting;
}

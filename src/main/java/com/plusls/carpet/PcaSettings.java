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
    public static final String NEED_CLIENT = "need_client";
    final public static int INT_DISABLE = 114514;
    public static final String xaeroWorldNameNone = "#none";
    // protocol
    @Rule(
            desc = "Support sync entity and blockEntity from server",
            category = {PCA, PROTOCOL}
    )
    public static boolean pcaSyncProtocol = false;
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
            category = {PCA, RuleCategory.FEATURE, NEED_CLIENT}
    )
    public static boolean emptyShulkerBoxStack = false;
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
    @Rule(
            desc = "spawn Y Max, 114514 to close",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static int spawnYMax = INT_DISABLE;

    @Rule(
            desc = "spawn Y Min, 114514 to close",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static int spawnYMin = INT_DISABLE;
    @Rule(
            desc = "spawn biome",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static PCA_SPAWN_BIOME spawnBiome = PCA_SPAWN_BIOME.DEFAULT;
    @Rule(
            desc = "quick leaf decay",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean quickLeafDecay = false;
    @Rule(
            desc = "place gravestone after player dead.",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean gravestone = false;
    @Rule(
            desc = "set xaero world name to sync word id to xaerominimap, \"#none\" is disable.",
            category = {PCA, PROTOCOL},
            strict = false,
            options = {xaeroWorldNameNone}
    )
    public static String xaeroWorldName = xaeroWorldNameNone;
    @Rule(
            desc = "Villagers are attracted by emerald block.",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean villagersAttractedByEmeraldBlock = false;
    @Rule(
            desc = "Leash villagers and mobs by lead.",
            category = {PCA, RuleCategory.FEATURE, NEED_CLIENT}
    )
    public static boolean superLead = false;
    @Rule(
            desc = "Allow anvil level cost above 40 (If the client is not installed mod, it will be too expensive but can be used in practice).",
            category = {PCA, RuleCategory.FEATURE, NEED_CLIENT}
    )
    public static boolean avoidAnvilTooExpensive = false;
    @Rule(
            desc = "Allow use bone meal in cactus, sugar cane, chorus flower.",
            category = {PCA, RuleCategory.FEATURE, RuleCategory.DISPENSER}
    )
    public static boolean powerfulBoneMeal = false;
    @Rule(
            desc = "World will switch to night when player sleep during the day.",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean sleepingDuringTheDay = false;
    @Rule(
            desc = "Dispenser can fix iron golem.",
            category = {PCA, RuleCategory.FEATURE, RuleCategory.DISPENSER}
    )
    public static boolean dispenserFixIronGolem = false;
    @Rule(
            desc = "Dispenser use bottle to collect xp.",
            category = {PCA, RuleCategory.FEATURE, RuleCategory.DISPENSER}
    )
    public static boolean dispenserCollectXp = false;
    @Rule(
            desc = "One tick player can place 2 block, insta break 1 block, can't do it at the same tick",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean playerOperationLimiter = false;
    @Rule(
            desc = "Player can sit down when fast sneak 3 times.",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean playerSit = false;
    @Rule(
            desc = "Use dispenser to auto trade with villager",
            extra = {
                    "If EMERALD_BLOCK under the dispenser, it will trade once",
                    "If DIAMOND_BLOCK under the dispenser, it will trade all",
                    "Trade offer depend on redstone power."
            },
            category = {PCA, RuleCategory.FEATURE, RuleCategory.DISPENSER}
    )
    public static boolean autoTrade = false;
    @Rule(
            desc = "Use spectral arrow to shoot villager to force restock.",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean forceRestock = false;
    @Rule(
            desc = "Only in nether, throw the diamond equipment with 1 durability to lava fluid to get netherite equipment.",
            category = {PCA, RuleCategory.FEATURE}
    )
    public static boolean renewableNetheriteEquip = false;

    // debug
    @Rule(
            desc = "pcaDebug mode",
            category = {PCA}
    )
    public static boolean pcaDebug = false;

    public enum PCA_SYNC_PLAYER_ENTITY_OPTIONS {
        NOBODY, BOT, OPS, OPS_AND_SELF, EVERYONE
    }

    public enum PCA_SPAWN_BIOME {
        DEFAULT, DESERT, PLAINS
    }
}

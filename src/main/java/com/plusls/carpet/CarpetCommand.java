package com.plusls.carpet;

import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class CarpetCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("testcommand").
                then(literal("first").
                        executes((c) -> {
                            Messenger.m(c.getSource(), "gi Shhhh.....");
                            return 1;
                        })).
                then(literal("second").
                        executes((c) -> listSettings(c.getSource()))));

    }

    private static int listSettings(ServerCommandSource source) {
        Messenger.m(source, "w Here is all the settings we manage:");
        Messenger.m(source, "w Own stuff:");
//        Messenger.m(source, "w  - boolean: " + PCASettings.boolSetting);
//        Messenger.m(source, "w  - string: " + PCASettings.stringSetting);
//        Messenger.m(source, "w  - int: " + PCASettings.intSetting);
//        Messenger.m(source, "w  - enum: " + PCASettings.optionSetting);
        return 1;
    }
}
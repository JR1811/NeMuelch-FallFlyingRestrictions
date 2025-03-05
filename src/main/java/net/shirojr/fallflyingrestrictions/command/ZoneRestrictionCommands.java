package net.shirojr.fallflyingrestrictions.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.shirojr.fallflyingrestrictions.data.PersistentWorldData;
import net.shirojr.fallflyingrestrictions.data.VolumeData;
import net.shirojr.fallflyingrestrictions.data.shape.BoxShape;
import net.shirojr.fallflyingrestrictions.data.shape.SphereShape;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ZoneRestrictionCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("flying").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(literal("zone")
                        .then(literal("restriction")
                                .then(literal("print")
                                        .executes(ZoneRestrictionCommands::printAll)
                                )
                                .then(literal("add")
                                        .then(literal("box")
                                                .then(argument("start", BlockPosArgumentType.blockPos())
                                                        .then(argument("end", BlockPosArgumentType.blockPos())
                                                                .then(argument("considerHeight", BoolArgumentType.bool())
                                                                        .then(argument("preventStartFlying", BoolArgumentType.bool())
                                                                                .suggests(PREVENT_START_FLYING)
                                                                                .then(argument("interruptFlying", BoolArgumentType.bool())
                                                                                        .suggests(INTERRUPT_FLYING)
                                                                                        .executes(ZoneRestrictionCommands::addBox)
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(literal("sphere")
                                                .then(argument("center", BlockPosArgumentType.blockPos())
                                                        .then(argument("distance", DoubleArgumentType.doubleArg(0))
                                                                .suggests(DISTANCE)
                                                                .then(argument("preventStartFlying", BoolArgumentType.bool())
                                                                        .suggests(PREVENT_START_FLYING)
                                                                        .then(argument("interruptFlying", BoolArgumentType.bool())
                                                                                .suggests(INTERRUPT_FLYING)
                                                                                .executes(ZoneRestrictionCommands::addSphere)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("remove")
                                        .executes(ZoneRestrictionCommands::removeAll)
                                        .then(argument("center", BlockPosArgumentType.blockPos())
                                                .executes(ZoneRestrictionCommands::remove))
                                )
                        )
                )
        );
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        BlockPos center = BlockPosArgumentType.getBlockPos(context, "center");
        accessWorldData(context).modifyNoFlyingZones(data -> data.removeIf(entry -> entry.volume().center().equals(center)), context.getSource().getServer());
        print(context, "Removed volume", true);
        return Command.SINGLE_SUCCESS;
    }

    private static int removeAll(CommandContext<ServerCommandSource> context) {
        accessWorldData(context).modifyNoFlyingZones(List::clear, context.getSource().getServer());
        print(context, "Removed all volumes", true);
        return Command.SINGLE_SUCCESS;
    }

    private static int printAll(CommandContext<ServerCommandSource> context) {
        List<VolumeData> list = accessWorldData(context).getNoFlyingZones();
        for (int i = 0; i < list.size(); i++) {
            VolumeData entry = list.get(i);
            print(context, "%s | Center: %s | Volume Count: %s".formatted(
                    entry.identifier().getPath(),
                    entry.volume().center().toShortString(),
                    entry.volume().blockCount()
            ), true);
            StringBuilder builder = new StringBuilder();
            if (entry.volume().preventStartFlying()) builder.append(" | prevents flying take-off | ");
            if (entry.volume().interruptFlying()) builder.append(" | player will fall out of the air | ");
            print(context, builder.toString(), true);
            if (i < list.size() - 1) {
                print(context, "-----", true);
            }
        }
        if (list.isEmpty()) print(context, "No saved volumes", true);
        return Command.SINGLE_SUCCESS;
    }

    private static int addBox(CommandContext<ServerCommandSource> context) {
        BlockPos start = BlockPosArgumentType.getBlockPos(context, "start");
        BlockPos end = BlockPosArgumentType.getBlockPos(context, "end");
        boolean considerHeight = BoolArgumentType.getBool(context, "considerHeight");
        boolean preventStartFlying = BoolArgumentType.getBool(context, "preventStartFlying");
        boolean interruptFlying = BoolArgumentType.getBool(context, "interruptFlying");

        VolumeData volumeData = new VolumeData(BoxShape.IDENTIFIER, new BoxShape(start, end, considerHeight, preventStartFlying, interruptFlying));
        accessWorldData(context).modifyNoFlyingZones(volumeDataList -> volumeDataList.add(volumeData), context.getSource().getServer());

        StringBuilder builder = new StringBuilder("Created new Box Shape | start: [%s] | end: [%s]".formatted(start.toShortString(), end.toShortString()));
        if (considerHeight) builder.append(" | considers height values");
        if (preventStartFlying) builder.append(" | prevents start flying");
        if (interruptFlying) builder.append(" | player will fall out of the air");
        print(context, builder.toString(), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int addSphere(CommandContext<ServerCommandSource> context) {
        BlockPos center = BlockPosArgumentType.getBlockPos(context, "center");
        double distance = DoubleArgumentType.getDouble(context, "distance");
        boolean preventStartFlying = BoolArgumentType.getBool(context, "preventStartFlying");
        boolean interruptFlying = BoolArgumentType.getBool(context, "interruptFlying");

        VolumeData volumeData = new VolumeData(SphereShape.IDENTIFIER, new SphereShape(center, distance, preventStartFlying, interruptFlying));
        accessWorldData(context).modifyNoFlyingZones(volumeDataList -> volumeDataList.add(volumeData), context.getSource().getServer());

        StringBuilder builder = new StringBuilder("Created new Sphere Shape | center: [%s] | distance: [%s]".formatted(center.toShortString(), distance));
        if (preventStartFlying) builder.append(" | prevents start flying");
        if (interruptFlying) builder.append(" | player will fall out of the air");
        print(context, builder.toString(), true);

        return Command.SINGLE_SUCCESS;
    }


    private static void print(CommandContext<ServerCommandSource> context, String message, boolean toOps) {
        context.getSource().sendFeedback(() -> Text.literal(message), toOps);
    }

    private static PersistentWorldData accessWorldData(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        return PersistentWorldData.getServerState(server, context.getSource().getWorld().getRegistryKey());
    }


    private static final SuggestionProvider<ServerCommandSource> PREVENT_START_FLYING = (context, builder) -> {
        builder.suggest("true", Text.literal("Players can't start flying in this volume"));
        builder.suggest("false", Text.literal("Players can start flying in this volume"));
        return builder.buildFuture();
    };

    private static final SuggestionProvider<ServerCommandSource> INTERRUPT_FLYING = (context, builder) -> {
        builder.suggest("true", Text.literal("Players will fall out of the air in that volume"));
        builder.suggest("false", Text.literal("Players can fly in that volume"));
        return builder.buildFuture();
    };

    private static final SuggestionProvider<ServerCommandSource> DISTANCE = (context, builder) -> {
        builder.suggest("12.3", Text.literal("Defines the radius of the sphere"));
        return builder.buildFuture();
    };
}

package dev.caecorthus.sparkwitch.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.caecorthus.sparkwitch.roles.neutral.ferryman.FerrymanAutoPardon;
import dev.caecorthus.sparkwitch.util.SparkWitchPermissions;
import dev.doctor4t.wathe.Wathe;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

/**
 * Test-only command: {@code /sparkwitch:ferrymanAutoPardon <enabled> [players]}. A Charon held by the switch presses the
 * refusal the instant a pardon window opens, so a tester never has to hit the key inside the 1.5 s window. The press
 * itself is the real one — cooldown, delayed glance and messages all stay untouched.
 * 仅用于测试的命令：{@code /sparkwitch:ferrymanAutoPardon <enabled> [players]}。被开关接管的卡戎会在宽恕窗口开启的瞬间自己
 * 按下免死，测试者不必在 1.5 秒窗口里掐点。按键本身仍是真实的那一次——冷却、延迟落下的「瞥视」与提示都不改。
 */
public final class FerrymanAutoPardonCommand {
    private static final String PERMISSION = "sparkwitch.command.ferrymanautopardon";
    private static final String ON_KEY = "message.sparkwitch.ferryman.autopardon.on";
    private static final String OFF_KEY = "message.sparkwitch.ferryman.autopardon.off";

    private FerrymanAutoPardonCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("sparkwitch:ferrymanAutoPardon")
                        .requires(Permissions.require(PERMISSION, SparkWitchPermissions.DEFAULT_COMMAND_LEVEL))
                        .then(
                                CommandManager.argument("enabled", BoolArgumentType.bool())
                                        .executes(context -> execute(
                                                context.getSource(),
                                                List.of(context.getSource().getPlayerOrThrow()),
                                                BoolArgumentType.getBool(context, "enabled")
                                        ))
                                        .then(
                                                CommandManager.argument("players", EntityArgumentType.players())
                                                        .executes(context -> execute(
                                                                context.getSource(),
                                                                EntityArgumentType.getPlayers(context, "players"),
                                                                BoolArgumentType.getBool(context, "enabled")
                                                        ))
                                        )
                        )
        );
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, boolean enabled) {
        return Wathe.executeSupporterCommand(source, () -> {
            for (ServerPlayerEntity target : targets) {
                FerrymanAutoPardon.set(target.getUuid(), enabled);
                target.sendMessage(Text.translatable(enabled ? ON_KEY : OFF_KEY), true);
            }
        });
    }
}

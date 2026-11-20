package dev.caecorthus.sparkwitch.roles.neutral.ferryman;

import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test-only convenience switch: a Charon on this list presses the refusal by itself the instant a pardon window opens,
 * so a tester never has to hit the key inside the 1.5 s window. Nothing here changes the pardon itself — the automatic
 * press walks the exact same {@link FerrymanPardonService#onPress} path a real key press does, so the cooldown, the
 * delayed glance and the messages all stay real.
 * 仅用于测试的开关：名单上的卡戎在宽恕窗口开启的瞬间会自己按下免死，测试者不必在 1.5 秒窗口里掐点。这里不修改宽恕本身——
 * 自动按键走的是与真人按键完全相同的 {@link FerrymanPardonService#onPress} 路径，冷却、延迟落下的「瞥视」与提示都保持真实。
 *
 * <p>Server only, never persisted: a restart clears every entry, so a test build can never carry the switch into a real
 * session. Enable and disable with {@code /sparkwitch:ferrymanAutoPardon}.
 * 仅服务端、从不持久化：重启即清空全部条目，因此测试构建的开关绝不会被带进真正的对局。用
 * {@code /sparkwitch:ferrymanAutoPardon} 开关。</p>
 */
public final class FerrymanAutoPardon {
    private static final Set<UUID> ENABLED = ConcurrentHashMap.newKeySet();

    private FerrymanAutoPardon() {
    }

    public static void set(@Nullable UUID player, boolean enabled) {
        if (player == null) {
            return;
        }
        if (enabled) {
            ENABLED.add(player);
        } else {
            ENABLED.remove(player);
        }
    }

    public static boolean isEnabled(@Nullable UUID player) {
        return player != null && ENABLED.contains(player);
    }

    public static void clearAll() {
        ENABLED.clear();
    }
}

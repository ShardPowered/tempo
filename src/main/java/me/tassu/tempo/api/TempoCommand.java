package me.tassu.tempo.api;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.val;
import me.tassu.tempo.db.user.UserManager;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.optional.qual.MaybePresent;

public abstract class TempoCommand implements Command {

    private int permission;

    public TempoCommand() {
        this.permission = -1;
    }

    public TempoCommand(int permission) {
        this.permission = permission;
    }

    /**
     * Executes the command for the specified {@link CommandSource}.
     *
     * @param source the source of this command
     * @param args the arguments for this command
     */
    protected abstract void run(CommandSource source, String @NonNull [] args);

    @Override
    public void execute(CommandSource source, @NonNull String[] args) {
        if (source instanceof Player) {
            val player = (Player) source;
            val user = UserManager.getInstance().get(player.getUniqueId());
            val rank = user.getRank();

            if (rank.getWeight() < permission) {
                source.sendMessage(TextComponent.of("Your permission level is too low.", TextColor.GRAY));
                return;
            }
        }

        run(source, args);
    }

    @Override
    public @MaybePresent boolean hasPermission(@MaybePresent CommandSource source, @MaybePresent @NonNull String[] args) {
        if (source == null) return false;

        if (source instanceof Player) {
            val player = (Player) source;
            val user = UserManager.getInstance().get(player.getUniqueId());
            val rank = user.getRank();

            return rank.getWeight() >= permission;
        }

        return true;
    }
}

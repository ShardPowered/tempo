package me.tassu.tempo.whitelist;

import com.velocitypowered.api.command.CommandSource;
import lombok.experimental.var;
import lombok.val;
import me.tassu.tempo.api.TempoCommand;
import me.tassu.tempo.db.user.UserManager;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WhitelistAdminCommand extends TempoCommand {

    public WhitelistAdminCommand() {
        super(100);
    }

    @Override
    protected void run(CommandSource source, @NonNull String[] args) {
        if (args.length < 2) {
            sendHelp(source);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "priority":
                int priority = 0;
                try {
                    priority = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {}

                WhitelistConfig.getInstance().setRequiredRankWeight(priority);
                source.sendMessage(TextComponent.of("Set whitelist required rank weight to " + priority, TextColor.GRAY));
                break;
            case "tokens":
                boolean state = false;
                val stateStr = args[1];

                if (stateStr.equalsIgnoreCase("on") || stateStr.equalsIgnoreCase("yes") || stateStr.equalsIgnoreCase("true")) {
                    state = true;
                }

                WhitelistConfig.getInstance().setEnableWhitelistTokens(state);
                source.sendMessage(TextComponent.of((state ? "Enabled" : "Disabled") + " whitelist tokens", TextColor.GRAY));
                break;
            case "check":
                var optUser = UserManager.getInstance().get(args[1]);

                if (!optUser.isPresent()) {
                    source.sendMessage(TextComponent.of("User not found.", TextColor.GRAY));
                    return;
                }

                var user = optUser.get();

                if (!user.isWhitelisted()) {
                    source.sendMessage(TextComponent.of(user.getUserName() + " is not whitelisted and has " + user.getWhitelists() + " tokens remaining.", TextColor.GRAY));
                    return;
                }

                val whitelistedBy = UserManager.getInstance().get(user.getWhitelistedBy());
                val whitelitedByName = whitelistedBy == null ? "An unknown player" : whitelistedBy.getUserName();

                source.sendMessage(TextComponent.of(user.getUserName() + " was whitelisted by " + whitelitedByName + ".", TextColor.GRAY));
                source.sendMessage(TextComponent.of(user.getUserName() + " has " + user.getWhitelists() + " tokens remaining.", TextColor.GRAY));
                break;
            case "add":
                optUser = UserManager.getInstance().get(args[1]);

                if (!optUser.isPresent()) {
                    source.sendMessage(TextComponent.of("User not found.", TextColor.GRAY));
                    return;
                }

                user = optUser.get();

                if (args.length != 3) {
                    sendHelp(source);
                    return;
                }

                int amount;
                try {
                    amount = Integer.valueOf(args[2]);
                } catch (NumberFormatException e) {
                    sendHelp(source);
                    return;
                }

                user.setWhitelists(user.getWhitelists() + amount);
                source.sendMessage(TextComponent.of("Added " + amount + " whitelists to " + user.getUserName() + ".", TextColor.GRAY));
                break;
            default:
                sendHelp(source);
        }
    }

    private void sendHelp(CommandSource source) {
        source.sendMessage(TextComponent.of("Help for /whitelistadmin", TextColor.GOLD));
        source.sendMessage(TextComponent.of(""));
        source.sendMessage(TextComponent.of("Whitelist rank management commands", TextColor.WHITE));
        source.sendMessage(TextComponent.of("- /whitelistadmin priority [priority=0]", TextColor.GRAY));
        source.sendMessage(TextComponent.of(""));
        source.sendMessage(TextComponent.of("Whitelist token management commands", TextColor.WHITE));
        source.sendMessage(TextComponent.of("- /whitelistadmin tokens [on|off]", TextColor.GRAY));
        source.sendMessage(TextComponent.of("- /whitelistadmin check [user]", TextColor.GRAY));
        source.sendMessage(TextComponent.of("- /whitelistadmin add [user] [tokens]", TextColor.GRAY));
        source.sendMessage(TextComponent.of(""));
    }

}

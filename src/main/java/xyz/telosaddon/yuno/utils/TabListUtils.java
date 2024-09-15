package xyz.telosaddon.yuno.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TabListUtils {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final String pingPattern = "^Ping:\\s\\d+$";
    public static final String serverPattern = "Server:\\s*\\[(.*?)\\]";

    public static Optional<String> getPing(){
        Optional<String> ping = getLineMatches(pingPattern);

        if(ping.isEmpty()) return Optional.empty();

        String result = ping.get().split(": ")[1];
        return Optional.of(result);
    }

    public static Optional<String> getServer(){
       Optional<String> server = getLineMatches(serverPattern);

        if(server.isEmpty()) return Optional.empty();

        String content = server.get().replaceAll("Server:\\s*\\[(.*?)]", "$1");
        return Optional.of(content);
    }

    public static Optional<String> getLineMatches(String pattern){
        ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if(networkHandler == null) return Optional.empty();

        Collection<PlayerListEntry> playerCollection =  networkHandler.getPlayerList();
        List<PlayerListEntry> PlayerEntries = new ArrayList<>(playerCollection);

        if(PlayerEntries.isEmpty()) return Optional.empty();

        List<String> playerNameList = PlayerEntries.stream()
                .filter(player -> Objects.nonNull(player.getDisplayName()))
                .map(player -> player.getDisplayName().getString())
                .filter(Objects::nonNull)
                .map(playerString -> stripAllFormatting(playerString).trim())
                .filter(playerString -> !playerString.isEmpty())
                .toList();

        return playerNameList.stream().filter(player -> Pattern.matches(pattern, player)).findFirst();
    }

    private static String stripAllFormatting(String input) {
        String withoutColors = input.replaceAll("§[0-9a-fk-or]", "");
        return withoutColors.replaceAll("[^\\p{ASCII}]", "");
    }
}

package gg.minehut.flexed.event;

import gg.minehut.flexed.data.PlayerData;
import gg.minehut.flexed.task.impl.LocationTask;
import gg.minehut.flexed.util.ColorUtil;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

@Getter
public abstract class Event implements Listener {

    public final ArrayList<PlayerData> players = new ArrayList<>();
    public final ArrayList<PlayerData> spectators = new ArrayList<>();
    public PlayerData host;
    public State state;
    public int gameTime, maxPlayers;

    public abstract void update();

    public void addPlayer(PlayerData player) {
        if (players.contains(player)) {
            player.getPlayer().sendMessage(ChatColor.RED + "You are already in a event!");
            return;
        }
        if (players.size() >= maxPlayers) {
            player.getPlayer().sendMessage(ColorUtil.translate("&cThat event is fulL!"));
            return;
        }
        if (state == State.WAITING) {
            if (!players.contains(player)) {
                players.add(player);
                player.getPlayer().sendMessage(ColorUtil.translate("&aYou have joined the event!"));
            }
        } else {
            if (!spectators.contains(player)) {
                spectators.add(player);
                player.getPlayer().sendMessage(ColorUtil.translate("&eYou are now spectating the event!"));
            }
        }
    }

    public void removePlayer(PlayerData player) {
        spectators.remove(player);
        players.remove(player);
        player.getPlayer().sendMessage(ColorUtil.translate("&cYou have left the event!"));
        for (PotionEffect effect : player.getPlayer().getActivePotionEffects())
            player.getPlayer().removePotionEffect(effect.getType());
        player.getPlayer().teleport(LocationTask.getInstance().get("spawn"));
    }


    public enum State {
        WAITING, INGAME, END;

        public String toString() {
            return StringUtils.capitalize(this.name().toLowerCase());
        }

        public State next() {
            if (values()[ordinal() + 1] == null) {
                return values()[0];
            }
            return values()[ordinal() + 1];
        }

    }

    public enum Type {
        SUMO, FFA, SKYWARS;

        public String toString() {
            return StringUtils.capitalize(this.name().toLowerCase());
        }
    }

}
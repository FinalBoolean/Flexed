package gg.minehut.flexed.event.events;

import gg.minehut.flexed.Flexed;
import gg.minehut.flexed.data.PlayerData;
import gg.minehut.flexed.event.Event;
import gg.minehut.flexed.event.maps.SumoMap;
import gg.minehut.flexed.task.impl.EventManager;
import gg.minehut.flexed.util.ColorUtil;
import gg.minehut.flexed.util.Pair;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Sumo extends Event {

    private final Pair<PlayerData, PlayerData> roundPlayers = new Pair<>();
    private final SumoMap map;
    public int round;

    public Sumo(SumoMap map, PlayerData host) {
        this.map = map;
        maxPlayers = 50;
        state = State.WAITING;
        gameTime = 30;
        this.host = host;
    }

    @Override
    public void addPlayer(PlayerData player) {
        player.getPlayer().teleport(map.getSpawnLocation());
        player.getPlayer().getInventory().clear();
        player.getPlayer().getInventory().setHelmet(null);
        player.getPlayer().getInventory().setChestplate(null);
        player.getPlayer().getInventory().setLeggings(null);
        player.getPlayer().getInventory().setBoots(null);
        Flexed.getInstance().getDataManager().get(player.getPlayer()).updateBoard();
        super.addPlayer(player);
    }

    @Override
    public void update() {
        updateBoard();
        switch (state) {
            case WAITING: {
                gameTime--;
                if (gameTime == 29 || gameTime == 20 || gameTime == 10) {
                    TextComponent textComponent = new TextComponent(ColorUtil.translate("&6[Event] &f" + host.getPlayer().getName() + " &7is hosting a &fSumo Event! &a[Click to join]"));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event join"));
                    Bukkit.broadcastMessage(ColorUtil.translate("&7"));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.spigot().sendMessage(textComponent);
                    }
                    Bukkit.broadcastMessage(ColorUtil.translate("&7"));
                }
                if (gameTime == 0) {
                    if (players.size() > 1) {
                        state = State.INGAME;
                        Bukkit.broadcastMessage(ColorUtil.translate("&6[Event] &fSumo Event &7has started!"));
                    } else {
                        EventManager.getInstance().deleteEvent("Not enough players.");
                        state = State.END;
                    }
                }
                break;
            }
            case INGAME: {
                if (roundPlayers.getY() == null) {
                    gameTime = 5;
                    round++;

                    Random random = new Random();

                    PlayerData randomPlayer = players.get(random.nextInt(players.size()));

                    PlayerData randomPlayer2 = players.get(random.nextInt(players.size()));

                    while (randomPlayer == randomPlayer2 && players.size() > 1) {
                        randomPlayer2 = players.get(random.nextInt(players.size()));
                    }
                    if (players.size() < 2) {
                        Bukkit.broadcastMessage(ColorUtil.translate("&6[Event] &f" + players.get(0).getPlayer().getName() + " &7has won the &fSumo Event&7!"));
                        state = State.END;
                    }

                    roundPlayers.setX(randomPlayer);
                    roundPlayers.setY(randomPlayer2);
                } else {
                    if (gameTime == 5) {
                        players.forEach(playerData -> playerData.getPlayer().sendMessage(ColorUtil.translate("&6[Event] &eRound #" + round + ": &f" + roundPlayers.getX().getPlayer().getName() + " &7vs &f" + roundPlayers.getY().getPlayer().getName())));

                        roundPlayers.getX().getPlayer().teleport(map.getFightLocation1());
                        roundPlayers.getY().getPlayer().teleport(map.getFightLocation2());

                        PotionEffect potionEffect = PotionEffectType.JUMP.createEffect(100, 200);
                        PotionEffect potionEffect2 = PotionEffectType.SLOW.createEffect(100, 100);

                        roundPlayers.getX().getPlayer().addPotionEffect(potionEffect);
                        roundPlayers.getY().getPlayer().addPotionEffect(potionEffect2);

                        roundPlayers.getX().getPlayer().addPotionEffect(potionEffect);
                        roundPlayers.getY().getPlayer().addPotionEffect(potionEffect2);

                        roundPlayers.getX().getPlayer().sendMessage(ColorUtil.translate("&6[Event] &7Round against &f" + roundPlayers.getY().getPlayer().getName() + " &7starting in &f5s..."));
                        roundPlayers.getY().getPlayer().sendMessage(ColorUtil.translate("&6[Event] &7Round against &f" + roundPlayers.getX().getPlayer().getName() + " &7starting in &f5s..."));

                    }
                    if (gameTime > 0 && gameTime < 4) {
                        roundPlayers.getX().getPlayer().teleport(map.getFightLocation1());
                        roundPlayers.getY().getPlayer().teleport(map.getFightLocation2());

                        String roundMSG = ColorUtil.translate("&6[Event] &7Round starting in &f" + gameTime + "s");
                        roundPlayers.getX().getPlayer().sendMessage(roundMSG);
                        roundPlayers.getY().getPlayer().sendMessage(roundMSG);

                    }
                    if (players.size() < 2) {
                        Bukkit.broadcastMessage(ColorUtil.translate("&6[Event] &f" + players.get(0).getPlayer().getName() + " &7has won the &fSumo Event&7!"));

                        state = State.END;

                    }
                    if (gameTime == 1) {
                        roundPlayers.getX().getPlayer().teleport(map.getFightLocation1());
                        roundPlayers.getY().getPlayer().teleport(map.getFightLocation2());

                        for (PotionEffect loopEffectX : roundPlayers.getX().getPlayer().getActivePotionEffects()) {
                            roundPlayers.getX().getPlayer().removePotionEffect(loopEffectX.getType());
                        }

                        for (PotionEffect loopEffectY : roundPlayers.getY().getPlayer().getActivePotionEffects()) {
                            roundPlayers.getY().getPlayer().removePotionEffect(loopEffectY.getType());
                        }

                        roundPlayers.getX().getPlayer().getInventory().clear();
                        roundPlayers.getY().getPlayer().getInventory().clear();

                        PotionEffect potionEffect = PotionEffectType.DAMAGE_RESISTANCE.createEffect(999999, 100);
                        roundPlayers.getX().getPlayer().addPotionEffect(potionEffect);
                        roundPlayers.getY().getPlayer().addPotionEffect(potionEffect);

                        String roundMSG = ColorUtil.translate("&6[Event] &aRound has started!");
                        roundPlayers.getX().getPlayer().sendMessage(roundMSG);
                        roundPlayers.getY().getPlayer().sendMessage(roundMSG);
                    }
                    if (gameTime == 0) {

                        Location player1 = roundPlayers.getX().getPlayer().getLocation();
                        Location player2 = roundPlayers.getY().getPlayer().getLocation();

                        if (player1.getY() <= map.getFallLevel() || !roundPlayers.getX().getPlayer().isOnline()) {
                            players.remove(roundPlayers.getX());
                            addPlayer(roundPlayers.getX());
                            roundPlayers.getX().getPlayer().teleport(map.getSpawnLocation());
                            roundPlayers.getY().getPlayer().teleport(map.getSpawnLocation());
                            roundPlayers.setX(null);
                            roundPlayers.setY(null);
                        }

                        if (player2.getY() <= map.getFallLevel() || !roundPlayers.getY().getPlayer().isOnline()) {
                            players.remove(roundPlayers.getY());
                            addPlayer(roundPlayers.getY());
                            roundPlayers.getY().getPlayer().teleport(map.getSpawnLocation());
                            roundPlayers.getX().getPlayer().teleport(map.getSpawnLocation());
                            roundPlayers.setX(null);
                            roundPlayers.setY(null);
                        }
                    }
                    if (gameTime != 0) {
                        gameTime--;
                    }
                }
                break;
            }
            case END: {
                if (players.isEmpty() && spectators.isEmpty()) {
                    EventManager.getInstance().deleteEvent();
                } else {
                    if (!players.isEmpty()) removePlayer(players.get(0));
                    if (!spectators.isEmpty()) removePlayer(spectators.get(0));
                }
                break;
            }
        }

    }

    private void updateBoard() {

    }

}
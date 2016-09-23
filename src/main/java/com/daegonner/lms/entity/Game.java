package com.daegonner.lms.entity;

import com.daegonner.lms.settings.ArenaSettings;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

/**
 * Contains all data for an active LMS game.
 */
public class Game {

    private final Arena arena;
    private final Set<Player> participants;
    private final Set<Player> spectators = new HashSet<>();
    private final Map<Player, PlayerSnapshot> snapshots = new HashMap<>();

    public Game(Arena arena, Set<Player> participants) {
        this.arena = arena;
        this.participants = participants;
    }

    /**
     * Gets the arena in use.
     *
     * @return the arena.
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Gets all non-dead participants.
     *
     * @return the participants.
     */
    public Set<Player> getParticipants() {
        return participants;
    }

    /**
     * Gets the spectators.
     *
     * @return the spectators.
     */
    public Set<Player> getSpectators() {
        return spectators;
    }

    /**
     * Gets the participant snapshots for recovery on death or game end.
     *
     * @return the snapshots.
     */
    public Map<Player, PlayerSnapshot> getSnapshots() {
        return snapshots;
    }

    /**
     * Pulses the current game.
     */
    public void pulse() {
        // TODO: Implement fail safe checks.
    }

    /**
     * Initializes the games state, managing player locations and inventories.
     *
     * @param settings the settings for this specific arena.
     */
    public void start(ArenaSettings settings) {
        snapshotParticipants();
        teleportParticipants();
        loadKits(settings);
    }

    /**
     * Stores the snapshot of every participant in the game.
     */
    private void snapshotParticipants() {
        participants.forEach(this::snapshotParticipant);
    }

    /**
     * Stores a players snapshot.
     *
     * @param player the player.
     */
    private void snapshotParticipant(Player player) {
        snapshots.put(player, PlayerSnapshot.of(player));
    }

    /**
     * Teleports all participants to the arena.
     */
    private void teleportParticipants() {
        for (Player player : participants) {
            player.teleport(arena.getRandomSpawn());
        }
    }

    /**
     * Loads all player kits.
     *
     * @param settings the arena specific settings.
     */
    private void loadKits(ArenaSettings settings) {
        ItemStack[] contents = settings.getInventory().toArray(new ItemStack[settings.getInventory().size()]);
        for (Player player : participants) {
            PlayerInventory inventory = player.getInventory();
            inventory.setContents(contents);
            inventory.setHelmet(settings.getHeadArmour());
            inventory.setChestplate(settings.getBodyArmour());
            inventory.setLeggings(settings.getLegArmour());
            inventory.setBoots(settings.getLegArmour());
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    /**
     * Registers a player death in the game.
     *
     * @param player the player.
     */
    public void playerDeath(Player player) {
        if (!participants.contains(player)) {
            return;
        }

        PlayerSnapshot snapshot = snapshots.remove(player);
        if (snapshot != null) {
            snapshot.restore();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(arena, game.arena) &&
                Objects.equals(participants, game.participants) &&
                Objects.equals(spectators, game.spectators) &&
                Objects.equals(snapshots, game.snapshots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arena, participants, spectators, snapshots);
    }

    @Override
    public String toString() {
        return "Game{" +
                "arena=" + arena +
                ", participants=" + participants +
                ", spectators=" + spectators +
                ", snapshots=" + snapshots +
                '}';
    }
}

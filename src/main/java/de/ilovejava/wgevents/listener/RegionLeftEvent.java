package de.ilovejava.wgevents.listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.ilovejava.wgevents.WGEventsMovment;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public class RegionLeftEvent extends RegionEvent
{
    public RegionLeftEvent(ProtectedRegion region, Player player, WGEventsMovment movement, PlayerEvent parent)
    {
        super(region, player, movement, parent);
    }
}

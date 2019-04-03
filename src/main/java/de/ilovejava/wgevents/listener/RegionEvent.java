package de.ilovejava.wgevents.listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.ilovejava.wgevents.WGEventsMovment;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class RegionEvent extends PlayerEvent {

    private static final HandlerList handlerList = new HandlerList();
    
    private ProtectedRegion region;
    private WGEventsMovment movement;
    public PlayerEvent parentEvent;

    public RegionEvent(ProtectedRegion region, Player player, WGEventsMovment movement, PlayerEvent parent)
    {
        super(player);
        this.region = region;
        this.movement = movement;
        this.parentEvent = parent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public ProtectedRegion getRegion()
    {
        return region;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlerList;
    }
    
    public WGEventsMovment getMovementWay()
    {
        return this.movement;
    }
    
    public PlayerEvent getParentEvent()
    {
        return parentEvent;
    }
}

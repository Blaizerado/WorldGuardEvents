package de.ilovejava.wgevents.listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.ilovejava.wgevents.WGEventsMovment;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;


public class RegionEnterEvent extends RegionEvent implements Cancellable
{
    private boolean cancelled, cancellable;

    public RegionEnterEvent(ProtectedRegion region, Player player, WGEventsMovment movement, PlayerEvent parent)
    {
        super(region, player, movement, parent);
        cancelled = false;
        cancellable = true;
        
        if (movement == WGEventsMovment.SPAWN
            || movement == WGEventsMovment.DISCONNECT)
        {
            cancellable = false;
        }
    }
    
    @Override
    public void setCancelled(boolean cancelled)
    {
        if (!this.cancellable)
        {
            return;
        }
        
        this.cancelled = cancelled;
    }
    
    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }
    
    public boolean isCancellable()
    {
        return this.cancellable;
    }
    
    protected void setCancellable(boolean cancellable)
    {
        this.cancellable = cancellable;
        
        if (!this.cancellable)
        {
            this.cancelled = false;
        }
    }
}

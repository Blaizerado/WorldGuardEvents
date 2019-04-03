package de.ilovejava.wgevents;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.ilovejava.wgevents.listener.RegionEnterEvent;
import de.ilovejava.wgevents.listener.RegionEnteredEvent;
import de.ilovejava.wgevents.listener.RegionLeaveEvent;
import de.ilovejava.wgevents.listener.RegionLeftEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.*;

public class WGEventsListener implements Listener
{
    private WorldGuardPlugin wgPlugin;
    private WGEvents plugin;
    
    private Map<Player, Set<ProtectedRegion>> playerRegions;
    
    public WGEventsListener(WGEvents plugin, WorldGuardPlugin wgPlugin)
    {
        this.plugin = plugin;
        this.wgPlugin = wgPlugin;
        
        playerRegions = new HashMap<Player, Set<ProtectedRegion>>();
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent e)
    {
        Set<ProtectedRegion> regions = playerRegions.remove(e.getPlayer());
        if (regions != null)
        {
            for(ProtectedRegion region : regions)
            {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), WGEventsMovment.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), WGEventsMovment.DISCONNECT, e);

                plugin.getServer().getPluginManager().callEvent(leaveEvent);
                plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        Set<ProtectedRegion> regions = playerRegions.remove(e.getPlayer());
        if (regions != null)
        {
            for(ProtectedRegion region : regions)
            {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), WGEventsMovment.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), WGEventsMovment.DISCONNECT, e);

                plugin.getServer().getPluginManager().callEvent(leaveEvent);
                plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        e.setCancelled(updateRegions(e.getPlayer(), WGEventsMovment.MOVE, e.getTo(), e));
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e)
    {
        e.setCancelled(updateRegions(e.getPlayer(), WGEventsMovment.TELEPORT, e.getTo(), e));
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        updateRegions(e.getPlayer(), WGEventsMovment.SPAWN, e.getPlayer().getLocation(), e);
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e)
    {
        updateRegions(e.getPlayer(), WGEventsMovment.SPAWN, e.getRespawnLocation(), e);
    }
    
    private synchronized boolean updateRegions(final Player player, final WGEventsMovment movement, Location to, final PlayerEvent event)
    {
        Set<ProtectedRegion> regions;
        Set<ProtectedRegion> oldRegions;
        
        if (playerRegions.get(player) == null)
        {
            regions = new HashSet<ProtectedRegion>();
        }
        else
        {
            regions = new HashSet<ProtectedRegion>(playerRegions.get(player));
        }
        
        oldRegions = new HashSet<ProtectedRegion>(regions);
        
        RegionManager rm = wgPlugin.getRegionManager(to.getWorld());
        
        if (rm == null)
        {
            return false;
        }
        
        HashSet<ProtectedRegion> appRegions = new HashSet<ProtectedRegion>(
                rm.getApplicableRegions(to).getRegions());
        ProtectedRegion globalRegion = rm.getRegion("__global__");
        if (globalRegion != null)
        {
            appRegions.add(globalRegion);
        }
        
        for (final ProtectedRegion region : appRegions)
        {
            if (!regions.contains(region))
            {
                RegionEnterEvent e = new RegionEnterEvent(region, player, movement, event);
                
                plugin.getServer().getPluginManager().callEvent(e);
                
                if (e.isCancelled())
                {
                    regions.clear();
                    regions.addAll(oldRegions);
                    
                    return true;
                }
                else
                {
                    Bukkit.getScheduler().runTaskLater(plugin,new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            {}
                            RegionEnteredEvent e = new RegionEnteredEvent(region, player, movement, event);
                            
                            plugin.getServer().getPluginManager().callEvent(e);
                        }
                    }, 1L);
                    regions.add(region);
                }
            }
        }
        
        Iterator<ProtectedRegion> itr = regions.iterator();
        while(itr.hasNext())
        {
            final ProtectedRegion region = itr.next();
            if (!appRegions.contains(region))
            {
                if (rm.getRegion(region.getId()) != region)
                {
                    itr.remove();
                    continue;
                }
                RegionLeaveEvent e = new RegionLeaveEvent(region, player, movement, event);

                plugin.getServer().getPluginManager().callEvent(e);

                if (e.isCancelled())
                {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                else
                {
                    Bukkit.getScheduler().runTaskLater(plugin,new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            RegionLeftEvent e = new RegionLeftEvent(region, player, movement, event);
                            
                            plugin.getServer().getPluginManager().callEvent(e);
                        }
                    }, 1L);
                    itr.remove();
                }
            }
        }
        playerRegions.put(player, regions);
        return false;
    }
}

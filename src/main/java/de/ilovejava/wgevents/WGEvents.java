package de.ilovejava.wgevents;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WGEvents extends JavaPlugin {
    private WGEventsListener listener;
    private WorldGuardPlugin wgPlugin;
    
    @Override
    public void onEnable()
    {
        wgPlugin = getWGPlugin();
        if (wgPlugin == null)
        {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        listener = new WGEventsListener(this, wgPlugin);
        
        getServer().getPluginManager().registerEvents(listener, wgPlugin);
    }
    
    private WorldGuardPlugin getWGPlugin()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        
        if (plugin == null || !(plugin instanceof WorldGuardPlugin))
        {
            return null;
        }
        
        return (WorldGuardPlugin) plugin;
    }
}

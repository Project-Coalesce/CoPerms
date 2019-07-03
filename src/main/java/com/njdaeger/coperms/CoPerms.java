package com.njdaeger.coperms;

import com.njdaeger.bci.defaults.CommandStore;
import com.njdaeger.coperms.configuration.CoPermsConfig;
import com.njdaeger.coperms.configuration.SuperDataFile;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.coperms.tree.Node;
import com.njdaeger.coperms.vault.Chat_CoPerms;
import com.njdaeger.coperms.vault.Permission_CoPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class CoPerms extends JavaPlugin {
    
    private CommandStore commandStore;
    private DataLoader dataLoader;
    private CoPermsConfig config;
    private SuperDataFile supers;
    
    @Override
    public void onEnable() {

        /*Set<String> testPermissions = new HashSet<>();
        testPermissions.add("worldedit.biome.info");
        testPermissions.add("-worldedit.brush.options.*");
        testPermissions.add("worldedit.brush.options.range");
        testPermissions.add("worldedit.region.*");
        testPermissions.add("-worldedit.region.hollow");
        System.out.println(Node.hasPermission(Node.getRootNodeTree(testPermissions), "worldedit.brush.options.range")); //expect true
        System.out.println(Node.hasPermission(Node.getRootNodeTree(testPermissions), "worldedit.region.select")); //Expect true
        System.out.println(Node.hasPermission(Node.getRootNodeTree(testPermissions), "worldedit.biome.info")); //Expect true
        System.out.println(Node.hasPermission(Node.getRootNodeTree(testPermissions), "worldedit.region.hollow")); //Expect false
        System.out.println(Node.hasPermission(Node.getRootNodeTree(testPermissions), "worldedit.brush.options.size")); //Expect false*/


        this.config = new CoPermsConfig(this);
        this.supers = new SuperDataFile(this);
        this.commandStore = new CommandStore(this);
        this.dataLoader = new DataLoader(this);
        new Metrics(this);

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Permission_CoPerms perms = new Permission_CoPerms(this);
            getServer().getServicesManager().register(Permission.class, perms, this, ServicePriority.High);
            getServer().getServicesManager().register(Chat.class, new Chat_CoPerms(this, perms), this, ServicePriority.High);
        } else {
            getLogger().warning("Vault is not hooked into CoPerms... Loading anyway...");
        }
        dataLoader.enable();
    }

    @Override
    public void onDisable() {
        getDataHolder().getGroups().forEach(Group::unload);
        dataLoader.disable();
    }

    /**
     * Gets CoPerms' configuration.
     *
     * @return CoPerm's configuration.
     */
    public CoPermsConfig getPermsConfig() {
        return config;
    }

    /**
     * Gets the plugin data holder.
     *
     * @return The data holder.
     */
    public DataHolder getDataHolder() {
        return dataLoader.getDataHolder();
    }

    /**
     * Returns the super data file.
     *
     * @return The super data file.
     */
    public SuperDataFile getSuperDataFile() {
        return supers;
    }
    
    /**
     * Gets this plugin's command store
     *
     * @return The command store
     */
    public CommandStore getCommandStore() {
        return commandStore;
    }
    
}

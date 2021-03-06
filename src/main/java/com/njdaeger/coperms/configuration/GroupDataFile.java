package com.njdaeger.coperms.configuration;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.pdk.config.ConfigType;
import com.njdaeger.pdk.config.Configuration;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class GroupDataFile extends Configuration {

    
    private final Group defaultGroup;
    private final Map<String, Group> groups;
    private final Map<Integer, String> groupIds;
    
    /**
     * Generates a groupdata file
     * @param plugin The plugin instance
     * @param world The world the datafile was originally created with.
     */
    public GroupDataFile(CoPerms plugin, World world) {
        super(plugin, ConfigType.YML, "worlds" + File.separator + world.getName() + File.separator + "groups");
        
        this.groups = new HashMap<>();
        this.groupIds = new HashMap<>();
        
        if (!hasSection("groups")) {
            addEntry("groups.default.permissions", Arrays.asList("ttb.generate", "ttb.undo", "ttb.redo"));
            addEntry("groups.default.inherits", Collections.emptyList());
            addEntry("groups.default.info.canBuild", true);
            addEntry("groups.default.info.prefix", "");
            addEntry("groups.default.info.suffix", "");
            addEntry("groups.default.info.rankid", 0);
        }
        
        getSection("groups").getKeys(false).forEach(k -> groups.put(k.toLowerCase(), new Group(plugin, this, k)));
        groups.values().forEach(Group::loadInheritance);
        groups.values().forEach(g -> groupIds.put(g.getRankID(), g.getName()));
        
        int[] arr = groupIds.keySet().stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(arr);
        
        this.defaultGroup = getGroup(arr[0]);

        save();
    }
    
    public void reloadGroups() {
        groups.values().forEach(group -> {
            group.preInheritanceLoad();
            group.loadInheritance();
        });
    }
    
    /**
     * Get a map of all the groups and their names from this file
     * @return A map of all the groups
     */
    public Map<String, Group> getGroups() {
        return groups;
    }
    
    /**
     * Get a group via its name.
     * @param name The name of the group to look for
     * @return The group if found, or else null.
     */
    public Group getGroup(@NotNull String name) {
        Validate.notNull(name, "Name cannot be null");
        return groups.get(name.toLowerCase());
    }
    
    /**
     * Get a group via its group id
     * @param id The group id
     * @return The group if found, or else null.
     */
    public Group getGroup(int id) {
        Validate.isTrue(id >= 0, "Group ID must be greater than or equal to 0. Given: ", id);
        if (groupIds.get(id) == null) return null;
        return getGroup(groupIds.get(id));
    }
    
    /**
     * Get the default group of this particular group file.
     * @return The default group
     */
    public Group getDefaultGroup() {
        return defaultGroup;
    }
}

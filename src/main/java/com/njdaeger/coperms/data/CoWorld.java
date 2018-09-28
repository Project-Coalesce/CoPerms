package com.njdaeger.coperms.data;

import com.njdaeger.coperms.configuration.GroupDataFile;
import com.njdaeger.coperms.configuration.UserDataFile;
import com.njdaeger.coperms.groups.Group;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CoWorld {

    private final World world;
    private final UserDataFile userData;
    private final GroupDataFile groupData;
    private final Map<UUID, CoUser> users;

    public CoWorld(World world, UserDataFile userData, GroupDataFile groupData) {
        this.world = world;
        this.users = new HashMap<>();
        this.userData = userData;
        this.groupData = groupData;
    }

    /**
     * Gets the world represented by this CoWorld
     *
     * @return The world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the name of the world
     *
     * @return The name of the world
     */
    public String getName() {
        return getWorld().getName();
    }

    /**
     * Gets the user data file this world uses
     *
     * @return The worlds user data file.
     */
    public UserDataFile getUserDataFile() {
        return userData;
    }

    /**
     * Gets the group data file this world uses
     *
     * @return The worlds group data file
     */
    public GroupDataFile getGroupDataFile() {
        return groupData;
    }
    
    /**
     * Gets an online user from this world.
     *
     * @param uuid The user to find
     * @return The user if the world has the user and if they're online. Null otherwise.
     */
    public CoUser getUser(UUID uuid) {
        return hasUser(uuid, true) ? users.get(uuid) : null;
    }

    /**
     * Gets an online user from this world.
     *
     * @param name The name of the user
     * @return The user if the world has the user and if they're online. Null otherwise.
     */
    public CoUser getUser(String name) {
        return hasUser(name, true) ? users.values().stream().filter(u -> u.getName().equalsIgnoreCase(name)).findFirst().orElse(null) : null;
    }

    /**
     * Check if this world has a user via uuid. The user can be online or offline.
     *
     * @param uuid The user to look for
     * @return True if the user ir or has been in this world, false otherwise.
     */
    public boolean hasUser(UUID uuid) {
        return hasUser(uuid, true);
    }
    
    /**
     * Check if this world has a user via uuid.
     *
     * @param uuid The user to look for
     * @param onlineOnly Whether to search online users only
     * @return True if the user exists (search is online only if specified)
     */
    public boolean hasUser(UUID uuid, boolean onlineOnly) {
        return onlineOnly ? users.containsKey(uuid) : userData.hasUser(uuid);
    }
    
    /**
     * Check if this world has a user via username. The user can be online or offline.
     *
     * @param name The user to look for
     * @return True if the user has been in this world, false otherwise.
     */
    public boolean hasUser(String name) {
        return hasUser(name, true);
    }
    
    /**
     * Check if this world has a user via username.
     *
     * @param name The user to look for
     * @param onlineOnly Whether to search for online users only
     * @return True if the user exists (search is online only if specified)
     */
    public boolean hasUser(String name, boolean onlineOnly) {
        return onlineOnly ? users.values().stream().anyMatch(u -> u.getName().equalsIgnoreCase(name)) : userData.hasUser(name);
    }
    
    /**
     * Gets a map of all the users in this world
     *
     * @return The worlds user map
     */
    public Map<UUID, CoUser> getOnlineUsers() {
        return users;
    }

    /**
     * Gets all the users that exist in this worlds userdata file
     *
     * @return All the users. Online or offline
     */
    public Set<UUID> getAllUsers() {
        return userData.getUsers();
    }

    /**
     * Gets a group from this world
     *
     * @param name The name of the group to find
     * @return The group
     */
    public Group getGroup(String name) {
        return groupData.getGroup(name);
    }

    /**
     * Gets a rank via ID
     *
     * @param id The id of the rank
     * @return The rank if it exists
     */
    public Group getGroup(int id) {
        return groupData.getGroup(id);
    }
    
    /**
     * Check if this world has a group
     * @param name The name of the group to look for
     * @return True if this world has the group, false otherwise.
     */
    public boolean hasGroup(String name) {
        return getGroup(name) != null;
    }
    
    /**
     * Gets a list of all the groups from this world.
     * @return All the groups in this world.
     */
    public List<Group> getGroups() {
        return new ArrayList<>(getGroupMap().values());
    }
    
    /**
     * Gets a map of all the groups in this world
     *
     * @return The group map
     */
    public Map<String, Group> getGroupMap() {
        return groupData.getGroupMap();
    }

    /**
     * Gets the default group of this world
     *
     * @return The worlds default group
     */
    public Group getDefaultGroup() {
        return groupData.getDefaultGroup();
    }
    
    /**
     * Loads a user into this world
     * @param user The user to load
     */
    public void addUser(CoUser user) {
        //The user data file is a database type of file. Data can only be written and read. Not deleted. We load the user into the file
        userData.loadUser(user.getUserID());
        users.put(user.getUserID(), user);
        user.load(this);
    }
    
    /**
     * Unloads a user from this world
     * @param user The user to unload
     */
    public void removeUser(CoUser user) {
        users.remove(user.getUserID());
        user.unload();
    }
}

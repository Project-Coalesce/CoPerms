package com.njdaeger.coperms.commands;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.commands.flags.WorldFlag;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.exceptions.GroupNotExistException;
import com.njdaeger.coperms.exceptions.UserNotExistException;
import com.njdaeger.coperms.exceptions.WorldNotExistException;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.pdk.command.CommandBuilder;
import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.OptionalFlag;

import static org.bukkit.ChatColor.*;

public final class UserCommands {

    public UserCommands(CoPerms plugin) {

        CommandBuilder.of("promote", "promo")
                .executor(this::promote)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .flag(new WorldFlag())
                .flag(new OptionalFlag("All users online and offline", "-a", "a"))
                .description("Promotes someone to the next rank.")
                .usage("/promote <user> [-w <world>] [-a]")
                .min(1)
                .max(2)
                .permissions("coperms.ranks.promote")
                .build().register(plugin);

        CommandBuilder.of("setrank", "setr", "setgroup")
                .executor(this::setRank)
                .completer((c) -> {
                    c.completionAt(0, CommandUtil::playerCompletion);
                    c.completionAt(1, CommandUtil::groupCompletion);
                })
                .flag(new WorldFlag())
                .flag(new OptionalFlag("All users online and offline", "-a", "a"))
                .description("Adds a user to a specified rank")
                .usage("/setrank <user> <rank> [-w <world>] [-a]")
                .min(2)
                .max(3)
                .permissions("coperms.ranks.setrank")
                .build().register(plugin);

        CommandBuilder.of("demote", "demo")
                .executor(this::demote)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .flag(new WorldFlag())
                .flag(new OptionalFlag("All users online and offline", "-a", "a"))
                .description("Demotes someone to the previous rank.")
                .usage("/demote <user> [-w <world>] [-a]")
                .min(1)
                .max(2)
                .permissions("coperms.ranks.demote")
                .build().register(plugin);

        CommandBuilder.of("setprefix", "prefix")
                .executor(this::setPrefix)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .description("Adds or removes a prefix from a user")
                .usage("/prefix <user> [prefix] [-w <world>] [-a]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("All users online and offline", "-a", "a"))
                .min(1)
                .permissions("coperms.variables.user.prefix")
                .build().register(plugin);

        CommandBuilder.of("setsuffix", "suffix")
                .executor(this::setSuffix)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .description("Adds or removes a suffix from a user")
                .usage("/suffix <user> [suffix] [-w <world>] [-a]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("All users online and offline", "-a", "a"))
                .min(1)
                .permissions("coperms.variables.user.suffix")
                .build().register(plugin);

        CommandBuilder.of("setgprefix", "gprefix", "setgpre")
                .executor(this::setGroupPrefix)
                .completer((c) -> c.completionAt(0, CommandUtil::groupCompletion))
                .flag(new WorldFlag())
                .description("Sets the prefix of a group.")
                .usage("/setgprefix <group> [prefix] [-w <world>]")
                .min(2)
                .permissions("coperms.variables.group.prefix")
                .build().register(plugin);

        CommandBuilder.of("setgsuffix", "gsuffix", "setgsuf")
                .executor(this::setGroupSuffix)
                .completer((c) -> c.completionAt(0, CommandUtil::groupCompletion))
                .flag(new WorldFlag())
                .description("Sets the suffix of a group.")
                .usage("/setgsuffix <group> [suffix] [-w <world>]")
                .min(2)
                .permissions("coperms.variables.group.suffix")
                .build().register(plugin);

        CommandBuilder.of("getrank", "rankof")
                .executor(this::getRank)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .description("Find the rank of a person.")
                .usage("/getrank <user> [-w <world>] [-a]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("All users online and offline", "-a", "a"))
                .min(1)
                .max(1)
                .permissions("coperms.ranks.getrank")
                .build().register(plugin);
    }

    //
    //
    //
    //

    //promote command
    private void promote(CommandContext context) throws PDKCommandException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Group group = world.getGroup(user.getGroup().getRankID() + 1);
        if (group == null) throw new GroupNotExistException();
        
        user.setGroup(world, group.getName());
        context.pluginMessage(AQUA + user.getName() + GRAY + " was promoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were promoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    //Demote command
    private void demote(CommandContext context) throws PDKCommandException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Group group = world.getGroup(user.getGroup().getRankID() - 1);
        if (group == null) throw new GroupNotExistException();
        
        user.setGroup(world, group.getName());
        context.pluginMessage(AQUA + user.getName() + GRAY + " was demoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were demoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    //
    //
    //
    //

    private void setRank(CommandContext context) throws PDKCommandException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Group group = world.getGroup(context.argAt(1));
        if (group == null) throw new GroupNotExistException();
        
        user.setGroup(world, group.getName());
        context.pluginMessage(AQUA + user.getName() + GRAY + " was added to group " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were added to group " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    private void getRank(CommandContext context) throws PDKCommandException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();

        String group;
        if (user.isOnline()) group = user.getGroup().getName();
        else group = user.getUserSection().getString("group");

        context.pluginMessage(AQUA + user.getName() + GRAY + " is in group " + AQUA + group + GRAY + " in world " + AQUA + world.getName());

    }

    //
    //
    //
    //

    //User prefix
    private void setPrefix(CommandContext context) throws PDKCommandException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();

        if (context.getLength() < 2) {
            user.setPrefix(null);
            context.pluginMessage(GRAY + "Prefix for " + AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your prefix has been disabled.");
            return;
        }
        user.setPrefix(context.joinArgs(1) + " ");
        context.pluginMessage(GRAY + "Prefix for " + AQUA + user.getName() + GRAY + " has been changed to " + AQUA + translate(user.getPrefix()));
        user.pluginMessage(GRAY + "Your prefix has been changed to " + AQUA + translate(user.getPrefix()));
    }

    //User suffix
    private void setSuffix(CommandContext context) throws PDKCommandException {
    
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
    
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();

        if (context.getLength() < 2) {
            user.setSuffix(null);
            context.pluginMessage(GRAY + "Suffix for " + AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your suffix has been disabled.");
            return;
        }
        user.setSuffix(" " + context.joinArgs(1));
        context.pluginMessage(GRAY + "Suffix for " + AQUA + user.getName() + GRAY + " has been changed to " + AQUA + translate(user.getSuffix()));
        user.pluginMessage(GRAY + "Your suffix has been changed to " + AQUA + translate(user.getSuffix()));
    }

    //
    //
    //
    //

    //Group prefix
    private void setGroupPrefix(CommandContext context) throws PDKCommandException {
    
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();
        
        if (context.getLength() < 3) {
            group.setPrefix(null);
            context.pluginMessage(GRAY + "Prefix for " + AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setPrefix(context.joinArgs(2) + " ");
        context.pluginMessage(GRAY + "Prefix for " + AQUA + group.getName() + GRAY + " has been changed to " + AQUA + group.getPrefix());
    }

    //Group suffix
    private void setGroupSuffix(CommandContext context) throws PDKCommandException {
    
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();
        
        if (context.getLength() < 3) {
            group.setSuffix(null);
            context.pluginMessage(GRAY + "Suffix for " + AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setSuffix(" " + context.joinArgs(2));
        context.pluginMessage(GRAY + "Suffix for " + AQUA + group.getName() + GRAY + " has been changed to" + AQUA + group.getSuffix());
    }

    //
    //
    //
    //

    private String translate(String message) {
        return translateAlternateColorCodes('&', message);
    }

}

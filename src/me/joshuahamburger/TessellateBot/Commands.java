package me.joshuahamburger.TessellateBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class Commands extends ListenerAdapter {
	
    HashMap<String, Game> games = new HashMap<>();
    ArrayList<String> commandsPrefix = new ArrayList<>(Arrays.asList("play", "continue", "stop"));
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User user = event.getAuthor();
        Message message = event.getMessage();
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        if (user.getId().equals(event.getJDA().getSelfUser().getId())) {
            List<MessageEmbed> embeds = message.getEmbeds();
            if (embeds.size() > 0) {
                MessageEmbed embed = embeds.get(0);
                if (embed.getTitle() != null && embed.getTitle().length() > 0) {
                    if (embed.getTitle().startsWith("Level")) {
                        message.addReaction("U+2B05").queue();
                        message.addReaction("U+27A1").queue();
                        message.addReaction("U+2B06").queue();
                        message.addReaction("U+2B07").queue();
                        message.addReaction("U+21A9").queue();
                        message.addReaction("U+1F504").queue();
                        MessageEmbed.Footer footerObject = embed.getFooter();
                        if (footerObject != null) {
                            String footer = footerObject.getText();
                            if (footer != null) {
                                String playerId = footer.substring(8, footer.length());
                                if (games.containsKey(playerId)) {
                                    Game game = games.get(playerId);
                                    game.setGameMessage(message);
                                }
                            }
                        }
                    }
                }
            }
            return;
        }
        String[] args = message.getContentRaw().split("\\s+");
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (((arg.length() > 0 && Character.toString(arg.charAt(0)).equals(Main.prefix) && commandsPrefix.contains(arg.substring(1))))) {
                if (!hasPermissions(guild, channel)) {
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                Game game;
                if (!games.containsKey(user.getAsTag())) {
                    game = new Game(user);
                    games.put(user.getAsTag(), game);
                } else game = games.get(user.getAsTag());
                String userInput = arg;
                if (userInput.substring(0, 1).equals(Main.prefix)) userInput = userInput.substring(1);
                game.run(event.getGuild(), channel, userInput);
                if (userInput.equals("stop")) games.remove(user.getAsTag());
                if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) message.delete().queue();
            } else if ((arg.equals(Main.prefix + "info")) || (message.getMentionedUsers().size() > 0 && message.getMentionedUsers().get(0).equals(event.getJDA().getSelfUser()))) {
                if (!hasPermissions(guild, channel)) {
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                channel.sendMessage(info(event.getGuild()).build()).queue();
                if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) message.delete().queue();
            }
        }
    }

    private static final Collection<Permission> requiredPermissions = Arrays.asList(Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_WRITE);

    private boolean hasPermissions(Guild guild, TextChannel channel) {
        Member self = guild.getSelfMember();
        if (self.hasPermission(Permission.ADMINISTRATOR)) return true;
        return self.hasPermission(channel, requiredPermissions);
    }

    private void sendInvalidPermissionsMessage(User user, TextChannel channel) {
        if (channel.canTalk()) {
            StringBuilder requiredPermissionsDisplay = new StringBuilder();
            for (Permission requiredPermission : requiredPermissions) {
                requiredPermissionsDisplay.append("`").append(requiredPermission.getName()).append("`, ");
            }
            if (requiredPermissionsDisplay.toString().endsWith(", "))
                requiredPermissionsDisplay = new StringBuilder(requiredPermissionsDisplay.substring(0,
                        requiredPermissionsDisplay.length() - 2));
            channel.sendMessage(user.getAsMention() + ", I don't have enough permissions to work properly.\nMake " +
                    "sure I have the following permissions: " + requiredPermissionsDisplay + "\nIf you think this is "
                    + "an error, please contact a server administrator.").queue();
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        User user = event.getUser();
        if (user.isBot()) {
            return;
        }
        Guild guild = event.getGuild();
        MessageReaction reaction = event.getReaction();
        TextChannel channel = event.getChannel();
        channel.retrieveMessageById(event.getMessageId()).queue(message -> {
            if (message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                Game game;
                if (!games.containsKey(user.getAsTag())) {
                    game = new Game(user);
                    games.put(user.getAsTag(), game);
                } else game = games.get(user.getAsTag());
                boolean reactionCommand = true;
                String userInput = "";
                switch (event.getReactionEmote().toString()) {
                    case "RE:U+2b05":
                        userInput = "left";
                        break;
                    case "RE:U+27a1":
                        userInput = "right";
                        break;
                    case "RE:U+2b06":
                        userInput = "up";
                        break;
                    case "RE:U+2b07":
                        userInput = "down";
                        break;
                    case "RE:U+1f504":
                        userInput = "reset";
                        break;
                    case "RE:U+21a9":
                    	userInput = "undo";
                    	break;
                    default:
                        reactionCommand = false;
                        break;
                }
                if (reactionCommand) game.run(guild, channel, userInput);
                reaction.removeReaction(user).queue();
            }
        });
    }

    EmbedBuilder info(Guild guild) {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("Wrap Bot");
        info.setThumbnail(guild.getSelfMember().getUser().getAvatarUrl());
        info.setDescription("Wrap bot is a bot that lets you play the puzzle game Wrap.");
        info.setColor(0xd5bc6c);
        info.addField("Instructions", "You play as :sauropod:\n"
        		+ "You move around with :arrow_left:, :arrow_right:, :arrow_up:, and :arrow_down:.\n"
                + "You can use :leftwards_arrow_with_hook: to undo and :arrows_counterclockwise: to reset.\n"
        		+ "Your goal is to fill in every block in the map, and then return to the starting.\n"
                + "You can teleport across the level by moving over the edge.\n"
        		+ "Be careful though, you can't walk over your own trail!\n"
                + "Although you can walk over the starting tile.", false);
        info.addField("Commands",
                "``" + Main.prefix + "play`` can be used to start a game if you are not " + "currently in " + "one.\n``" + Main.prefix + "stop`` can be used to stop your active game at any " + "time.\n``" + Main.prefix + "info`` provides some useful details about the bot and " + "rules of the game", false);
        info.addField("Add to your server",
        			  "https://top.gg/bot/713635251703906336", false);
        info.setFooter("created by HamburgJ", "https://avatars1.githubusercontent.com/u/68581670?s=460&u=88d50cf11f2147662a814d00cae10c9f38728720&v=4");
        return info;
    }

    public static void sendGameEmbed(MessageChannel channel, String game, User user) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Level");
        embed.setDescription(game);
        embed.addField("", ":arrow_left:, :arrow_right:, :arrow_up:, :arrow_down: to move\n" + ":leftwards_arrow_with_hook: to undo\n" +":arrows_counterclockwise: to reset", false);
        embed.setFooter("Player: " + user.getAsTag(), user.getAvatarUrl());
        channel.sendMessage(embed.build()).queue();
    }

    public static void updateGameEmbed(Message message, String game, User user) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Level");
        embed.setDescription(game);
        embed.addField("", ":arrow_left:, :arrow_right:, :arrow_up:, :arrow_down: to move\n" + ":leftwards_arrow_with_hook: to undo\n" +":arrows_counterclockwise: to reset", false);
        embed.setFooter("Player: " + user.getAsTag(), user.getAvatarUrl());
        message.editMessage(embed.build()).queue();
    }

    public static void sendWinEmbed(Guild guild, Message message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("You win!");
        embed.setDescription("Type ``" + Main.prefix + "continue`` to continue" + " or ``" + Main.prefix + "stop`` to quit ");
        embed.setFooter("You can also press any reaction to continue.");
        message.editMessage(embed.build()).queue();
    }
}
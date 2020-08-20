package me.joshuahamburger.TessellateBot;

import net.dv8tion.jda.api.entities.*;

public class Game {
    long gameMessageID;
    long channelID;
    User user;
    String playerEmote = ":flushed:";
    public boolean gameActive = false;
    public int level = 1;
    int width = 9;
    int height = 6;
    String txt ="";

    public Game(User user) {
        this.user = user;
    }

    public void setPlayerEmote(String emote) {
        playerEmote = emote;
    }

    public void setGameMessage(Message gameMessage) {
        // To avoid an Unknown Message error, we will store the IDs and retrieve the Channel object when needed.
        gameMessageID = gameMessage.getIdLong();
        channelID = gameMessage.getChannel().getIdLong();
    }

    public void newGame(MessageChannel channel) {
        if (!gameActive) {
            level = 1;
            width = 9;
            height = 6;
            gameActive = true;
            Commands.sendGameEmbed(channel, String.valueOf(level), "ye", user);
        }
    }

    public void run(Guild guild, TextChannel channel, String userInput) {
        if (userInput.equals("stop") && gameActive) {
            channel.sendMessage("Thanks for playing, " + user.getAsMention() + "!").queue();
            gameActive = false;
        }
        
        
        
        if (userInput.equals("play") && !gameActive) {
            newGame(channel);
        } else if (gameActive) {
                String direction = userInput;
                if (direction.equals("up") || direction.equals("w")) {
                	txt = "up";
                } else if (direction.equals("down") || direction.equals("s")) {
                	txt = "down";
                } else if (direction.equals("left") || direction.equals("a")) {
                	txt = "left";
                } else if (direction.equals("right") || direction.equals("d")) {
                	txt = "right";
                } else if (direction.equals("r")) {
                	txt = "r";
                }
                    TextChannel textChannel = Main.getJDA().getTextChannelById(channelID);
                    if (textChannel != null) {
                        textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> Commands.updateGameEmbed(gameMessage, String.valueOf(level), txt, user));
                    }
            /*
            if (grid.hasWon()) {
                level += 1;
                if (width < 13) {
                    width += 2;
                }
                if (height < 8) {
                    height += 1;
                }
                TextChannel textChannel = Main.getJDA().getTextChannelById(channelID);
                if (textChannel != null) {
                    textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> Commands.sendWinEmbed(guild,
                            gameMessage, String.valueOf(level)));
                }
                grid = new Grid(width, height, level, playerEmote);
            }
            */
        }
    }
}
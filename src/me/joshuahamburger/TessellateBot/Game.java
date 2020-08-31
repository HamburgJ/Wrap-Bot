package me.joshuahamburger.TessellateBot;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Game {
    long gameMessageID;
    long channelID;
    User user;
    public boolean gameActive = false;
    Level lvl;
    Level currentLevel;
    int levelIndex = 0;
    ArrayList<Level> levels;

    public Game(User user) {
        this.user = user;
    }

    public void setGameMessage(Message gameMessage) {
        // To avoid an Unknown Message error, we will store the IDs and retrieve the Channel object when needed.
        gameMessageID = gameMessage.getIdLong();
        channelID = gameMessage.getChannel().getIdLong();
    }

    private void setUpLevels() {
        levels = new ArrayList<>();
        
        lvl = new Level(3, 1, 1);
        lvl.addWalls(new int[][] {{2,2}, {0,2}});
        levels.add(lvl);
        
        lvl = new Level(7, 3, 3);
        lvl.addWalls(new int[][] {{1,1},{1,2},{2,1},{2,2},
			  					  {4,1},{4,2},{5,1},{5,2},
			  					  {1,4},{2,4},{1,5},{2,5},
			  					  {4,4},{4,5},{5,4},{5,5}});
        levels.add(lvl);
        lvl = new Level(9, 4, 4);
        lvl.addWalls(new int[][] {{0,0},{0,1},{1,0},{2,2},
        						  {0,7},{0,8},{1,8},{6,2},
        						  {7,0},{8,0},{8,1},{2,6},
        						  {8,8},{7,8},{8,7},{6,6}});
        levels.add(lvl);
        
        lvl = new Level(9, 4, 4);
        lvl.addWalls(new int[][] {{1,4}, {7,4},
        						  {0,0},{0,1},{0,2},{1,0},
        						  {0,8},{0,7},{1,8},{0,6},
        						  {8,0},{7,0},{8,1},{8,2},
        						  {8,8},{8,7},{7,8},{8,6}});
        levels.add(lvl);
        
        lvl = new Level(7, 3, 3);
        lvl.addWalls(new int[][] {{0,0},{1,2},
        						  {0,6},{4,1},
        						  {6,0},{2,5},
        						  {6,6},{5,4}});
        levels.add(lvl);
        
        lvl = new Level(7, 3, 3);
        lvl.addWalls(new int[][] {{0,1},{0,3},{0,5},
        						  {6,1},{6,3},{6,5},
        						  {2,2},{2,4},{4,2},{4,4}});
        levels.add(lvl);
        
        lvl = new Level(7, 3, 3);
        lvl.addWalls(new int[][] {{1,2},{1,5},{1,6},
        						  {5,0},{5,1},{5,4}});
        levels.add(lvl);
        
        lvl = new Level(7, 3, 3);
        lvl.addWalls(new int[][] {{3,0},{3,1},
        						  {3,5},{3,6},
        						  {1,3},{2,3},
        						  {4,3},{5,3}});
        levels.add(lvl);
        
        lvl = new Level(7, 3, 3);
        lvl.addWalls(new int[][] {{1,1},{1,5},
        						  {5,1},{5,5},
        						  {0,3},{3,0},
        						  {6,3},{3,6}});
        levels.add(lvl);
        
        lvl = new Level(6, 1, 1);
        lvl.addWalls(new int[][] {{0,0},{0,5},{5,0},
        						  {2,2},{3,3},{4,4}});
        levels.add(lvl);
        
        lvl = new Level(3, 1, 1);
        lvl.addWalls(new int[][] {{2,2}, {0,2}});
        levels.add(lvl);
    }
    
    public void newGame(MessageChannel channel) {
        if (!gameActive) {
            gameActive = true;
            setUpLevels();
            
            currentLevel = levels.get(0);
            Commands.sendGameEmbed(channel, currentLevel.getString(), user);
         
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
            	currentLevel.move(0, -1);
            } else if (direction.equals("down") || direction.equals("s")) {
            	currentLevel.move(0, 1);
            } else if (direction.equals("left") || direction.equals("a")) {
            	currentLevel.move(-1, 0);
            } else if (direction.equals("right") || direction.equals("d")) {
            	currentLevel.move(1, 0);
            } else if (direction.equals("reset")) {
            	currentLevel.reset();
            } else if (direction.equals("undo")) {
            	currentLevel.undo();
            }

            TextChannel textChannel = Main.getJDA().getTextChannelById(channelID);
            if (textChannel != null) {
                textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> Commands.updateGameEmbed(gameMessage, currentLevel.getString(), user));
            }
            if (currentLevel.hasWon()) {
                if (textChannel != null) {
                	if (levels.size() == levelIndex+1) {
                		textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> Commands.sendWinAllEmbed(guild,
                				gameMessage));
                	} else {
                		textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> Commands.sendWinEmbed(guild,
                				gameMessage));
                	}
                }
                if (levels.size() == levelIndex+1) {
                	levelIndex = 0;
                	setUpLevels();
                	currentLevel = levels.get(levelIndex);
                } else {
                	levelIndex++;
                	currentLevel = levels.get(levelIndex);
                }
            }
        }
    }
}
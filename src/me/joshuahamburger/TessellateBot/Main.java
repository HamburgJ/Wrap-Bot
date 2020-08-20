package me.joshuahamburger.TessellateBot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class Main {
	public static JDA jda;
	public static String prefix = "!";
	
	public static void main(String[] args) throws LoginException {
		String token = null;
        try {
            File tokenFile = Paths.get("token.txt").toFile();
            if (!tokenFile.exists()) {
                System.out.println("[ERROR] Could not find token.txt file");
                System.out.println("[ERROR] Please create a file called \"token.txt\" in the same folder as the jar " + "file and paste in your bot token.");
                return;
            }
            token = new String(Files.readAllBytes(tokenFile.toPath()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (token == null) return;
		jda = JDABuilder.createDefault(token).build();
		
		jda.getPresence().setStatus(OnlineStatus.ONLINE);
		jda.addEventListener(new Commands());
		
	}
	
	public static JDA getJDA() {
        return jda;
    }
}

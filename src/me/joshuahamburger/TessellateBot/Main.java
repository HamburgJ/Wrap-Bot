package me.joshuahamburger.TessellateBot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class Main {
	public static JDA jda;
	public static String prefix = "!";
	
	public static void main(String[] args) throws LoginException {
		String token = "NzQ0Mjg5NjQyOTQwOTg5NTAw.XzhDvw.5i-6dIUlDX1EI45EhMIC8weOuQ0";
		jda = JDABuilder.createDefault(token).build();
		
		jda.getPresence().setStatus(OnlineStatus.ONLINE);
		jda.addEventListener(new Commands());
		
	}
	
	public static JDA getJDA() {
        return jda;
    }
}

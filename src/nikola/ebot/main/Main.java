/*
 * Created with IntelliJ IDEA.
 * Author: Nikola Kovacevic
 * Licence: MIT
 *
 * Copyright (c) 2013 Nikola Kovacevic <nikolak@outlook.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nikola.ebot.main;

import nikola.ebot.main.Commands.*;
import nikola.ebot.main.Modules.YTtitle;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main extends PircBot {
    // Store the commands
    protected static final String version = "1.0.2";
    private final List<BotCommand> commands;
    private final List<ModuleInterface> modules;
    private SupaVector ADMINS = new SupaVector();
    private SupaVector banned = new SupaVector();//TODO: Figure out a way to use one thing for bans and user registration
    private String bot_server = null;
    private String bot_channels = null;
    private String bot_password = "";
    private String admin_password = "";

    public Main(Properties config) {
        commands = new ArrayList<BotCommand>();
        modules = new ArrayList<ModuleInterface>();
        // Add all commands
        commands.add(new info());
        commands.add(new reg());
        commands.add(new fight());
        commands.add(new avatar());
        commands.add(new link());
        commands.add(new medals());
        commands.add(new donate());
        commands.add(new join());
        commands.add(new part());
        //Add all modules
        modules.add(new YTtitle());

        String bot_nick = config.getProperty("bot_nickname", "erepBot");
        bot_server = config.getProperty("server", "0.0.0.0");
        bot_channels = config.getProperty("channel", null);
        bot_password = config.getProperty("bot_nickname_password", "");
        admin_password = config.getProperty("admin_password", "fznxRMrW8hWK");
        if (admin_password.equals("fznxRMrW8hWK")) {
            log("\n[WARNING] USING DEFAULT ADMIN LOGIN PASSWORD! YOU SHOULD USE A CUSTOM ONE!\n");

        }

        setName(bot_nick);
        setAutoNickChange(true); //TODO: Add way of trying to get originally intended nickname if it gets freed up
        setVerbose(true);
        setFinger("Get your hand off of me!");
        setLogin("erepBot");
        setVersion("erepublik bot ver " + version + " by <nikolak@outlook.com>");
        try {
            connectAndJoin();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(banned.read("banned.users")){
            log("\n[INFO] Banned list loaded...");
        }else{
            log("\n[INFO] Banned file not found, creating new (empty) one");
            createConfigFiles conf = new createConfigFiles();
            conf.createBannedFile();
        }
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream("bot.ini"));
        } catch (IOException e) {
            createConfigFiles conf = new createConfigFiles();
            System.out.print("Configuration files not found\nTrying to create default ones...\n");
            conf.createFiles();
            System.out.print("Edit bot.ini file with your settings and run the bot again\n");
            System.err.println("Error: Error loading config file...\n");
            System.exit(0);
        }

        new Main(config);
    }

    protected void connectAndJoin() throws IrcException, IOException {
        connect(bot_server);
        for (String channel : bot_channels.split(",")) {
            if (validChannel(channel) && channel.startsWith("#")) {
                joinChannel(channel.trim());
            } else {
                if(validChannel("#"+channel.trim())){
                    joinChannel("#" + channel.trim());
                }else{
                    log("\n[ERROR] Invalid channel"+ channel);
                }

            }
        }
        if (bot_password.trim().length() > 0) {
            sendMessage("NickServ", "IDENTIFY " + bot_password);//TODO:Find a more sutable way of identifying. This works only on nickserv
        }

    }

    //a channel is valid if it conforms to the IRC RFC naming requirements
    private boolean validChannel(String channel) {
        return channel.matches("(?:#|&)[^ \\a\\x00\\r\\n,]+");
    }
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        if (ADMINS.contains(oldNick.toLowerCase())) {
            ADMINS.remove(oldNick.toLowerCase());
            ADMINS.add(newNick.toLowerCase());
            log("[INFO] Admin nickname changed from " + oldNick + " to " + newNick);
        }
    }

    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        if (ADMINS.contains(sourceNick.toLowerCase())) {
            ADMINS.remove(sourceNick.toLowerCase());
            log("[INFO] Admin removed: " + sourceNick);
        }
    }

    public void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (message.trim().equals(admin_password)) {
            if (ADMINS.contains(sender.toLowerCase())) {
                sendNotice(sender, "You are already authenticated");
            } else {
                ADMINS.add(sender.toLowerCase());
                log("[INFO] New admin added! " + sender);
                sendNotice(sender, "Password is accepted, you are now authenticated as admin.");
            }
        }
    }


    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        //TODO: Implement throttler to prevent abuse
        message = message.toLowerCase();
        if (banned.contains(sender.toLowerCase())) {
            return;
        }
        boolean is_admin = ADMINS.contains(sender.toLowerCase());

        if ((is_admin) & (message.startsWith(".ban") || (message.startsWith(".unban")))) {
            if (message.startsWith(".ban")) botBan(channel, message);
            if (message.startsWith(".unban")) botUnban(channel, message);
        }

        for (BotCommand command : commands) {
            if (message.startsWith(command.getCommandName())) {
                command.handleMessage(this, channel, sender.toLowerCase(),
                        message.replace(command.getCommandName(), "").trim(), is_admin);
            }
        }
        for (ModuleInterface module : modules) {
            if (message.startsWith(module.getCommandName())) {
                module.handleMessage(this, channel, sender.toLowerCase(),
                        message.replace(module.getCommandName(), "").trim());
            } else if (message.contains(module.getCommandName()) & message.contains("youtube")) {
                //Needed for YTtitle module
                module.handleMessage(this, channel, sender.toLowerCase(),
                        message.replace(module.getCommandName(), "").trim());
            }
        }
        if (message.replace(":", "").replace(",", "").equalsIgnoreCase(getNick() + " help")) {
            sendNotice(sender, "Note: Commands that accept nickname as parameter ONLY work if the nickname" +
                    " is linked with valid Citizen ID in this bot. This can be done using .reg command explained below.");
            for (BotCommand command : commands) {
                if (command.getHelp()!=null){
                    sendNotice(sender, command.getHelp());
                }
            }
        }
    }

    // We were disconnected, try to reconnect 10 times then give up.
    public void onDisconnect() {  //TODO: Test this

        int tries = 0;
        while (tries < 10 && !isConnected()) {
            try {
                connectAndJoin();
            } catch (Exception e) {
                System.out.println("Connection attempt " + tries + " failed.");
                tries++;
                if (tries == 10) System.exit(0);
            }
        }
    }

    public void botBan(String channel, String params) {
        String user_to_ban = params.split(" ")[1].toLowerCase().trim();
        if (!banned.contains(user_to_ban)) {
            banned.add(user_to_ban);
            banned.write("banned.users");
            sendMessage(channel, "User is now banned");
        } else {
            sendMessage(channel, "User is already banned");
        }
    }

    public void botUnban(String channel, String params) {
        String user_to_ban = params.split(" ")[1].toLowerCase().trim();
        if (banned.contains(user_to_ban)) {
            banned.remove(user_to_ban);
            banned.write("banned.users");
            sendMessage(channel, "User is no longer banned.");
        } else {
            sendMessage(channel, "User is not banned");
        }
    }

    @Override //TODO: Add proper logging & log saving combined with exception saving for finding bugs
    public void log(String line) {
        System.out.print(line + "\n");
    }
}
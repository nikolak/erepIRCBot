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

package nikola.ebot.main.Commands;

import nikola.ebot.main.BotCommand;
import nikola.ebot.main.userRegistration;
import org.jibble.pircbot.PircBot;

import java.io.IOException;

public class reg implements BotCommand {
    userRegistration config = new userRegistration();

    public String getCommandName() {
        return ".reg";
    }

    public String getHelp() {
        return String.format("\u0002%s\u0002 - Register/Link your current nickname to specified" +
                " Citizen ID which enables you to use most of the commands without " +
                "providing Citizen ID or your own nickname. Usage: .reg <citizen ID>. " +
                "To un-link your nickname type .reg remove", getCommandName());
    }

    public void handleMessage(PircBot bot, String channel, String sender, String message, Boolean admin) {

        if(message.equals("remove")){
            try {
                config.register(sender.toLowerCase(),null);
            } catch (IOException e) {
                bot.sendMessage(channel, "I can't remove you at the moment. Error: "+e.toString());
            }
            bot.sendMessage(channel,sender+": ID removed");
        }else{
            if(!message.trim().matches("[0-9]+")){
                bot.sendMessage(channel,sender+": You need to enter your citizen ID (only numbers)");

            }else{
                try {
                    config.register(sender.toLowerCase(),message.trim());
                } catch (IOException e) {
                    bot.sendMessage(channel, "I can't add you at the moment. Error: "+e.toString());
                }
                bot.sendMessage(channel,sender+": Successfully registered your ID with your nickname!");
            }
        }

    }
}

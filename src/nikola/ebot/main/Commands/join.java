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
import org.jibble.pircbot.PircBot;

import java.util.Arrays;

public class join implements BotCommand {
    @Override
    public String getCommandName() {
        return ".join";
    }

    public String getHelp() {
        return null;
    }

    @Override
    public void handleMessage(PircBot bot, String channel, String sender, String message, Boolean admin) {
        String to_join="#"+message.split(" ")[0].trim().replace("#","");
        if(admin){
            if (to_join.matches("(?:#|&)[^ \\a\\x00\\r\\n,]+")) {
                if (Arrays.asList(bot.getChannels()).contains(to_join)){
                    bot.sendMessage(channel, "I'm already there as far as I know...");
                }else{
                    bot.sendMessage(channel, "Trying to join "+to_join);
                    bot.joinChannel(to_join);
                }
            }else{
                bot.sendMessage(channel,"Invalid channel name...");
            }


        }
    }
}

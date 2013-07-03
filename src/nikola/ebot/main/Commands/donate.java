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

import eapi.Citizen;
import nikola.ebot.main.BotCommand;
import nikola.ebot.main.userRegistration;
import org.jibble.pircbot.PircBot;

public class donate implements BotCommand {

    public String getCommandName() {

        return ".donate";
    }

    public String getHelp() {
        return String.format("\u0002%s\u0002 - Returns items and/or money donation link for specified user. " +
                "Usage: .donate <Citizen ID or IRC nickname> -m (for money link) or -i (for items link)." +
                "Sender nickname is used as nickname if no parameters are specified", getCommandName());
    }

    private String makeResponse(Citizen citizen,Boolean items, Boolean money){
        String out=String.format("%s [%s] :: ", citizen.name,citizen.citizen_id);

        if(!items & !money){
            return "You have to specify which donation link you want. " +
                    "For items use '-i' in your command, for money use '-m'";
        }
        if(items) out+=String.format("\u0002Items donation:\u0002 %s  ", citizen.items_donate);
        if(money) out+=String.format("\u0002Money donation:\u0002 %s  ", citizen.money_donate);

        return out;
    }

    public void handleMessage(PircBot bot, String channel, String sender, String message, Boolean admin) {
        // Channel - Can be upper or lowercase
        // Sender - Always lowercase
        // Message - Always lowercase
        userRegistration userRegistration = new userRegistration();
        String user_ID;
        Boolean items=message.contains("-i");
        Boolean money=message.contains("-m");
        if(items)message=message.replace("-i","");
        if(money)message=message.replace("-m","");

        if (message.trim().equals("")) {
            if (userRegistration.get(sender.toLowerCase()) == null) {
                bot.sendMessage(channel, sender + ": You need to register first, type .reg <your citizen ID>");
                return;
            } else {
                user_ID = userRegistration.get(sender.toLowerCase());
            }
        } else {
            user_ID = message.split(" ")[0].trim().toLowerCase();
        }

        Citizen user;
        if (user_ID.matches("[0-9]+")) {
            user = new Citizen().fromID(user_ID);
            if (user==null | user.Error!=null){
                if(user.Error!=null) bot.sendMessage(channel,user.Error);
                else bot.sendMessage(channel,"Unknown citizen ID: "+user_ID);
            } else {
                bot.sendMessage(channel, makeResponse(user,items,money));
            }
        } else {
            if (userRegistration.get(user_ID) == null) {
                bot.sendMessage(channel, sender + ": Username not registered with bot.");
            } else {
                user = new Citizen().fromID(userRegistration.get(user_ID));
                if (user==null | user.Error!=null){
                    if(user.Error!=null) bot.sendMessage(channel,user.Error);
                    else bot.sendMessage(channel,"Unknown citizen ID: "+user_ID);
                } else {
                    bot.sendMessage(channel, makeResponse(user,items,money));
                }
            }
        }
    }
}

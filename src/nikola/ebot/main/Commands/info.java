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

public class info implements BotCommand {

    public String getCommandName() {
        return ".lp";
    }

    public String getHelp() {
        return String.format("\u0002%s\u0002 - Display user info such as location, strength etc. " +
                "Usage: .lp <Citizen ID or IRC nickname>" +
                "If no parameters are specified sender nickname is used.", getCommandName());
    }

    private String returnInfo(Citizen user){
        /*
        > .lp [20:41:09] <&e-bot> :: Unihorn [1649964]
        :: Level 61 (183764) :: Strength 26,994.371 :: Birthday Jul 16, 2009 ::
        National Rank 217 :: Rank God of War * (167,054,899) ::
        Location Great Poland - Poland :: Citizenship Poland  ::
        Newspaper Unihorn Newspapers :: MU Tesla Troopers (971)
        */
        String user_info = "";
        user_info+=String.format(":: \u0002%s\u0002 [%s] :: \u0002Level\u0002 %s (%s)" +
                                 " :: \u0002Strength\u0002 %s :: \u0002Birthday\u0002 %s :: ",
                                user.name,user.citizen_id,user.level,user.experience_points,
                                user.strength,user.birthday);
        user_info+=String.format("\u0002National rank\u0002 %s :: \u0002Rank\u0002 %s (%s) :: " +
                                "\u0002Location\u0002 %s - %s :: \u0002Citizenship\u0002 %s :: ",
                                user.national_rank,user.full_rank,user.rank_points,user.residence_region_name,
                                user.residence_country_name,user.citizenship_country_name);
        if(user.party_member){
            user_info+="\u0002Party\u0002"+user.party_name;
            if (user.party_leader) user_info+=" (Party President)";
            user_info+=" :: ";
        }

        if(user.owns_newspapers){
            user_info+="\u0002Newspaper\u0002 "+user.newspaper_name+" :: ";
        }
        if(user.unit_member){
            user_info+=String.format(" \u0002MU\u0002 %s (%s)",user.unit_name,user.unit_id);
            if (user.unit_leader) user_info+=" (Leader)";
        }

        return user_info;

    }

    public void handleMessage(PircBot bot, String channel, String sender, String message, Boolean admin) {
        // Channel - Can be upper or lowercase
        // Sender - Always lowercase
        // Message - Always lowercase
        String user_ID;
        Citizen user;
        userRegistration userRegistration = new userRegistration();
        if(message.trim().equals("")){
            if(userRegistration.get(sender.toLowerCase())==null){
                bot.sendMessage(channel,sender+": You need to register first, type .reg <your citizen ID>");
                return;
            }else{
                user_ID=userRegistration.get(sender.toLowerCase());
            }
        } else{
            user_ID=message.split(" ")[0].trim().toLowerCase();
        }

        if(user_ID.matches("[0-9]+")){
            user = new Citizen().fromID(user_ID);
            if (user==null | user.Error!=null){
                if(user.Error!=null) bot.sendMessage(channel,user.Error);
                else bot.sendMessage(channel,"Unknown citizen ID: "+user_ID);
            }else{
                bot.sendMessage(channel,returnInfo(user));
            }
        }else{
            if(userRegistration.get(user_ID)==null){
                bot.sendMessage(channel,sender+": Username not registered with bot.");
            }else{
                user = new Citizen().fromID(userRegistration.get(user_ID));
                if (user==null | user.Error!=null){
                    if(user.Error!=null) bot.sendMessage(channel,user.Error);
                    else bot.sendMessage(channel,"Unknown citizen ID: "+user_ID);
                }else{
                    bot.sendMessage(channel,returnInfo(user));
                }
            }
        }
    }
}
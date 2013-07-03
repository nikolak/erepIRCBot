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

public class template implements BotCommand {

    public String getCommandName() {

        return "i"; //String that used with prefix in Main will trigger handleMessage
    }

    @Override
    public String getHelp() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleMessage(PircBot bot, String channel, String sender, String message, Boolean admin) {
        // Channel - Can be upper or lowercase
        // Sender - Always lowercase
        // Message - Always lowercase

        //Get citizen ID from username/citizen name if registered with bot
        //Or use citizen ID directly

        userRegistration userRegistration = new userRegistration(); //Citizen name<>Citizen ID handler
        String user_ID=null;//user_ID initialized

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

        Citizen user;
        if(user_ID.matches("[0-9]+")){
            user = new Citizen().fromID(user_ID); //Citizen will try to load data from api using specified ID
                                                  //If you're not using API you could practically skip this if
                                                  //Statement and just use else
            if (user==null){                      //Specific if/else statement only valid in Citizen class
                bot.sendMessage(channel,sender+": Unknown Citizen ID:"+user_ID);
            }else{
                bot.sendMessage(channel,sender+": "+user.name); //name is used as an example
            }
        }else{
            //Not a number was specified, i.e. Citizen ID is specified
            if(userRegistration.get(user_ID)==null){     //This name doesn't exist in bots database
                bot.sendMessage(channel,sender+": Username not registered with bot.");
            }else{
                //Name/ID pair exists in database; The following if/else is only used with Citizen class
                user = new Citizen().fromID(userRegistration.get(user_ID));
                if (user==null){
                    bot.sendMessage(channel,sender+": Unknown Citizen ID:"+user_ID);
                }else{
                    bot.sendMessage(channel,user.name); //name is used as an example
                }
            }
        }
    }
}

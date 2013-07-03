/*
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nikola
 * Date: 6/3/13
 * Time: 9:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class fight implements BotCommand {

    private static String fightCalc(Citizen user, Boolean natural_bonus, Integer fights) {
    /*
    # I = 10 × (1 +S/400) × (1 +R/5) × (1 +FirePower/100)
    # I: Influence on the hit
    # R: Value of the Rank
    # S: Strength
    */
        double strength = Double.valueOf(user.strength.toString());
        double rank_value = Double.valueOf(user.rank_value);
        Map<Integer, Double> weapons = new HashMap<Integer, Double>();
        Double s = strength;
        Double r = rank_value;
        String out = String.format("%s [%s] ::  \u0002Influence:\u0002 Fights: %s [NE Bonus: %s] :: ", user.name,
                user.citizen_id, fights, natural_bonus);
        weapons.put(7, 200.0);
        weapons.put(6, 120.0);
        weapons.put(5, 100.0);
        weapons.put(4, 80.0);
        weapons.put(3, 60.0);
        weapons.put(2, 40.0);
        weapons.put(1, 20.0);
        weapons.put(0, 0.0);

        for (int x = 0; x < 8; x++) {
            Double p1, p2, p3;
            Double fire_power = Double.valueOf(weapons.get(x).toString());
            Double i = 0.0;
            i = 10 * (1 + s / 400) * (1 + r / 5) * (1 + fire_power / 100);
            if (natural_bonus) i *= 1.1;
            i *= fights;
            out += String.format("\u0002[Q%s]:\u0002 %s :: ", x, Math.round(i));

        }
        return out;
    }

    public String getCommandName() {
        return ".fc";
    }

    public String getHelp() {
        return String.format("\u0002%s\u0002 - Returns influence you can make in one or " +
                "more battles with or without NE Bonus. Usage: .fc [optional: -f <number of fights>] " +
                "[optional -n (with NE bonus)] <Citizen ID or IRC nickname>. " +
                "Sender nickname is used as nickname and one fight without NE is calculated if no parameters are specified",
                getCommandName());
    }

    public void handleMessage(PircBot bot, String channel, String sender, String message, Boolean admin) {
        // Channel - Can be upper or lowercase
        // Sender - Always lowercase
        // Message - Always lowercase
        userRegistration userRegistration = new userRegistration();
        String user_ID;
        Boolean bonus = message.contains("-n");
        Integer fights = 1;
        if (bonus) message = message.replace("-n", "");
        if (message.contains("-f")) {
            try {
                fights = Integer.valueOf(message.split("-f")[1].trim());
                message = message.split("-f")[0].trim();
            } catch (NumberFormatException e) {
                bot.sendMessage(channel, " Number of fights must be the only thing after '-f' parameter; Example: .fc <Citizen ID> -f 20");
                return;
            }
        }

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
                bot.sendMessage(channel, fightCalc(user, bonus, fights));
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
                    bot.sendMessage(channel, fightCalc(user, bonus, fights));
                }
            }
        }
    }
}

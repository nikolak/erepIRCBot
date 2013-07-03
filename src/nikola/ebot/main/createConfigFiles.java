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

package nikola.ebot.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class createConfigFiles {
    private SupaVector banned = new SupaVector();

    public void createFiles(){
        //bot.ini
        //users.config
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("bot.ini");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties prop = new Properties();
        prop.put("server", "irc.example.com");
        prop.put("channel", "#channel");
        prop.put("admins", "admin,admin2");
        prop.put("banned", "banned,banned2");
        prop.put("nick", "bot_nickname");
        prop.put("nickpass","nickname_password");
        try {
            prop.store(fos, "Default bot settings");
            if (fos != null) {
                fos.close();
            } else {
                throw new AssertionError();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> users = new HashMap<String, String>();
        users.put("exampleuser","1");
        FileOutputStream saveFile = null;
        try {
            saveFile = new FileOutputStream("users.config");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream save = null;
        try {
            save = new ObjectOutputStream(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (save != null) {
            try {
                save.writeObject(users);
                save.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            if (saveFile != null) {
                saveFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createBannedFile(){
        banned.add("_Configuration_file_for_banned_users");
        banned.write("banned.users");

    }
}

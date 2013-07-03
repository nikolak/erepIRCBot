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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class userRegistration {
    private Map<String, String> users = new HashMap<String, String>();

    private void load() throws IOException, ClassNotFoundException {
        FileInputStream saveFile = new FileInputStream("users.config");

        ObjectInputStream save = new ObjectInputStream(saveFile);
        users = (HashMap<String, String>) save.readObject();
        save.close();
        saveFile.close();

    }

    private void store() throws IOException {
        FileOutputStream saveFile = new FileOutputStream("users.config");
        ObjectOutputStream save = new ObjectOutputStream(saveFile);
        save.writeObject(users);
        save.close();
        saveFile.close();
    }

    public boolean register(String name, String ID) throws IOException {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        users.put(name, ID);
        store();
        return true;

    }

    public String get(String name) {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users.get(name);
    }
}

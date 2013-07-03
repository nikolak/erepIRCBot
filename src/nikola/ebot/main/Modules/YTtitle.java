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

package nikola.ebot.main.Modules;

import nikola.ebot.main.ModuleInterface;
import org.jibble.pircbot.PircBot;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTtitle implements ModuleInterface {

    private static String makeBotResponse(String video_id) {
        String response;
        URL api_url;
        try {
            api_url = new URL("https://gdata.youtube.com/feeds/api/videos/" + video_id);//+"?v=2&alt=json-in-script&callback=youtubeFeedCallback");
        } catch (MalformedURLException e) {
            return "Error: Malformed URL...";
        }

        String inputJsonString;
        try {
            inputJsonString = getURLContent(api_url);
            //System.out.print(inputJsonString);
        } catch (IOException e) {
            return "Error: Can't fetch youtube feeds api.";
        }
        Serializer serializer = new Persister();
        //File source = new File("example.xml");
        youtubeAPI ytresponse;
        try {
            ytresponse = serializer.read(youtubeAPI.class, inputJsonString, false);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        response = String.format("\u0002Youtube video:\u0002 %s :: \u0002Length\u0002 %s ::" +
                " \u0002Author\u0002 %s :: \u0002Views\u0002 %s :: " +
                "\u0002Average rating\u0002 %s out of %s votes :: \u0002Uploaded on\u0002 %s",
                ytresponse.getTitle(), ytresponse.getGroup().getDuration().getDuration(),
                ytresponse.getAuthor().getAuthor(),
                ytresponse.getStats().getViewCount(),
                ytresponse.getRating().getAverage(), ytresponse.getRating().getNumRaters(),
                ytresponse.getPublished());
        return response;
    }

    private static String getURLContent(URL api_url) throws IOException {
        //System.out.print(api_url+"\n");
        //api_url= new URL("http://127.0.0.1/xml_feed.xml");
        URLConnection conn;
        InputStream in;
        try {
            conn = api_url.openConnection();
            in = conn.getInputStream();
        } catch (Exception e) {
            return e.toString();
        }

        StringBuffer sb = new StringBuffer();

        byte[] buffer = new byte[256];

        while (true) {
            int byteRead = in.read(buffer);
            if (byteRead == -1)
                break;
            for (int i = 0; i < byteRead; i++) {
                sb.append((char) buffer[i]);
            }
        }
        return sb.toString();
    }

    private static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0) {

            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    public String getCommandName() {
        return "youtube.com/watch?";
    }

    @Override
    public void handleMessage(PircBot bot, String channel, String sender, String message) {
        String[] message_parts = message.split(" ");
        String video_id = null;
        for (String message_part : message_parts) {
            if (message_part.contains("youtu.be/") | message_part.contains("youtube.com/")) {
                video_id = getYoutubeVideoId(message_part);
                break;
            }
        }
        if ((video_id == "") | (video_id == null)) {
            return;
        }
        //System.out.print("\nFetching info for ID:"+video_id);
        String final_response = makeBotResponse(video_id);
        if (!final_response.equals("error")) bot.sendMessage(channel, makeBotResponse(video_id));

    }
}

@Root
class youtubeAPI {
    @Element
    private String published;
    @Element
    private author author;
    @Element
    private String title;
    @Element
    @Namespace(prefix = "yt")
    private statistics statistics;
    @Element
    @Namespace(prefix = "yt")
    private rating rating;
    @Element
    @Namespace(prefix = "media")
    private group group;

    public String getTitle() {
        return title;
    }

    public author getAuthor() {
        return author;
    }

    public statistics getStats() {
        return statistics;
    }

    public rating getRating() {
        return rating;
    }

    public group getGroup() {
        return group;
    }

    public String getPublished() {
        return published;
    }

}

class author {

    @Element
    private String name;

    public String getAuthor() {
        return name;
    }

}

class statistics {
    @Attribute
    private String favoriteCount;
    @Attribute
    private String viewCount;

    public String getViewCount() {
        return viewCount;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }
}

class rating {
    @Attribute
    private String average;
    @Attribute
    private String max;
    @Attribute
    private String min;
    @Attribute
    private String numRaters;

    public String getAverage() {
        return average;
    }

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }

    public String getNumRaters() {
        return numRaters;
    }
}

class group {
    @Element
    @Namespace(prefix = "yt")
    private duration duration;

    public duration getDuration() {
        return duration;
    }
}

class duration {

    @Attribute
    private Integer seconds;

    public String getDuration() {
        return getDurationString(seconds);
    }

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + "h " + twoDigitString(minutes) + "m " + twoDigitString(seconds) + "s";
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
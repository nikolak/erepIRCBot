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

package eapi;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class jsonRetrieve {
    protected static String public_key = "";//Enter your public key here
    protected static String private_key = "";//Enter your private key here
    private static String auth_header = null;
    private static String date_header = null;

    public static void main(String[] args) {
//        String resource = "citizen";
//        String action = "profile";
//        ArrayList<String> params = new ArrayList<String>();
//        params.add("citizenid=123");
//        getData(resource, action, params);
    }

    public static String getData(String resource, String action, ArrayList<String> params) {
        String headers_url;
        String jsonString = null;
        URL fullURL = null;
        headers_url = resource + "/" + action;
        if (params.size() > 1) {
            headers_url += "?";
            for (int i = 0; i < params.size(); i++) {
                if (i < params.size() - 1) {
                    headers_url += params.get(i) + "&";
                } else {
                    headers_url += params.get(i);
                }
            }

        } else headers_url += "?" + params.toString().replace("[", "").replace("]", "");

        try {
            String api_url = "http://api.erepublik.com/";
            fullURL = new URL(api_url + headers_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (!constructHeader(headers_url)) return null;
        try {
            jsonString = getContentResult(fullURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static boolean constructHeader(String header_url) {
        /*
        Constructs headers to send with request.
        Internal usage.

        The Date header needs to be the current date and
        time of the request in RFC1123 format (e.g. Tue, 04 Sep 2012 15:57:48)

        The Auth header structure is {public_key}/{digest} where {public_key}
        is your public key provided by eRepublik and {digest}
        is a hash that represents an encrypted value of a concatenated string
        formed from {resource}, {action}, {params} (if they exist),
        and {date} (the same value Date header has).*/
        String RFC_1123 = "EEE, dd MMM yyyy HH:mm:ss";
        SimpleDateFormat f = new SimpleDateFormat(RFC_1123);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = f.format(new Date());
        String to_digest;
        auth_header = public_key + "/";
        date_header = date;
        to_digest = (header_url.replace("/", ":").replace("?", ":") + ":").toLowerCase() + date;
        auth_header += hmacDigest(to_digest, private_key, "HmacSHA256");
        return true;
    }

    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuilder hash = new StringBuilder();
            for (byte aByte : bytes) {
                String hex = Integer.toHexString(0xFF & aByte);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException ignored) {
        } catch (InvalidKeyException ignored) {
        } catch (NoSuchAlgorithmException ignored) {
        }
        return digest;
    }

    public static String getContentResult(URL url) throws IOException {
        //url=new URL("http://127.0.0.1/unihorn.json");
        URLConnection conn;
        InputStream in;
        try{
            conn = url.openConnection();
            conn.addRequestProperty("Auth", auth_header);
            conn.addRequestProperty("Date", date_header);
            in = conn.getInputStream();
        }catch(Exception e){
            return e.toString();
        }

        StringBuilder sb = new StringBuilder();

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
}
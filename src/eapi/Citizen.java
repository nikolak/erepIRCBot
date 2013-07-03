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

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Citizen {
    public String   name, birthday, avatar, rank_name, rank_icon, citizenship_country_name,
                    citizenship_country_initials, citizenship_region_name,
                    residence_country_name, residence_country_initials, residence_region_name, party_name,
                    unit_name, newspaper_name, url,citizen_id, full_rank, medals_list, items_donate, money_donate;

    public Boolean  is_alive, has_avatar, party_member, unit_member, owns_newspapers, party_leader, unit_leader;
    public Integer  level, national_rank, experience_points, rank_points,
                    rank_stars, citizenship_country_id, citizenship_region_id,
                    residence_country_id, residence_region_id, party_id, unit_id, newspaper_id,medal_count,
                    rank_value;
    public Float    strength;

    public String Error;

    private ArrayList<String> params = new ArrayList<String>();

    public Citizen fromID(String userID) {
        params.add("citizenId="+userID);
        String resource = "citizen";
        String action = "profile";
        String inputJsonString = jsonRetrieve.getData(resource, action,params);
        if (!inputJsonString.startsWith("{")){
            Error="API Error: "+inputJsonString;
            return this;
        }
//        System.out.print(inputJsonString);
        JsonParserFactory factory = JsonParserFactory.getInstance();
        JSONParser parser = factory.newJsonParser();
        Map jsonData = parser.parseJson(inputJsonString);

        if (!jsonData.get("code").equals("200")) {
            return null;
        }

        jsonData = (Map) jsonData.get("message");
        Map generalData = (Map) jsonData.get("general");
        Map militaryData = (Map) jsonData.get("militaryAttributes");
        Map locationData = (Map) jsonData.get("location");
        //Add achievements

        party_member = !jsonData.get("party").equals("false");
        unit_member = !jsonData.get("militaryUnit").equals("false");
        owns_newspapers = !jsonData.get("newspaper").equals("false");

        //General info
        name = generalData.get("name").toString();
        if (name.equals("null")){
            return null; //sometimes null gets returned if user doesn't exist
        }
        citizen_id=generalData.get("citizen_id").toString();
        url="http://www.erepublik.com/en/citizen/profile/"+citizen_id;
        items_donate="http://www.erepublik.com/en/economy/donate-items/"+citizen_id;
        money_donate="http://www.erepublik.com/en/economy/donate-money/"+citizen_id;
        is_alive = generalData.get("is_alive").equals("1");
        has_avatar = generalData.get("has_avatar").equals("true");
        avatar = generalData.get("avatar").toString().replace("\\","");
        experience_points = Integer.valueOf((generalData.get("experience_points")).toString());
        level = Integer.valueOf(generalData.get("level").toString());
        birthday = generalData.get("birthDay").toString();
        national_rank = Integer.valueOf(generalData.get("nationalRank").toString());

        //Military Attributes
        strength = Float.valueOf(militaryData.get("strength").toString());
        rank_points = Integer.valueOf(militaryData.get("rank_points").toString());
        rank_name = militaryData.get("rank_name").toString();
        rank_stars = Integer.valueOf(militaryData.get("rank_stars").toString());
        rank_icon = militaryData.get("rank_icon").toString();
        full_rank=rank_name;
        for(int i=0;i<rank_stars;i++) full_rank+="*";
        rank_value=getRank_value(full_rank);

        //Location attributes
        // Citizenship
        citizenship_country_id = Integer.valueOf(locationData.get("citizenship_country_id").toString());
        citizenship_country_name = locationData.get("citizenship_country_name").toString();
        citizenship_country_initials = locationData.get("citizenship_country_initials").toString();
        citizenship_region_id = Integer.valueOf(locationData.get("citizenship_region_id").toString());
        citizenship_region_name = locationData.get("citizenship_region_name").toString();

        // Residence
        residence_country_id = Integer.valueOf(locationData.get("residence_country_id").toString());
        residence_country_name = locationData.get("residence_country_name").toString();
        residence_country_initials = locationData.get("residence_country_initials").toString();
        residence_region_id = Integer.valueOf(locationData.get("residence_region_id").toString());
        residence_region_name = locationData.get("residence_region_name").toString();

        //Party attributes
        if (party_member) {
            Map partyData = (Map) jsonData.get("party");
            party_id = Integer.valueOf(partyData.get("id").toString());
            party_name = partyData.get("name").toString();
            party_leader = partyData.get("is_president").equals("1");
        }

        if (unit_member) {
            Map unitData = (Map) jsonData.get("militaryUnit");
            unit_id = Integer.valueOf(unitData.get("id").toString());
            unit_name = unitData.get("name").toString();
            unit_leader = unitData.get("is_leader").equals("true");
        }

        if (owns_newspapers) {
            Map newspaperData = (Map) jsonData.get("newspaper");
            newspaper_id = Integer.valueOf(newspaperData.get("id").toString());
            newspaper_name = newspaperData.get("name").toString();
        }

        Map medalsData= (Map) jsonData.get("achievements");
        Object[] medalArray = medalsData.values().toArray();
        medal_count=0;
        medals_list="";
        for (Object aMedalArray : medalArray) {
            Map medal = (Map) aMedalArray;
            medals_list += String.format(" %s (%s) :", medal.get("type").toString()
                    , medal.get("total").toString());
            medal_count += Integer.valueOf(medal.get("total").toString());
        }
        return this;
    }

    private Integer getRank_value(String fullRank){
        Map<String, Integer> ranks;
        ranks = new HashMap<String, Integer>();
        ranks.put("Corporal",6);
        ranks.put("Legendary Force**",60);
        ranks.put("Lt Colonel**",32);
        ranks.put("Supreme Marshal**",48);
        ranks.put("Major***",25);
        ranks.put("Colonel*",35);
        ranks.put("Colonel",34);
        ranks.put("Commander *",27);
        ranks.put("Legendary Force***",61);
        ranks.put("Private**",4);
        ranks.put("God of War",62);
        ranks.put("National Force***",53);
        ranks.put("Field Marshal***",45);
        ranks.put("Corporal***",9);
        ranks.put("Lieutenant***",17);
        ranks.put("Major*",23);
        ranks.put("Legendary Force",58);
        ranks.put("General*",39);
        ranks.put("General",38);
        ranks.put("God of War*",63);
        ranks.put("God of War**",64);
        ranks.put("Colonel**",36);
        ranks.put("Lt Colonel***",33);
        ranks.put("Private***",5);
        ranks.put("Captain***",21);
        ranks.put("Lieutenant",14);
        ranks.put("National Force**",52);
        ranks.put("Sergeant*",11);
        ranks.put("Field Marshal**",44);
        ranks.put("Corporal**",8);
        ranks.put("World Class",57);
        ranks.put("Captain**",20);
        ranks.put("Corporal*",7);
        ranks.put("Supreme Marshal***",49);
        ranks.put("Field Marshal*",43);
        ranks.put("Supreme Marshal*",47);
        ranks.put("God of War***",65);
        ranks.put("National Force*",51);
        ranks.put("Recruit",1);
        ranks.put("Lieutenant*",15);
        ranks.put("Field Marshal",42);
        ranks.put("Captain*",19);
        ranks.put("Sergeant**",12);
        ranks.put("Private*",3);
        ranks.put("Lt Colonel*",31);
        ranks.put("Commander ***",29);
        ranks.put("Colonel***",37);
        ranks.put("Major",22);
        ranks.put("Supreme Marshal",46);
        ranks.put("General**",40);
        ranks.put("National Force",50);
        ranks.put("Private",2);
        ranks.put("Commander",26);
        ranks.put("Major**",24);
        ranks.put("Commander **",28);
        ranks.put("Lt Colonel",30);
        ranks.put("General***",41);
        ranks.put("Sergeant***",13);
        ranks.put("Lieutenant**",16);
        ranks.put("Legendary Force*",59);
        ranks.put("Captain",18);
        ranks.put("Sergeant",10);

        return ranks.get(fullRank);

    }
}
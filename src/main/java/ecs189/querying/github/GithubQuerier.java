package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";
    private static final String base = "https://github.com/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);
            JSONObject repo = event.getJSONObject("repo");
            String name = repo.getString("name");
            String url = repo.getString("url");
            JSONObject payload = event.getJSONObject("payload");
            JSONArray array = payload.getJSONArray("commits");
            String sha ="";
            String msg ="";
            for(int j = 0; j <array.length(); j++){
                JSONObject obj = array.getJSONObject(j);
                sha = obj.getString("sha");
                msg = obj.getString("message");
            }

            String neatsha = sha.substring(0, Math.min(sha.length(), 8));
            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");
            sb.append("<a href =");
            sb.append(base);
            sb.append(name);
            sb.append(">");
            sb.append(name);
            sb.append("</a>");
            sb.append("<br />");
            sb.append(neatsha);
            sb.append("<br />");
            sb.append(msg);
            sb.append("<br />");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        String url = BASE_URL + user + "/events";
        System.out.println(url);
        JSONObject json = Util.queryAPI(new URL(url));
        System.out.println(json);
        JSONArray events = json.getJSONArray("root");
        int count = 0;
        for (int i = 0; (i < events.length()) && (count < 10); i++) {
            if (events.getJSONObject(i).get("type").equals("PushEvent")) {
                eventList.add(events.getJSONObject(i));
                count++;
            }
        }
        return eventList;
    }
}
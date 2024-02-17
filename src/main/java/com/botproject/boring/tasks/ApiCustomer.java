package com.botproject.boring.tasks;

import com.vdurmont.emoji.EmojiParser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ApiCustomer {
    private final static String RANDOM_URL = "http://www.boredapi.com/api/activity/";

    private Map<String, String> emoji = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        emoji.put("education", ":open_book:");
        emoji.put("recreational", ":sparkles:");
        emoji.put("social", ":couple:");
        emoji.put("diy", ":hammer_and_wrench:");
        emoji.put("charity", ":heart:");
        emoji.put("cooking", ":sandwich:");
        emoji.put("relaxation", ":woman_in_lotus_position:");
        emoji.put("music", ":man_dancing:");
        emoji.put("busywork", ":computer:");
    }

    /**
     * Such method uses external API to get content in reply
     * @param urlAddress external API`s url
     * @return JSON in the form of String
     */
    public String getURLContent(String urlAddress) {
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(urlAddress);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch (MalformedURLException e) {
            log.error("Incorrect URL address", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("An I/O error has occurred " + e.getMessage());
        }
        return content.toString();
    }

    /**
     * Processing of the received content via JSONObject
     * @return A convenient form of the bot's response
     */
    public String getRandomActivity() {
        String output = getURLContent(RANDOM_URL);
        StringBuffer buffer = new StringBuffer();
        if (!output.isEmpty()) {
            try {
                JSONObject object = new JSONObject(output);
                buffer
                        .append("Try the following activity today \n\n")
                        .append(object.getString("activity")) .append(emoji.get(object.getString("type"))).append("\n\n")
                        .append("Participants: ").append(object.getInt("participants")).append("\n\n")
                        .append("Rate:" ).append(priceConverter(object.getDouble("price")))

                ;
            } catch (JSONException e) {
                log.error("JSON parsing exception was occured");
                throw new RuntimeException();
            }
        }
        return  EmojiParser.parseToUnicode(buffer.toString());
    }

    private String priceConverter(Double ratio) {
        String convertedPrice;
        if (ratio <= 0.3) {
            convertedPrice="Low costs";
        }else if(ratio>0.3&&ratio<=0.8){
            convertedPrice ="Medium costs";
        }else {
            convertedPrice = "High costs";
        }
        return convertedPrice;
    }
}

//package com.mfbi.models;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpResponse;
//import java.util.HashMap;
//import java.util.List;
//import twitter4j.*;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "client")
//public class Trainedmodel {
//
//    @Id
//    @GeneratedValue
//    private Long id;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Trainedmodel(){
//        Twitter twitter = new TwitterFactory().getInstance();
//        try {
//        Query query = new Query("#ukraine");
//        query.setCount(1);
//        QueryResult result;
//        //            do {
//        //                result = twitter.search(query);
//        //                List<Status> tweets = result.getTweets();
//        //                for (Status tweet : tweets) {
//        //                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
//        //                }
//        //            } while ((query = result.nextQuery()) != null);
//        result = twitter.search(query);
//        List<Status> tweets = result.getTweets();
//        for (Status tweet : tweets) {
//        System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
//        var values = new HashMap<String, String>() {{
//        put("text", tweet.getText());
//        put ("sentiment", "positive");
//        }};
//
//        var objectMapper = new ObjectMapper();
//        String requestBody = objectMapper
//        .writeValueAsString(values);
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//        .uri(URI.create("http://localhost:8080/tweets"))
//        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//        .header("Content-Type","application/json")
//        .build();
//
//        HttpResponse<String> response = client.send(request,
//        HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//        }
//        System.out.println("Done");
//        System.exit(0);
//        } catch (TwitterException | JsonProcessingException te) {
//        te.printStackTrace();
//        System.out.println("Failed to search tweets: " + te.getMessage());
//        System.exit(-1);
//        } catch (IOException e) {
//        e.printStackTrace();
//        } catch (InterruptedException e) {
//        e.printStackTrace();
//        }
//    }
//
//    }
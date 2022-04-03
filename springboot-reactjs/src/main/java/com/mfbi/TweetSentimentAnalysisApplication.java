package com.mfbi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class TweetSentimentAnalysisApplication {
	/** Insert desired tags to find in tagStr */
	public static final String tagStr = "uitm";

	public static final String queryStr = "(#" + tagStr + ") min_faves:1 -filter:links -filter:replies";

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TweetSentimentAnalysisApplication.class, args);
		getTweets();

	}

	public static void getTweets() throws Exception {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("DpdVygsjBU4PIzJY5Ak5zOE7A")
				.setOAuthConsumerSecret("MjnX1lK4f5yNUjFRpKRqZPY1hzx6Ouj6j05LcCnrlCFUq82hpN")
				.setOAuthAccessToken("1318486078978809856-zWaot5hFxgOSCDvvABtr05KsNROPvw")
				.setOAuthAccessTokenSecret("zdUv3b5bMWVXFlHWTkKmTr64KP4QPibEJUvrUqVUFwXYC");
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
		EvaluateTweet evaluateTweet = new EvaluateTweet();
		try {
			Query query = new Query(queryStr);
			query.setCount(10);
			QueryResult result;
			System.out.println("Fetching tweets");
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
					Boolean bool = evaluateTweet.eval(tweet.getText());
					String sentiment = getSentiment(bool);
					var values = new HashMap<String, String>() {{
						put("text", tweet.getText());
						put("sentiment", sentiment);
						put("tweetId", 	Long.toString(tweet.getId()));
						put("score", 	Double.toString(EvaluateTweet.score));
					}};

					var objectMapper = new ObjectMapper();
					String requestBody = objectMapper
							.writeValueAsString(values);

					java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
					java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
							.uri(URI.create("http://localhost:8080/tweets"))
							.POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
							.header("Content-Type", "application/json")
							.build();

					java.net.http.HttpResponse<String> response = client.send(request,
							HttpResponse.BodyHandlers.ofString());

					System.out.println(response.body());
				}
			} while ((query = result.nextQuery()) != null);
			System.out.println("Done");
		} catch (TwitterException | JsonProcessingException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getSentiment(boolean bool){
		if (bool == true)
			return "Positive";
		else
			return "Negative";
	}

}

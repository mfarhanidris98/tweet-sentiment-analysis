package com.mfbi.controllers;

import com.mfbi.models.Tweet;
import com.mfbi.repositories.TweetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/tweets")
public class TweetsController {

    private final TweetRepository tweetRepository;

    public TweetsController(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    @GetMapping
    public List<Tweet> getTweets() {
        return tweetRepository.findAll();
    }

    @GetMapping("/{id}")
    public Tweet getTweet(@PathVariable Long id) {
        return tweetRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping
    public ResponseEntity createTweet(@RequestBody Tweet tweet) throws URISyntaxException {
        Tweet savedTweet = tweetRepository.save(tweet);
        return ResponseEntity.created(new URI("/tweets/" + savedTweet.getId())).body(savedTweet);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTweet(@PathVariable Long id, @RequestBody Tweet tweet) {
        Tweet currentTweet = tweetRepository.findById(id).orElseThrow(RuntimeException::new);
        currentTweet.setText(tweet.getText());
        currentTweet.setSentiment(tweet.getSentiment());
        currentTweet.setTweetId(tweet.getTweetId());
        currentTweet = tweetRepository.save(tweet);

        return ResponseEntity.ok(currentTweet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTweet(@PathVariable Long id) {
        tweetRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
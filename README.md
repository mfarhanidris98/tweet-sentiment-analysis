
# twitter-sentiment-analysis

A web application that visualizes sentiment analysis of tweets for the Malaysian language.


## Installation

Setup Twitter api v2 token in TweetSentimentAnalysisApplication class
in the project springboot-reactjs

```bash
    ...
    ConfigurationBuilder cb = new ConfigurationBuilder();
		/** Twitter api v2 token */
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("******************")
				.setOAuthConsumerSecret("******************")
				.setOAuthAccessToken("******************")
				.setOAuthAccessTokenSecret("******************");
    ...

```

Get trained model and mswiki.vector file in this link: https://drive.google.com/drive/folders/1NrBOAAHnos3TSqILsq2QVVshDYyaj02k?usp=sharing
Put the files in respective folders

```bash
[insert cloned path here]...springboot-reactjs/src/main/resources/malay_word2vec/mswiki.vector
[insert cloned path here]...springboot-reactjs/src/main/resources/pretrained-models/trained-model.zip
```
    
Setup path for mswiki.vector and trained model in EvaluateTweet class in the 
project springboot-reactjs

```bash
/** classpathresources path doesn't work, use absolute path instead*/
        WORD_VECTORS_PATH ="[insert cloned path here]...springboot-reactjs/src/main/resources/malay_word2vec/mswiki.vector";
        trainedModelPath = "[insert cloned path here]...springboot-reactjs/src/main/resources/pretrained-models/trained-model.zip";
        this.text = str;
```
## Run Locally

Clone the project

```bash
  git clone https://github.com/mfarhanidris/tweet-sentiment-analysis-v2
```

Open the project springboot-reactjs and run TweetSentimentAnalysisApplication class

```bash
  public class TweetSentimentAnalysisApplication { ... }
```

After Springboot is up, run Reactjs frontend

```bash
  cd frontend
```

Start the web app

```bash
  npm start
```


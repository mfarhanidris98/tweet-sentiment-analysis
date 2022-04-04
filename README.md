
# twitter-sentiment-analysis

A web application that visualizes sentiment analysis of tweets for the Malaysian language.


## Installation

Setup Twitter api v2 token in **TweetSentimentAnalysisApplication** class
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

Get **trained model** and **mswiki.vector** file in this link: https://drive.google.com/drive/folders/1NrBOAAHnos3TSqILsq2QVVshDYyaj02k?usp=sharing
Put the files in respective folders

```bash
[insert cloned path here]...springboot-reactjs/src/main/resources/malay_word2vec/mswiki.vector
[insert cloned path here]...springboot-reactjs/src/main/resources/pretrained-models/trained-model.zip
```
    
Setup path for **mswiki.vector** and **trained model** in EvaluateTweet class in the 
project springboot-reactjs

```bash
/** classpathresources path doesn't work, use absolute path instead*/
        WORD_VECTORS_PATH ="[insert cloned path here]...springboot-reactjs/src/main/resources/malay_word2vec/mswiki.vector";
        trainedModelPath = "[insert cloned path here]...springboot-reactjs/src/main/resources/pretrained-models/trained-model.zip";
        this.text = str;
```

Insert desired tag to analyse with sentiment model at **TweetSentimentAnalysisApplication** class

```bash
/** Insert desired tags to find in tagStr */
	public static final String tagStr = "[insert desired tag to analyse here]";

	public static final String queryStr = "(#" + tagStr + ") min_faves:1 -filter:links -filter:replies";
```
## Run Locally

Clone the project

```bash
  git clone https://github.com/mfarhanidris/tweet-sentiment-analysis-v2
```

Open the project springboot-reactjs and run **TweetSentimentAnalysisApplication** class

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


## Model Training

Model is train with RNN layers

```bash 
MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .updater(new Adam(5e-3))
            .l2(1e-5)
            .weightInit(WeightInit.XAVIER)
            .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0)
            .list()
            .layer(new LSTM.Builder().nIn(vectorSize).nOut(256)
                .activation(Activation.TANH).build())
                .layer(new DenseLayer.Builder().nOut(500).build())
            .layer(new DenseLayer.Builder().nOut(256).build())
            .layer(new DenseLayer.Builder().nOut(256).build())
            .layer(new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
                .lossFunction(LossFunctions.LossFunction.XENT).nIn(256).nOut(2).build())
            .build();
```

# Result

**Test #1**
```bash
Layers:
1. RNN

Dataset
Train
Positive/Negative: 36002
Test
Positive/Negative: 4000

Test Evaluation
Accuracy    : 0.8519
Precision   : 0.8633
Recall      : 0.8362
F1 Score    : 0.8495
```

**Test #2**
```bash
Layers:
1. RNN

Dataset
Train
Positive/Negative: 36002
Test
Positive/Negative: 4000

Test Evaluation
Accuracy    : 0.8007
Precision   : 0.8141
Recall      : 0.7795
F1 Score    : 0.7964
```

**Test #3**
```bash
Layers:
1. DenseLayer
2. DenseLayer
3. RNN

Dataset:
Positive/Negative: 36002

Test Evaluation
Accuracy    : ~0.7
Precision   : ~0.7
Recall      : ~0.7
F1 Score    : ~0.7
```
## Appendix

1. Tweet Datasets:
    https://github.com/huseinzol05/malay-dataset/tree/master/sentiment/semisupervised-twitter

2. Word vector:
    https://github.com/huseinzol05/malaya/tree/master/pretrained-model/wordvector

3. Twitter4j:
    https://twitter4j.org/en/index.html

4. Word2vec Sentiment model training:
    https://github.com/breandan/deep-learning-samples/tree/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/recurrent/word2vecsentiment

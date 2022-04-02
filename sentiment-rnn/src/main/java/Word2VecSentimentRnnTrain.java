/*******************************************************************************
 * Copyright (c) 2015-2019 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.net.URL;

/**Example: Given a movie review (raw text), classify that movie review as either positive or negative based on the words it contains.
 * This is done by combining Word2Vec vectors and a recurrent neural network model. Each word in a review is vectorized
 * (using the Word2Vec model) and fed into a recurrent neural network.
 * Training data is the "Large Movie Review Dataset" from http://ai.stanford.edu/~amaas/data/sentiment/
 * This data set contains 25,000 training reviews + 25,000 testing reviews
 *
 * Process:
 * 1. Automatic on first run of example: Download data (movie reviews) + extract
 * 2. Load existing Word2Vec model (for example: Google News word vectors. You will have to download this MANUALLY)
 * 3. Load each each review. Convert words to vectors + reviews to sequences of vectors
 * 4. Train network
 *
 * With the current configuration, gives approx. 83% accuracy after 1 epoch. Better performance may be possible with
 * additional tuning.
 *
 * NOTE / INSTRUCTIONS:
 * You will have to download the Google News word vector model manually. ~1.5GB
 * The Google News vector model available here: https://code.google.com/p/word2vec/
 * Download the GoogleNews-vectors-negative300.bin.gz file
 * Then: set the WORD_VECTORS_PATH field to point to this location.
 *
 * @author Alex Black
 */
public class Word2VecSentimentRnnTrain
{
    public static String DATA_PATH = "";

    public static String WORD_VECTORS_PATH = "";

    public static int truncateReviewsToLength = 280;

    public static File modelOutfile = new File(new ClassPathResource("/pretrained-models/trained-model.zip").getPath()).getAbsoluteFile();;
    
    /**
     * This int holds the number of examples in each minibatch.
     */
    public static final int batchSize = 1;
    
    public static void main(String[] args) throws Exception 
    {
        DATA_PATH = new File(new ClassPathResource("/").getPath()).getAbsolutePath();
        WORD_VECTORS_PATH = new File(new ClassPathResource("/malay_word2vec/mswiki.vector").getPath()).getAbsolutePath();

        if(args.length != 0)
        {
            WORD_VECTORS_PATH = args[0];
            
            //TODO: introduce a logger
            System.out.println("using command line argument as word vectors path: " + WORD_VECTORS_PATH);
        }
        
        if(WORD_VECTORS_PATH.startsWith("/PATH/TO/YOUR/VECTORS/"))
        {
            throw new RuntimeException("Please set the WORD_VECTORS_PATH before running this example");
        }

        //Download and extract data
//        downloadData();

        int vectorSize = 300;   //Size of the word vectors. 300 in the Google News model

        int nEpochs = 1;        //Number of epochs (full passes of training data) to train on
        
        final int seed = 1234;     //Seed for reproducibility
        
        Nd4j.getMemoryManager().setAutoGcWindow(1000);  //https://deeplearning4j.org/workspaces

        //Set up network configuration
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

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        //DataSetIterators for training and testing respectively
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));
        
        SentimentExampleIterator train = new SentimentExampleIterator(DATA_PATH, wordVectors, batchSize, truncateReviewsToLength, true);
        
        SentimentExampleIterator test = new SentimentExampleIterator(DATA_PATH, wordVectors, batchSize, truncateReviewsToLength, false);

        System.out.println("Starting training");
        
        model.setListeners(new ScoreIterationListener(10), new EvaluativeListener(test, 1, InvocationType.EPOCH_END));
        model.setListeners(new ScoreIterationListener(10), new EvaluativeListener(train, 1, InvocationType.EPOCH_END));

        model.fit(train, nEpochs);
        
        // save the trained model        
        boolean saveUpdatesForFurtherTrainging = false;
        
        ModelSerializer.writeModel(model, modelOutfile, saveUpdatesForFurtherTrainging);
        
        // evaluate
//        evaluate(test, model, truncateReviewsToLength);
        
        System.out.println("----- Example complete -----");
    }

}

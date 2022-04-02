import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.File;
import java.io.IOException;

//import RnnTrain.*;

/**
 * This class evaluates a trained neural network model.
 * 
 * @author Roberto Marquez
 */
public class Word2VecSentimentRnnEvaluate
{
    public static String DATA_PATH = "";

    public static String WORD_VECTORS_PATH = "";

    public static int truncateReviewsToLength = 280;

    public static int batchSize = 1;


    public static void main(String[] args) throws Exception
    {
        String trainedModelPath = null;

        DATA_PATH = new File(new ClassPathResource("/").getPath()).getAbsolutePath();
        WORD_VECTORS_PATH = new File(new ClassPathResource("/malay_word2vec/mswiki.vector").getPath()).getAbsolutePath();
        trainedModelPath = new File(new ClassPathResource("/pretrained-models/trained-model.zip").getPath()).getAbsolutePath();

        System.out.println("----- Evaluation initializing -----");

        System.out.println("using command line arguement for trained model path: " + trainedModelPath);
        
        System.out.println("----- Evaluation starting -----");
        
        Word2VecSentimentRnnEvaluate deepLearner = new Word2VecSentimentRnnEvaluate();
        
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(trainedModelPath);                

        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));
        
        SentimentExampleIterator test = new SentimentExampleIterator(DATA_PATH, wordVectors, batchSize, truncateReviewsToLength, false);
        
        // expected bad review
        String shortNegativeReview = "hours dah short pastu suruh macam macam babi";
        deepLearner.evaluate(test, model, truncateReviewsToLength, shortNegativeReview);
        
        // another expected bad review
        String secondBadReview = "girls klau dah kawen nanti nak melawa selagi laki suruh sila guna duit muka hodoh takpe dik takde";
        deepLearner.evaluate(test, model, truncateReviewsToLength, secondBadReview);
        
        // a good review follows (hopefully)
        String goodReview = "terima kasih ninasubki mempercayakan kesehatan kecantikan bibir selalu rkds hydra lip";
        deepLearner.evaluate(test, model, truncateReviewsToLength, goodReview);
        
        System.out.println("----- Evaluation complete -----");
    }

    /**
     * After training: load a single example and generate predictions
     * @param test
     * @param model
     * @param truncateReviewsToLength
     * @param review
     * @throws IOException 
     */
    private void evaluate(SentimentExampleIterator test, MultiLayerNetwork model, 
                            int truncateReviewsToLength, String review) throws IOException
    {
        INDArray features = test.loadFeaturesFromString(review, truncateReviewsToLength);
        INDArray networkOutput = model.output(features);
        long timeSeriesLength = networkOutput.size(2);
        INDArray probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1));

        System.out.println("\n\n-------------------------------");
        System.out.println("Review: \n" + review);
        System.out.println("\n\nProbabilities at last time step:");
        System.out.println("p(positive): " + probabilitiesAtLastWord.getDouble(0));
        System.out.println("p(negative): " + probabilitiesAtLastWord.getDouble(1));

        System.out.println("----- Evaluate complete -----");
    }    
}

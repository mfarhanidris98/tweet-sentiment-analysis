package com.mfbi;

import java.io.File;
import java.io.IOException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;
import java.io.*;
import java.util.*;
import org.nd4j.common.io.ClassPathResource;

public class EvaluateTweet {

    public static String DATA_PATH = "";

    /** mswiki.vector path */
    public static String WORD_VECTORS_PATH = "";

    /** Pretrained model path */
    public static String trainedModelPath = "";

    public static int truncateReviewsToLength = 280;  //Truncate reviews with length (# words) greater than this

    public static final int batchSize = 1;

    public static String text = "";
    public EvaluateTweet(){}

    public boolean eval(String str) throws Exception
    {
        System.out.println("----- Evaluation initializing -----");

        DATA_PATH = new File(new ClassPathResource("/").getPath()).getAbsolutePath();
        WORD_VECTORS_PATH = new File(new ClassPathResource("/malay_word2vec/mswiki.vector").getPath()).getAbsolutePath();
        trainedModelPath = new File(new ClassPathResource("/pretrained-models/trained-model.zip").getPath()).getAbsolutePath();
        this.text = str;

        System.out.println("using command line arguement for trained model path: " + trainedModelPath);

        System.out.println("----- Evaluation starting -----");

        EvaluateTweet deepLearner = new EvaluateTweet();

        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(trainedModelPath);

        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));

        Iterator test = new Iterator(DATA_PATH, wordVectors, batchSize, truncateReviewsToLength, false);

        boolean bool = deepLearner.evaluate(test, model, truncateReviewsToLength, cleanStrings(text));

        System.out.println("----- Evaluation complete -----");

        return bool;
    }

    private boolean evaluate(Iterator test, MultiLayerNetwork model,
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

        double val = probabilitiesAtLastWord.getDouble(0);

        System.out.println("----- Evaluate complete -----");

        if (val<0.5)
            return false;
        else
            return true;
    }

    //TODO apply more regex
    public static String cleanStrings(String str) throws IOException {
        String result = str.toLowerCase(Locale.ROOT), rmvStr = "http", str2, str3;
        int strIndex, strIndex2;

        if (result.contains(rmvStr)){
            int i = result.lastIndexOf(rmvStr), j;
            if (result.indexOf(" ", i+1) == -1) {
                if (result.charAt(i-1) == ' ')
                    result = result.substring(0, i - 1);
                else
                    result = result.substring(0, i);
            }
        }

        while (result.contains(rmvStr)){
            strIndex = result.indexOf(rmvStr);
            strIndex2 = result.indexOf(" ", strIndex);
            str2 = result.substring(0, strIndex);
            if (strIndex2 == -1)
                str3 = result.substring(strIndex+rmvStr.length(), result.length());
            else
                str3 = result.substring(strIndex2+1, result.length());
            result = str2.concat(str3);
        }

        rmvStr = "@";
        if (result.contains(rmvStr)){
            int i = result.lastIndexOf(rmvStr), j;
            if (result.indexOf(" ", i+1) == -1) {
                if (result.charAt(i-1) == ' ')
                    result = result.substring(0, i-1);
                else
                    result = result.substring(0, i);
            }
        }

        while (result.contains(rmvStr)){
            strIndex = result.indexOf(rmvStr);
            strIndex2 = result.indexOf(" ", strIndex);
            str2 = result.substring(0, strIndex);
            if (strIndex2 == -1)
                str3 = result.substring(strIndex+rmvStr.length(), result.length());
            else
                str3 = result.substring(strIndex2+1, result.length());
            result = str2.concat(str3);
        }

        rmvStr = "#";
        if (result.contains(rmvStr)){
            int i = result.lastIndexOf(rmvStr), j;
            if (result.indexOf(" ", i+1) == -1) {
                if (result.charAt(i-1) == ' ')
                    result = result.substring(0, i - 1);
                else
                    result = result.substring(0, i);
            }
        }

        while (result.contains(rmvStr)){
            strIndex = result.indexOf(rmvStr);
            strIndex2 = result.indexOf(" ", strIndex);
            str2 = result.substring(0, strIndex);
            if (strIndex2 == -1)
                str3 = result.substring(strIndex+rmvStr.length(), result.length());
            else
                str3 = result.substring(strIndex2+1, result.length());
            result = str2.concat(str3);
        }

        result = result.replaceAll("\\r\\n|\\r|\\n", "");
        result = result.replaceAll("\\d", "");
        result = result.replaceAll("[^a-zA-Z0-9\\s]", "");
        result = result.replaceAll("(\\s+[a-z](?=\\s))","");
        result = result.replaceAll("[-+^]*", "");

        File file=new ClassPathResource("malay-stopwords.txt").getFile();    //creates a new file instance
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
        List<String> rmvStrList = new ArrayList<>();
        List<String> myList = new ArrayList<String>(Arrays.asList(result.split(" ")));
        while ((rmvStr = br.readLine()) != null) {
            rmvStrList.add(rmvStr);
            rmvStr = br.readLine();
            myList.remove(rmvStr);
        }
        myList.removeAll(rmvStrList);

        result = String.join(" ", myList);


        return result;
    }
}

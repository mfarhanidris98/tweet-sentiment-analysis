
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.nd4j.common.io.ClassPathResource;

import java.io.*;
import java.util.*;

public class JsonToFilePos {

    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(new ClassPathResource("/strong-positives.json").getFile()));

            JSONArray array = new JSONArray();
            array.add(obj);

            String str = array.toString();

            String newStr = "";
            int i = 1, strPos = 0;
            boolean result;
            while (str != "") {
                strPos = str.indexOf("\",\"", 2);
                newStr = str.substring(3, strPos);
                str = str.substring(strPos, str.length());
                newStr = cleanStrings(newStr);
                List<String> newStrList = new ArrayList<String>(Arrays.asList(newStr.split(" ")));
                int newStrSize = newStrList.size();
                if(i > 30523){
                    if (newStr != "" && newStrSize > 5) {
                        File file = new File(new ClassPathResource("/tweets-labeled").getPath() +"/positive/" + i + ".txt");
                        file.createNewFile();
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(newStr);
                        fileWriter.flush();
                        fileWriter.close();
                        System.out.println(i + ": " + newStr);
                        i++;
                    }
                } else {
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //TODO: refractor this method to new class
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

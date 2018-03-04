package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class SpamFilter{
    public static double threshold = 50.0;
    private double hamCount = 0;
    private double spamCount = 0;
    private File trainDir;
    private File testDir;
    private TreeMap<String,Double> prSW;
    private TreeMap<String,Integer> hamFreq;
    private TreeMap<String,Integer> spamFreq;
    private ObservableList<TestFile> tested;

    // obstinate with testing and training folders defined
    public SpamFilter(File trainDirectory, File testDirectory){

        trainDir = trainDirectory;
        testDir = testDirectory;
        hamFreq = new TreeMap<>();
        spamFreq = new TreeMap<>();
        prSW = new TreeMap<>();

    }

    public void train() throws IOException{
        // open train directory
        // make list containing spam, ham
        File[] contents = trainDir.listFiles();
        // parses through each folder (spam or ham) individually
        for (File current : contents) {
            // create list of files
            File[] files = current.listFiles();
            // parse through files
            for (File file : files) {
                // create list of words contained in file
                ArrayList<String> words = new ArrayList();
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    if (isWord(word)) {
                        // populate word list per file
                        if (!words.contains(word)) {
                            words.add(word);
                        }
                    }
                }
                // adjusts the ham frequency map according to words found in file
                if (current.getName().equals("ham")) {
                    this.hamCount ++;
                    for (String element : words) {
                        if (hamFreq.containsKey(element)) {
                            int oldCount = hamFreq.get(element);
                            hamFreq.put(element, oldCount + 1);
                        } else {
                            hamFreq.put(element, 1);
                        }
                    }

                    // adjusts the spam frequency map according to words found in file
                } else if (current.getName().equals("spam")) {
                    this.spamCount ++;
                    for (String element : words) {
                        if (spamFreq.containsKey(element)) {
                            int oldCount = spamFreq.get(element);
                            spamFreq.put(element, oldCount + 1);
                        } else {
                            spamFreq.put(element, 1);
                        }
                    }

                }
            }
        }
        // add probabilities from ham file
        for (Map.Entry<String, Integer> entry: hamFreq.entrySet()){
            if (!prSW.containsKey(entry.getKey())){
                if (spamFreq.containsKey(entry.getKey())){
                    prSW.put(entry.getKey(), (spamFreq.get(entry.getKey())/spamCount) / ((hamFreq.get(entry.getKey())/hamCount)+(spamFreq.get(entry.getKey())/spamCount)));
                }
            }
        }
        // check spamFreq to find probabilities of words not in ham
        for (Map.Entry<String, Integer> entry: spamFreq.entrySet()){
            if (!prSW.containsKey(entry.getKey())){
                if (hamFreq.containsKey(entry.getKey())){
                    prSW.put(entry.getKey(), (spamFreq.get(entry.getKey())/spamCount) / ((hamFreq.get(entry.getKey())/hamCount)+(spamFreq.get(entry.getKey())/spamCount)));
                }
            }
        }
    }

    // testing
    public ObservableList<TestFile> test() throws IOException{
        // return arraylist
        ObservableList<TestFile> testedFiles = FXCollections.observableArrayList();
        // parse through test directory
        // file array consisting of ham, spam folders
        File[] contents = testDir.listFiles();
        for (File current: contents){
            // file array consisting of all the files in each of the previous folders
            File[] files = current.listFiles();
            // parse through files
            for (File file: files){
                // instantiate TestFile object
                TestFile testing = new TestFile();
                // set the name
                testing.setFileName(file.getName());
                // set actual class
                if (current.getName().equals("ham")){
                    testing.setActualClass("Ham");
                }else if (current.getName().equals("spam")){
                    testing.setActualClass("Spam");
                }
                // get probability
                // sum variable
                double sum = 0;
                // parse over file and create list of words
                ArrayList<String> words = new ArrayList();
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    if (isWord(word)) {
                        // populate word list per fileham
                        if (!words.contains(word)) {
                            words.add(word);
                        }
                    }
                }
                for (String word: words){
                    // check if it is in training map, ignore if not present
                    if (prSW.containsKey(word)){
                        sum += ((Math.log(1 - (prSW.get(word))))-(Math.log(prSW.get(word))));
                        if(prSW.get(word) >= 1) {
                            System.out.println(prSW.get(word));
                        }
                    }
                }
                testing.setSpamProbability(1/(1+(Math.pow(Math.E, sum))));

                testedFiles.add(testing);
            }
        }
        this.tested = testedFiles;
        System.out.println("tested formed");
        return testedFiles;
    }

    public double[] getPrecisionAccuracy(){
        double[] precisionAccuracy = new double[2];
        int truePos = 0;
        int trueNeg = 0;
        int falsePos = 0;
        int falseNeg = 0;

        for (TestFile file: this.tested){

            // true positive classification as spam
            if (file.getActualClass() == "spam" && file.getGuessedClass() == "spam"){
                truePos ++;

            // false negative classification of spam
            } else if(file.getActualClass() == "spam" && file.getGuessedClass() == "ham"){
                falseNeg ++;

            // true negative classification of spam
            } else if(file.getActualClass() == "ham" && file.getGuessedClass() == "ham"){
                trueNeg ++;

            // false positive classification of spam
            } else if(file.getActualClass() == "ham" && file.getGuessedClass() == "spam"){
                falsePos ++;
            }
        }
        precisionAccuracy[0] = truePos/(falsePos + truePos);
        precisionAccuracy[1] = (truePos + trueNeg)/(this.hamCount + this.spamCount);

        return precisionAccuracy;
    }

    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z]+$";
        return (word.matches(pattern));
    }
}


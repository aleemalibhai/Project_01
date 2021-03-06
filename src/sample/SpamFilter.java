package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.FileNotFoundException;


public class SpamFilter{
    public static double threshold = 0.5;
    private double hamCount = 0;
    private double spamCount = 0;
    private double testTotal = 0;
    private File trainDir;
    private File testDir;
    private TreeMap<String,Double> prSW;
    private TreeMap<String,Double> hamFreq;
    private TreeMap<String,Double> spamFreq;
    private ObservableList<TestFile> tested;

    // obstinate with testing and training folders defined
    public SpamFilter(File trainDirectory, File testDirectory) throws FileNotFoundException{

        trainDir = trainDirectory;
        testDir = testDirectory;
        hamFreq = new TreeMap<>();
        spamFreq = new TreeMap<>();
        prSW = new TreeMap<>();
        Scanner scanner = new Scanner(new File("sWords_list.txt"));
        scanner.useDelimiter("\\s");
        while(scanner.hasNextLine()){
            if(isWord(scanner.nextLine())) {
                if (!prSW.containsKey(scanner.nextLine())) {
                    prSW.put(scanner.nextLine(), 0.71);
                }
            }
        }
        scanner.close();
    }

    public void train() throws IOException{
        // open train directory
        // make list containing spam, ham
        File[] contents = trainDir.listFiles();
        // parses through each folder (spam or ham) individually
        for (File current : contents) {
            // loops through folder and creates a list of files
            File[] files = current.listFiles();
            // parse through all files
            for (File file : files) {
                // create list of words contained in file
                ArrayList<String> words = new ArrayList();
                Scanner scanner = new Scanner(file);
                // delim on space
                scanner.useDelimiter("\\s");
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    // check for duplicates
                    if (isWord(word)) {
                        // populate word list per file
                        if (!words.contains(word)) {
                            words.add(word);
                        }
                    }
                }
                // adjusts the ham frequency map according to words found in file
                // check what folder we are in
                if (current.getName().equals("ham")) {
                    // increment ham count
                    this.hamCount ++;
                    // parse through word list
                    for (String element : words) {
                        if (hamFreq.containsKey(element)) {
                            // get the old count and one to it
                            double oldCount = hamFreq.get(element);
                            hamFreq.put(element, oldCount + 1.0);
                        } else {
                            // create element with a count of one if id does not exist
                            hamFreq.put(element, 1.0);
                        }
                    }

                // adjusts the spam frequency map according to words found in file
                } else if (current.getName().equals("spam")) {
                    this.spamCount ++;
                    for (String element : words) {
                        if (spamFreq.containsKey(element)) {
                            double oldCount = spamFreq.get(element);
                            spamFreq.put(element, oldCount + 1);
                        } else {
                            spamFreq.put(element, 1.0);
                        }
                    }

                }
            }
        }
        // add probabilities from ham file
        for (Map.Entry<String, Double> entry: hamFreq.entrySet()){
            if (!prSW.containsKey(entry.getKey())){
                if (spamFreq.containsKey(entry.getKey())){
                    prSW.put(entry.getKey(), (spamFreq.get(entry.getKey())/spamCount) / ((hamFreq.get(entry.getKey())/hamCount)+(spamFreq.get(entry.getKey())/spamCount)));
                }
            }
        }
        // check spamFreq to find probabilities of words not in ham
        for (Map.Entry<String, Double> entry: spamFreq.entrySet()){
            if (!prSW.containsKey(entry.getKey())){
                if (hamFreq.containsKey(entry.getKey())){
                    prSW.put(entry.getKey(), (spamFreq.get(entry.getKey())/spamCount) / ((hamFreq.get(entry.getKey())/hamCount)+(spamFreq.get(entry.getKey())/spamCount)));
                }
            }
        }
    }

    // testing
    public ObservableList<TestFile> test() throws IOException{
        // returns Observable list with all tested files
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
                }else if (current.getName().equals("spam")) {
                    testing.setActualClass("Spam");
                }
                // get probability
                // parse over file and create list of words
                ArrayList<String> words = new ArrayList();
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter("\\s");
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    if (isWord(word)) {
                        // populate word list per file
                        if (!words.contains(word)) {
                            words.add(word);
                        }
                    }
                }
                // sum variable
                double sum = 0;
                // parse through word list and get probabilities
                for (String word: words){
                    // check if it is in training map, ignore if not present
                    // add to the sum
                    if (prSW.containsKey(word)) {
                        sum += ((Math.log(1 - (prSW.get(word)))) - (Math.log(prSW.get(word))));
                    }
                }
                // set the calculated spam probability
                testing.setSpamProbability(1.0/(1.0+(Math.pow(Math.E, sum))));
                // set class depending on probability
                testing.setguessedClass();
                // add file to Observable List
                testedFiles.add(testing);
            }
        }
        this.tested = testedFiles;
        return testedFiles;
    }

    public String[] getPrecisionAccuracy(){
        // String array that has precision and accuracy
        String[] precisionAccuracy = new String[2];
        double truePos = 0;
        double trueNeg = 0;
        double falsePos = 0;
        DecimalFormat df = new DecimalFormat("0.00000");

        for (TestFile file: this.tested){

            // true positive classification as spam
            if (file.getActualClass().equals("Spam") && file.getGuessedClass().equals("spam")){
                truePos ++;

            // true negative classification of spam
            } else if(file.getActualClass().equals("Ham") && file.getGuessedClass().equals("ham")){
                trueNeg ++;

            // false positive classification of spam
            } else if(file.getActualClass().equals("Ham") && file.getGuessedClass().equals("spam")){
                falsePos ++;
            }
            // Test file counter
            this.testTotal++;
        }
        // make values ready to be displayed
        precisionAccuracy[0] = df.format(truePos/(falsePos + truePos));
        precisionAccuracy[1] = df.format((truePos + trueNeg)/(this.testTotal));

        return precisionAccuracy;
    }
    // checks if the parsed entry is a word
    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z]+$";
        return (word.matches(pattern));
    }
}


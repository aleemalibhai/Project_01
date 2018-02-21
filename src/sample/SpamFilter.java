package sample;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;


public class SpamFilter{
    private File trainDir;
    private File testDir;
    private TreeMap<String,Double> probabolitySpamGivenWord;
    private TreeMap<String,Integer> hamFreq;
    private TreeMap<String,Integer> spamFreq;

    // obstinate with testing and training folders defined
    public SpamFilter(File trainDirectory, File testDirectory){
        trainDir = trainDirectory;
        testDir = testDirectory;
    }

    public void train() throws IOException{
        // open train directory
        // make list containing spam, ham
        File[] contents = trainDir.listFiles();
        // parses through each folder (spam or ham) individually
        for (File current : contents){
            // create list of files
            File[] files = current.listFiles();
            // parse through files
            for (File file: files){
                // create list of words contained in file
                ArrayList<String> words = new ArrayList();
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    if (isWord(word)) {
                        // populate word list per file
                        if (!words.contains(word)){
                            words.add(word);
                        }
                    }
                }
                // adjusts the ham frequency map according to words found in file
                if (current.getName().equals("ham")){
                    for (String element: words){
                        if (hamFreq.containsKey(element)){
                            int oldCount = hamFreq.get(element);
                            hamFreq.put(element, oldCount+1);
                        }else {
                            hamFreq.put(element, 1);
                        }
                    }
                    // adjusts the spam frequency map according to words found in file
                }else if(current.getName().equals("spam")){
                    for (String element: words){
                        if (spamFreq.containsKey(element)){
                            int oldCount = spamFreq.get(element);
                            spamFreq.put(element, oldCount+1);
                        }else {
                            spamFreq.put(element, 1);
                        }
                    }

                }
            }
        }
    }

    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z]+$";
        return (word.matches(pattern));
    }
}


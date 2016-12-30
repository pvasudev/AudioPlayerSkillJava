package net.paavan.audioplayerskill;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlainTextMultilineFileReader {
    public List<String> readFileLinesAsList() throws IOException {
        List<String> fileLines = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dropboxNewSongs.txt").getFile());

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            for(String line; (line = br.readLine()) != null; ) {
                fileLines.add(line);
            }
        }

        return fileLines;
//        return Arrays.asList("https://dl.dropboxusercontent.com/u/518062/%5BNew%20Songs%5D/NewSongs/John_Wesley_Coleman_-_07_-_Tequila_10_Seconds.mp3");
    }
}

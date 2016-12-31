package net.paavan.audioplayerskill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlainTextMultilineFileReader {
    public List<String> readFileLinesAsList(final String bundledFileName) throws IOException {
        List<String> fileLines = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(bundledFileName).getFile());

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            for(String line; (line = br.readLine()) != null; ) {
                fileLines.add(line);
            }
        }

        return fileLines;
//        return Arrays.asList("https://dl.dropboxusercontent.com/u/518062/%5BNew%20Songs%5D/NewSongs/John_Wesley_Coleman_-_07_-_Tequila_10_Seconds.mp3");
    }
}

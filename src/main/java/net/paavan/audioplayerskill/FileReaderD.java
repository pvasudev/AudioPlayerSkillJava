package net.paavan.audioplayerskill;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileReaderD {
    public List<String> readFile() throws IOException {
        List<String> fileLines = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dropboxNewSongs.txt").getFile());

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            for(String line; (line = br.readLine()) != null; ) {
                // process the line.
                fileLines.add(line);
            }
            // line is not visible here.
        }

        return fileLines;
    }
}

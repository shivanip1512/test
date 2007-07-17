package com.cannontech.customtasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class CleanWhiteSpace extends Task {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String REGEX_PATTERN = "^\\s+|\\s+$";
    private String file;
    
    public void execute() {
    
        if (file == null) {
            throw new BuildException("file must be set");
        }
        
        try {
            String fullFilePath = getFullFilePath(file);
            List<String> lines = readFile(fullFilePath);
            writeToFile(fullFilePath, lines);
        } catch (IOException e) {
            throw new BuildException("unable to remove whitespace from file: " + e);
        }
    }
    
    private List<String> readFile(final String file) throws IOException {
        final List<String> lines = new ArrayList<String>();
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(cleanLine(line));
        }
        
        return lines;
    }
    
    private String getFullFilePath(final String file) {
        String fullFilePath = this.getProject().getProperty("basedir") + FILE_SEPARATOR + file;
        return fullFilePath;
    }
    
    private void writeToFile(final String file, final List<String> lines) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (final String line : lines) {
            writer.write(line + LINE_SEPARATOR);
        }
        writer.flush();
        writer.close();
    }
    
    private String cleanLine(final String dirty) {
        return dirty.replaceAll(REGEX_PATTERN, "");
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
    
}

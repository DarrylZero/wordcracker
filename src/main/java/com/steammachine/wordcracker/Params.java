package com.steammachine.wordcracker;

import java.io.File;

public class Params {

    String word;
    File outputDir;
    File dictionaries;

    public File dictionaries() {
        return dictionaries;
    }

    public File outputDir() {
        return outputDir;
    }

    public String word() {
        return word;
    }
}

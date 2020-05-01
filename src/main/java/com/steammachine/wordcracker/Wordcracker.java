package com.steammachine.wordcracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Character.toLowerCase;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

public class Wordcracker {

    private Set<String> dictionary = new HashSet<>();

    public void words(String wordsToBreak, Consumer<String> consumer) {
        List<String> word =
                wordsToBreak.chars().mapToObj(code -> (char) toLowerCase(code))
                    .map(Object::toString)
                    .collect(toCollection(ArrayList::new));

        stream(new PowerSetIterable<>(word).spliterator(), false)
                .flatMap(subset -> stream(new PermutationIterable<>(subset).spliterator(), false))
                .map(chars -> String.join("", chars))
                .filter(w -> dictionary.contains(w))
                .distinct()
                .forEach(consumer);
    }

    public static void main(String[] args) {
        run(args);
    }

    static void run(String[] args) {
        Params params = parseParams(args);


        Wordcracker wordcracker = new Wordcracker();
        if (params.dictionaries().list() != null) {

            Stream.of(requireNonNull(params.dictionaries().list()))
                  .map(name -> new File(params.dictionaries(), name))
                  .forEach(file -> {
                      try {
                          try (BufferedReader reader = new BufferedReader(
                                  new InputStreamReader(new FileInputStream(file), UTF_8))) {

                              wordcracker.dictionary.addAll(reader.lines().collect(Collectors.toSet()));
                          }
                      } catch (IOException e) {
                          e.printStackTrace(); // #ATTE
                      }
                  });
        }

        wordcracker.words(params.word, System.out::println);

    }

    static Params parseParams(String[] args) {
        if (args.length % 2 != 0) {
            throw new IllegalStateException();
        }
        Map<String, String> paramMap = IntStream
                .range(0, args.length / 2).peek(index -> {
                    if (!args[index * 2].startsWith("-")) {
                        throw new IllegalStateException();
                    }
                })
                .mapToObj(i -> new String[]{args[i * 2], args[i * 2 + 1]})
                .collect(toMap(strings -> strings[0], strings -> strings[1],
                               (s, s2) -> {
                                   throw new IllegalStateException();
                               }));


        Params params = new Params();

        String homeDir = System.getProperty("user.dir");
        String dictionaries = paramMap.get("-D") == null ? "/dictionaries" : paramMap.get("-D");

        params.dictionaries = dictionaries.startsWith("/") ? Paths.get(homeDir, dictionaries).toFile()
                : Paths.get(dictionaries).toFile();

        String out = paramMap.get("-O") == null ? "/output" : paramMap.get("-O");
        params.outputDir = out.startsWith("/")
                ? Paths.get(homeDir, out).toFile()
                : Paths.get(out).toFile();

        if (paramMap.get("-W") == null) {
            throw new IllegalArgumentException("param -W is not found"); // #ATTE
        }
        params.word = paramMap.get("-W"); // #ATTE
        return params;
    }


}

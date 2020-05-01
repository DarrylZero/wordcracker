package com.steammachine.wordcracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

public class AllCombinationss {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("v", "e", "r", "b", "o");

        stream(new PowerSetIterable<>(list).spliterator(), false)
                .flatMap(subset -> stream(new PermutationIterable<>(subset).spliterator(), false))
                .map(strings -> strings.stream().collect(joining()))
                .forEach(System.out::println);

    }
}

//From https://github.com/javagl/Combinatorics
class PowerSetIterable<T> implements Iterable<List<T>> {
    private final List<T> input;
    private final int numElements;

    public PowerSetIterable(List<T> input) {
        this.input = input;
        numElements = 1 << input.size();
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new Iterator<List<T>>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < numElements;
            }

            @Override
            public List<T> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }
                List<T> element = new ArrayList<T>();
                for (int i = 0; i < input.size(); i++) {
                    long b = 1 << i;
                    if ((current & b) != 0) {
                        element.add(input.get(i));
                    }
                }
                current++;
                return element;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                        "May not remove elements from a power set");
            }
        };
    }
}

//From https://github.com/javagl/Combinatorics
class PermutationIterable<T> implements Iterable<List<T>> {
    public static int factorial(int n) {
        int f = 1;
        for (int i = 2; i <= n; i++) {
            f = f * i;
        }
        return f;
    }

    private final List<T> input;
    private final int numPermutations;

    public PermutationIterable(List<T> input) {
        this.input = input;
        numPermutations = factorial(input.size());
    }

    @Override
    public Iterator<List<T>> iterator() {
        if (input.isEmpty()) {
            return Collections.singletonList(Collections.<T>emptyList()).iterator();
        }

        return new Iterator<List<T>>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < numPermutations;
            }

            @Override
            public List<T> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }
                // Adapted from http://en.wikipedia.org/wiki/Permutation
                List<T> result = new ArrayList<T>(input);
                int factorial = numPermutations / input.size();
                for (int i = 0; i < result.size() - 1; i++) {
                    int tempIndex = (current / factorial) % (result.size() - i);
                    T temp = result.get(i + tempIndex);
                    for (int j = i + tempIndex; j > i; j--) {
                        result.set(j, result.get(j - 1));
                    }
                    result.set(i, temp);
                    factorial /= (result.size() - (i + 1));
                }
                current++;
                return result;
            }
        };
    }
}
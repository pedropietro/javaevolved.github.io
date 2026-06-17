///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

/// Proof: files-walk
/// Source: content/io/files-walk.yaml
void printJavaFiles(Path root) throws IOException {
    try (Stream<Path> paths = Files.walk(root)) {
        paths
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".java"))
            .forEach(System.out::println);
    }
}

void main() {}

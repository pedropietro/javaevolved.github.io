///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.util.*;

/// Proof: local-records
/// Source: content/language/local-records.yaml
record Order(String department, long total) {}

void print(List<Order> orders) {
    record Row(String department, long total) {}

    orders.stream()
        .map(o -> new Row(o.department(), o.total()))
        .forEach(System.out::println);
}

void main() {}

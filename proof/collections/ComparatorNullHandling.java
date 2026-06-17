///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.util.*;

/// Proof: comparator-null-handling
/// Source: content/collections/comparator-null-handling.yaml
void sortNames(List<String> names) {
    names.sort(
        Comparator.nullsLast(
            String.CASE_INSENSITIVE_ORDER));
}

void main() {}

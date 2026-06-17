///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.util.*;

/// Proof: string-join
/// Source: content/strings/string-join.yaml
String joinNames(List<String> names) {
    String result = String.join(", ", names);
    return result;
}

void main() {}

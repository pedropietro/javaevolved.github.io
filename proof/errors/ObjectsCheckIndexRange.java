///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.util.*;

/// Proof: objects-check-index-range
/// Source: content/errors/objects-check-index-range.yaml
byte[] slice(byte[] bytes, int offset, int length) {
    int from = Objects.checkFromIndexSize(
        offset, length, bytes.length);

    byte[] part = Arrays.copyOfRange(
        bytes, from, from + length);
    return part;
}

void main() {}

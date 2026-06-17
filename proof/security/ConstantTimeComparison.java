///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.security.*;

/// Proof: constant-time-comparison
/// Source: content/security/constant-time-comparison.yaml
void verify(byte[] expectedMac, byte[] actualMac) {
    if (MessageDigest.isEqual(
            expectedMac, actualMac)) {
        authenticate();
    }
}

void authenticate() {}

void main() {}

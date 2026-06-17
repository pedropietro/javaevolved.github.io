///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.time.*;

/// Proof: clock-for-testable-time
/// Source: content/datetime/clock-for-testable-time.yaml
class TokenService {
    private final Clock clock;

    TokenService(Clock clock) {
        this.clock = clock;
    }

    boolean expired(Instant expiresAt) {
        return Instant.now(clock).isAfter(expiresAt);
    }
}

void main() {}

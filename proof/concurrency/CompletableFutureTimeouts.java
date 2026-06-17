///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.util.concurrent.*;

/// Proof: completablefuture-timeouts
/// Source: content/concurrency/completablefuture-timeouts.yaml
record User(long id) {}

CompletableFuture<User> fetchUser(long id) {
    return CompletableFuture.completedFuture(new User(id));
}

CompletableFuture<User> withTimeout(long id) {
    CompletableFuture<User> result =
        fetchUser(id)
            .orTimeout(2, TimeUnit.SECONDS);
    return result;
}

void main() {}

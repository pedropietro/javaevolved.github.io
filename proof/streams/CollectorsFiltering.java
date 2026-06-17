///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+

import java.util.*;
import java.util.stream.*;

/// Proof: collectors-filtering
/// Source: content/streams/collectors-filtering.yaml
record User(String team, boolean active) {}

Map<String, List<User>> activeByTeam(List<User> users) {
    Map<String, List<User>> activeByTeam =
        users.stream()
            .collect(Collectors.groupingBy(
                User::team,
                Collectors.filtering(
                    User::active,
                    Collectors.toList())));
    return activeByTeam;
}

void main() {}

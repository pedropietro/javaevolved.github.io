///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+
//DEPS jakarta.validation:jakarta.validation-api:3.1.1

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/// Proof: jakarta-bean-validation
/// Source: content/enterprise/jakarta-bean-validation.yaml
record UserDto(
    @Email @NotBlank String email,
    @Min(18) int age) {}

void save(UserDto user) {}

public void register(@Valid UserDto user) {
    save(user);
}

void main() {}

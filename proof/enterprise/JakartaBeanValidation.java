///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25+
//DEPS jakarta.validation:jakarta.validation-api:3.1.0

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/// Proof: jakarta-bean-validation
/// Source: content/enterprise/jakarta-bean-validation.yaml
record UserDto(
    @Email @NotBlank String email,
    @Min(18) int age) {}

void save(UserDto user) {}

void register(@Valid UserDto user) {
    save(user);
}

void main() {}

package com.crimsonwarpedcraft.exampleplugin.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

/**
 * Loads and validates the plugin configuration from disk.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public final class ConfigManager {

  private final ObjectMapper mapper;
  private final Validator validator;

  /**
   * Creates a ConfigManager with the given mapper and validator.
   *
   * <p>Intended for testing — production code should use {@link #ConfigManager()}.
   *
   * @param mapper the Jackson ObjectMapper to use for deserialization
   * @param validator the Jakarta Validator to use for constraint checking
   */
  ConfigManager(ObjectMapper mapper, Validator validator) {
    this.mapper = Objects.requireNonNull(mapper);
    this.validator = Objects.requireNonNull(validator);
  }

  /** Creates a ConfigManager with the default YAML mapper and validator. */
  public ConfigManager() {
    this(buildObjectMapper(), buildValidator());
  }

  /**
   * Loads and validates the given file as the given config type.
   *
   * @param <T> the config type
   * @param configFile the file to load
   * @param clazz the config class
   * @return the validated configuration
   * @throws IOException if the file cannot be read or parsed
   * @throws IllegalStateException if the config fails validation
   */
  public <T extends Config> T load(File configFile, Class<T> clazz) throws IOException {
    T config = mapper.readValue(Objects.requireNonNull(configFile), Objects.requireNonNull(clazz));
    validate(config);
    return config;
  }

  /**
   * Loads and validates {@code configFile} as a {@link PluginConfig}.
   *
   * @param configFile the file to load
   * @return the validated configuration
   * @throws IOException if the file cannot be read or parsed
   * @throws IllegalStateException if the config fails validation
   */
  public PluginConfig loadPluginConfig(File configFile) throws IOException {
    return load(configFile, PluginConfig.class);
  }

  /**
   * Validates the given configuration object against its declared constraints.
   *
   * @param <T> the config type
   * @param config the configuration to validate
   * @throws IllegalStateException if any constraint is violated
   */
  public <T extends Config> void validate(T config) {
    Set<ConstraintViolation<T>> violations = validator.validate(Objects.requireNonNull(config));
    if (!violations.isEmpty()) {
      String message = violations.stream()
          .map(v -> v.getPropertyPath() + ": " + v.getMessage())
          .collect(Collectors.joining(", "));
      throw new IllegalStateException("Invalid configuration: " + message);
    }
  }

  private static ObjectMapper buildObjectMapper() {
    return new ObjectMapper(new YAMLFactory())
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  private static Validator buildValidator() {
    try (ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(new ParameterMessageInterpolator())
        .buildValidatorFactory()) {
      return factory.getValidator();
    }
  }
}

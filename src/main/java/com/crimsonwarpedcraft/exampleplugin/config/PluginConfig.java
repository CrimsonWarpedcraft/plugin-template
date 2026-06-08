package com.crimsonwarpedcraft.exampleplugin.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the plugin configuration loaded from config.yml.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginConfig implements Config {

  @NotBlank
  @JsonProperty("pong-message")
  private String pongMessage = "Pong!";

  @NotBlank
  @JsonProperty("greet-message")
  private String greetMessage = "Hello, {player}!";

  PluginConfig() {}

  PluginConfig(String pongMessage, String greetMessage) {
    this.pongMessage = pongMessage;
    this.greetMessage = greetMessage;
  }

  public String getPongMessage() {
    return pongMessage;
  }

  public String getGreetMessage() {
    return greetMessage;
  }
}

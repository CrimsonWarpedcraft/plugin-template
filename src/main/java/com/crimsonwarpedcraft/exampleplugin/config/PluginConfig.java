package com.crimsonwarpedcraft.exampleplugin.config;

import com.crimsonwarpedcraft.cwcommons.config.Config;
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

  @NotBlank
  @JsonProperty("creepers-killed-message")
  private String creepersKilledMessage = "You have killed {count} creeper(s)!";

  PluginConfig() {}

  PluginConfig(String pongMessage, String greetMessage) {
    this.pongMessage = pongMessage;
    this.greetMessage = greetMessage;
  }

  PluginConfig(String pongMessage, String greetMessage, String creepersKilledMessage) {
    this(pongMessage, greetMessage);
    this.creepersKilledMessage = creepersKilledMessage;
  }

  public String getPongMessage() {
    return pongMessage;
  }

  public String getGreetMessage() {
    return greetMessage;
  }

  public String getCreepersKilledMessage() {
    return creepersKilledMessage;
  }
}

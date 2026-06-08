package com.crimsonwarpedcraft.exampleplugin.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ConfigManagerTest {

  @Test
  void loadPropagatesIoException() throws IOException {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    doThrow(new IOException("read failed"))
        .when(mockMapper).readValue(any(File.class), eq(PluginConfig.class));
    ConfigManager manager = new ConfigManager(mockMapper, mock(Validator.class));
    assertThrows(IOException.class,
        () -> manager.load(new File("anything.yml"), PluginConfig.class));
  }

  @Test
  void validateAcceptsValidConfig() {
    assertDoesNotThrow(() -> new ConfigManager().validate(new PluginConfig()));
  }

  @Test
  void validateThrowsOnViolations() {
    PluginConfig config = new PluginConfig("", "");
    assertThrows(IllegalStateException.class,
        () -> new ConfigManager().validate(config));
  }

  @Test
  void loadsConfigFromFile() throws IOException {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    PluginConfig expected = new PluginConfig();
    doReturn(expected).when(mockMapper).readValue(any(File.class), eq(PluginConfig.class));
    ConfigManager manager = spy(new ConfigManager(mockMapper, mock(Validator.class)));
    doNothing().when(manager).validate(any());
    PluginConfig result = manager.load(new File("anything.yml"), PluginConfig.class);
    assertSame(expected, result);
    verify(manager).validate(expected);
  }

  @Test
  void loadPropagatesValidationFailure() throws IOException {
    ObjectMapper mockMapper = mock(ObjectMapper.class);
    doReturn(new PluginConfig())
        .when(mockMapper).readValue(any(File.class), eq(PluginConfig.class));
    ConfigManager manager = spy(new ConfigManager(mockMapper, mock(Validator.class)));
    doThrow(new IllegalStateException("invalid")).when(manager).validate(any());
    assertThrows(IllegalStateException.class,
        () -> manager.load(new File("anything.yml"), PluginConfig.class));
  }
}

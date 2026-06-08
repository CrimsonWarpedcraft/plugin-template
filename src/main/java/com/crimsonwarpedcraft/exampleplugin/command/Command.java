package com.crimsonwarpedcraft.exampleplugin.command;

/**
 * Represents a plugin command that can be registered with the server.
 *
 * @author Copyright (c) Levi Muniz. All Rights Reserved.
 */
public interface Command {

  /**
   * Registers this command with the server.
   *
   * @return this command instance, for chaining
   */
  Command register();
}

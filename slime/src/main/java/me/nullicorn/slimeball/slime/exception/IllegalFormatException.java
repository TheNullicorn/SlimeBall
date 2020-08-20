package me.nullicorn.slimeball.slime.exception;

import java.io.IOException;

/**
 * Thrown when slime data in in an invalid format
 *
 * @author Nullicorn
 */
public class IllegalFormatException extends IOException {

  public IllegalFormatException() {
  }

  public IllegalFormatException(String message) {
    super(message);
  }

  public IllegalFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalFormatException(Throwable cause) {
    super(cause);
  }
}

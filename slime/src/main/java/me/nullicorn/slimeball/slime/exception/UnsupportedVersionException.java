package me.nullicorn.slimeball.slime.exception;

/**
 * Thrown when an unsupported version of the slime format is detected
 *
 * @author Nullicorn
 */
public class UnsupportedVersionException extends IllegalFormatException {

  public UnsupportedVersionException() {
  }

  public UnsupportedVersionException(int version) {
    this(String.format("Slime version '%s' is not supported", version));
  }

  public UnsupportedVersionException(String message) {
    super(message);
  }

  public UnsupportedVersionException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsupportedVersionException(Throwable cause) {
    super(cause);
  }
}

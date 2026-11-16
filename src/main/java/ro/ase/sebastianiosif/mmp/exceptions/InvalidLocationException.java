package ro.ase.sebastianiosif.mmp.exceptions;

/** Signals an invalid or unreadable directory selected by the user. */
public class InvalidLocationException extends RuntimeException {

    public InvalidLocationException(String message) {
        super(message);
    }

    public InvalidLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLocationException(Throwable cause) {
        super(cause);
    }
}
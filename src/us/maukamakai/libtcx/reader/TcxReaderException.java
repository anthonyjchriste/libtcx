package us.maukamakai.libtcx.reader;

public class TcxReaderException extends RuntimeException {
    public TcxReaderException(String message) {
        super(message);
    }

    public TcxReaderException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

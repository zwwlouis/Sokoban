package model;

/**
 * @author WeiWang Zhang
 * @date 2018/3/30.
 * @time 19:43.
 */
public class SokobanException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 8507110316768164130L;

    /**
     *
     */
    public SokobanException() {
    }

    /**
     * @param message
     */
    public SokobanException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public SokobanException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public SokobanException(String message, Throwable cause) {
        super(message, cause);
    }
}

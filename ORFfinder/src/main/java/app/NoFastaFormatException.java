package app;

public class NoFastaFormatException extends Exception {

    private String message;
    public NoFastaFormatException() {
        super();

    }
    public NoFastaFormatException(String error) {
        super(error);
    }
}

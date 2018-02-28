package org.cornutum.crescent.page;

/**
 * Thrown when page contents are in an invalid state.
 */
public class InvalidStateException extends PageException {
    
    private String reason;

    private static final long serialVersionUID = 509179763749551551L;

    /**
     * Creates a new InvalidStateException object.
     */
    public InvalidStateException( Page<?> page, String reason, Throwable cause) {
        super( page, cause);
        setReason( reason);
    }
  
    /**
     * Creates a new InvalidStateException object.
     */
    public InvalidStateException( Page<?> page, Throwable cause) {
        this( page, null, cause);
    }
  
    /**
     * Creates a new InvalidStateException object.
     */
    public InvalidStateException( Page<?> page, String reason) {
        this( page, reason, null);
    }

    /**
     * Changes the reason for the failure.
     */
    public void setReason( String reason) {
        this.reason = reason;
    }

    /**
     * Returns the reason for the failure.
     */
    public String getReason() {
        return reason;
    }

    public String getMessage() {
        StringBuilder msg = new StringBuilder();

        msg.append( getPage());
        if( getReason() != null) {
            msg.append( ": ").append( getReason());
        }
    
        return msg.toString();
    }
}


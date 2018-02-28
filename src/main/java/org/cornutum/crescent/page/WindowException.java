package org.cornutum.crescent.page;

/**
 * Thrown on failure to create a new window.
 */
public class WindowException extends PageException {
    
	private String reason;

	private static final long serialVersionUID = -3409076828257287540L;

    /**
     * Creates a new WindowException object.
     */
    public WindowException( Page<?> page, String reason, Throwable cause) {
        super( page, cause);
        setReason( reason);
    }
  
    /**
     * Creates a new WindowException object.
     */
    public WindowException( Page<?> page, Throwable cause) {
        this( page, null, cause);
    }
  
    /**
     * Creates a new WindowException object.
     */
    public WindowException( Page<?> page, String reason) {
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


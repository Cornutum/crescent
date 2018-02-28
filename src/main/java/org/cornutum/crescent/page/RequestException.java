package org.cornutum.crescent.page;

/**
 * Thrown when a user request cannot be completed.
 */
public class RequestException extends PageException {
    
    private String request;
    private String reason;
  
    private static final long serialVersionUID = 7996308489016292147L;

    /**
     * Creates a new RequestException object.
     */
    public RequestException( Page<?> page, String request, String reason, Throwable cause) {
        super( page, cause);
        setRequest( request);
        setReason( reason);
    }
  
    /**
     * Creates a new RequestException object.
     */
    public RequestException( Page<?> page, String request, Throwable cause) {
        this( page, request, null, cause);
    }
  
    /**
     * Creates a new RequestException object.
     */
    public RequestException( Page<?> page, String request, String reason) {
        this( page, request, reason, null);
    }

    /**
     * Changes the request that failed.
     */
    public void setRequest( String request) {
        this.request = request;
    }

    /**
     * Returns the request that failed.
     */
    public String getRequest() {
        return request;
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

        msg
            .append( getPage())
            .append( ", Can't ")
            .append( getRequest());

        if( getReason() != null) {
            msg
                .append( ": ")
                .append( getReason());
        }
    
        return msg.toString();
    }
}


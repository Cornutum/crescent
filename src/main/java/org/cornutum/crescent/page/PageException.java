package org.cornutum.crescent.page;

/**
 * Base class for page content exceptions.
 */
public abstract class PageException extends RuntimeException {

    private Page<?> page;

    private static final long serialVersionUID = 988785294044951096L;

    /**
     * Creates a new PageException object.
     */
    public PageException( Page<?> page, Throwable cause) {
        super( cause);
        setPage( page);
    }
  
    /**
     * Creates a new PageException object.
     */
    public PageException( Page<?> page) {
        this( page, null);
    }

    /**
     * Changes the page where this failure occurred.
     */
    public void setPage( Page<?> page) {
        this.page = page;
    }

    /**
     * Returns the page where this failure occurred.
     */
    public Page<?> getPage() {
        return page;
    }
}


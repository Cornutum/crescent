package org.cornutum.crescent.page;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a Selenium window handle.
 */
public class WindowHandle {

    private final String handle;
    
    /**
     * Creates a new WindowHandle object.
     */
    public WindowHandle( String handle) {
        this.handle = checkNotNull( handle, "Window handle cannot be null");
    }

    public int hashCode() {
        return handle.hashCode();
    }

    public boolean equals( Object object) {
        WindowHandle other =
            object != null && object.getClass().equals( getClass())
            ? (WindowHandle) object
            : null;

        return
            other != null
            && other.handle.equals( handle);
    }

    public String toString() {
        return handle;
    }
}

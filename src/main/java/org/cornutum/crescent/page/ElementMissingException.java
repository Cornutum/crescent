package org.cornutum.crescent.page;

import org.openqa.selenium.By;

/**
 * Thrown when a page element cannot be located.
 */
public class ElementMissingException extends PageException {

    private By locator;

    private static final long serialVersionUID = 7088789334285466574L;

    /**
     * Creates a new ElementMissingException object.
     */
    public ElementMissingException( Page page, By locator) {
        super( page);
        setLocator( locator);
    }

    /**
     * Changes the locator where this failure occurred.
     */
    public void setLocator( By locator) {
        this.locator = locator;
    }

    /**
     * Returns the locator where this failure occurred.
     */
    public By getLocator() {
        return locator;
    }

    public String getMessage() {
        return
            new StringBuilder()
            .append( getPage())
            .append( ", can't find element at locator=")
            .append( getLocator())
            .toString();
    }
}


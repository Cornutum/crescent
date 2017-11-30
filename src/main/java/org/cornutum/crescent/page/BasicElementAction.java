package org.cornutum.crescent.page;

import org.openqa.selenium.WebElement;

/**
 * An {@link ElementAction} that produces no new result -- simply returns the current page.
 */
public class BasicElementAction extends ElementAction<Page,Page> {

    /**
     * Creates a new BasicElementAction object.
     */
    public BasicElementAction( Page page, WebElement element) {
        super( page, element);
    }

    /**
     * Returns the results of this action in the given source window.
     */
    protected Page fromSource( Page source) {
        return source;
    }
}

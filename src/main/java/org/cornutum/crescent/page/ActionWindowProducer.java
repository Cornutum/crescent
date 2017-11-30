package org.cornutum.crescent.page;

import org.openqa.selenium.WebElement;

/**
 * Produces a new window by performing an action on a specific element.
 */
public class ActionWindowProducer extends WindowProducer {

    private final WebElement element;
    
    /**
     * Creates a new ActionWindowProducer object.
     */
    public ActionWindowProducer( Page page, WebElement element) {
        super( page);
        this.element = element;
    }

    /**
     * Performs the action that requests the new content.
     */
    protected void get() {
        // By default, click on the element to produce a new window.
        getElement().click();
    }

    /**
     * Returns the element responsible for producing a new window.
     */
    protected WebElement getElement() {
        return element;
    }

}

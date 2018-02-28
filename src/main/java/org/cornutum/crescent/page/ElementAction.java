package org.cornutum.crescent.page;

import org.openqa.selenium.WebElement;

/**
 * A {@link PageAction} that produces results by directly activating an element.
 */
public abstract class ElementAction<P extends Page<?>,T> extends PageAction<P,T> {

    /**
     * Creates a new ElementAction object.
     */
    protected ElementAction( P page, WebElement element) {
        super( page, element);
    }

    /**
     * Activates the given element.
     */
    protected void activate( WebElement element) {
        // By default, activate by clicking.
        element.click();
    }

    /**
     * Performs this action.
     */
    protected T perform( P page, WebElement element) {
        activate( element);
        return fromSource( page);
    }

    /**
     * Returns the results of this action in the given source window.
     */
    protected abstract T fromSource( P source);
}

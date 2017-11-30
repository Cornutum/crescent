package org.cornutum.crescent.page;

import org.openqa.selenium.WebElement;

/**
 * A {@link PageAction} that produces content in a new window.
 */
public abstract class NewWindowAction<P extends Page,T> extends PageAction<P,T> {

    /**
     * Creates a new NewWindowAction object.
     */
    protected NewWindowAction( P page, WebElement element) {
        super( page, element);
    }

    /**
     * Returns the WindowProducer for this action.
     */
    protected WindowProducer getWindowProducer( P page, WebElement element) {
        return new ActionWindowProducer( page, element);
    }

    /**
     * Performs this action.
     */
    protected T perform( P page, WebElement element) {
        return withNewWindow( page, getWindowProducer( page, element).open());
    }

    /**
     * Returns the results of this action in the given window.
     */
    protected abstract T withNewWindow( P source, WindowHandle window);
}

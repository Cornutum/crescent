package org.cornutum.crescent.page;

import org.cornutum.crescent.util.ToString;

import org.openqa.selenium.WebElement;

/**
 * Represents a page element that performs an action.
 */
public abstract class PageAction<P extends Page,T> {

    private final P page;
    private final WebElement element;
    
    /**
     * Creates a new PageAction object.
     */
    protected PageAction( P page, WebElement element) {
        this.page = page;
        this.element = element;
    }

    /**
     * Performs this action and returns the result.
     */
    protected abstract T perform( P page, WebElement element);

    /**
     * Performs this action and returns the result.
     */
    public T perform() {
        return perform( page, element);
    }

    /**
     * Returns the WebElement that performs this action.
     */
    public WebElement getElement() {
        return element;
    }

    /**
     * Returns the page that is the source of this action.
     */
    public P getSource() {
        return page;
    }

    public String toString() {
        return
            ToString.getBuilder( this)
            .append( element)
            .toString();
    }
}

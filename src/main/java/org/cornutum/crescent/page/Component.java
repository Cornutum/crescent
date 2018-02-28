package org.cornutum.crescent.page;

import org.cornutum.crescent.util.ToString;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Base class for a UI component common to multiple {@link Page} types.
 */
public abstract class Component<P extends Page<?>> {

    private final P page;
    private final SearchContext container;
    
    /**
     * Creates a new Component object.
     */
    protected Component( P page) {
        this( page, page.getDriver());
    }
    
    /**
     * Creates a new Component object.
     */
    protected Component( P page, SearchContext container) {
        this.page = page;
        this.container = container;
    }

    /**
     * Returns the page that contains this component.
     */
    public P getPage() {
        return page;
    }

    /**
     * Returns the Finder for this component.
     */
    protected Finder finder() {
        return page.finder().startingAt( container);
    }

    /**
     * Returns a Finder that starts from the given element.
     */
    protected Finder startingAt( WebElement element) {
        return finder().startingAt( element);
    }

    /**
     * Returns a Finder that waits for the given timeout interval.
     */
    protected Finder waitingFor( long duration, TimeUnit unit) {
        return finder().waitingFor( duration, unit);
    }

    /**
     * Returns a Finder that waits for the given time interval for an expected condition to remain unchanged.
     */
    protected Finder whenStableFor( long duration, TimeUnit unit) {
        return finder().whenStableFor( duration, unit);
    }

    /**
     * Returns a Finder that looks for elements satisfying the given condition.
     */
    protected Finder when( Predicate<WebElement> condition) {
        return finder().when( condition);
    }

    public String toString() {
        return
            ToString.getBuilder( this)
            .toString();
    }
}

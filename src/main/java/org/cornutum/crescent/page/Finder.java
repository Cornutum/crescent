package org.cornutum.crescent.page;

import org.cornutum.crescent.util.ToString;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;

/**
 * Searches for page elements with a specified timeout.
 */
public class Finder {

    private Page<?> page;
    private SearchContext root;
    private long timeout;
    private long interval;
    private long minStable;
    private Predicate<WebElement> condition;

    /**
     * Creates a new Finder object.
     */
    public Finder( Page<?> page) {
        this( page, page.getMaxAppWait());
    }

    /**
     * Creates a new Finder object.
     */
    public Finder( Page<?> page, long timeout) {
        this( page, timeout, getDefaultInterval( timeout));
    }

    /**
     * Creates a new Finder object.
     */
    public Finder( Page<?> page, long timeout, long interval) {
        setPage( page);
        setRoot( page.getDriver());
        setTimeout( timeout);
        setInterval( interval);
        setMinStable( getDefaultMinStable( getInterval()));
        setCondition( PageUtils.always);
    }

    /**
     * Returns the element identified by the given locator. Throws an {@link ElementMissingException} if not found.
     */
    public WebElement findElement( final By locator) {
        try {
            return find( new AnyElement( locator, getCondition()));
        }
        catch( TimeoutException te) {
            throw new ElementMissingException( getPage(), locator);
        }
    }

    /**
     * Returns the visible element identified by the given locator. Throws an {@link ElementMissingException} if not found.
     */
    public WebElement findVisibleElement( By locator) {
        return when( PageUtils.isVisible).findElement( locator);
    }

    /**
     * Returns the element identified by the given locator.
     */
    public Optional<WebElement> findOptionalElement( By locator) {
        WebElement found;
        try {
            found = findElement( locator);
        }
        catch( ElementMissingException ignored) {
            found = null;
        }

        return Optional.ofNullable( found);
    }

    /**
     * Returns the elements identified by the given locator.
     */
    public List<WebElement> findElements( By locator) {
        List<WebElement> found;
        AllElements allElements = new AllElements( locator, getCondition(), getRequestWait( getMinStable()));
        try {
            found = find( allElements);
        }
        catch( TimeoutException te) {
            found = allElements.getFound();
        }

        return found;
    }

    /**
     * Returns the visible elements identified by the given locator.
     */
    public List<WebElement> findVisibleElements( By locator) {
        return when( PageUtils.isVisible).findElements( locator);
    }

    /**
     * Returns when there are no elements identified by the given locator.
     */
    public void awaitNoElements( By locator) {
        try {
            find( new NoElements( locator, getCondition(), getRequestWait( getMinStable())));
        }
        catch( TimeoutException te) {
            throw new InvalidStateException( getPage(), "Matching elements still found for locator=" + locator);
        }
    }

    /**
     * Returns the results of the given finder function. Throws TimeoutException if not found.
     */
    private <V> V find( Function<SearchContext,V> finder) {
        return
            getWait()
            .ignoring( NoSuchElementException.class, StaleElementReferenceException.class)
            .until( finder);
    }

    /**
     * Changes the page for this finder.
     */
    private void setPage( Page<?> page) {
        this.page = page;
    }

    /**
     * Returns the page for this finder.
     */
    private Page<?> getPage() {
        return page;
    }

    /**
     * Changes the root search context for this finder.
     */
    public Finder startingAt( SearchContext root) {
        setRoot( root);
        return this;
    }

    /**
     * Changes the root search context for this finder.
     */
    public void setRoot( SearchContext root) {
        this.root = root;
    }

    /**
     * Returns the root search context for this finder.
     */
    public SearchContext getRoot() {
        return root;
    }

    /**
     * Changes the timeout interval.
     */
    public Finder waitingFor( long duration, TimeUnit unit) {
        setTimeout( unit.toMillis( duration));
        setInterval( getDefaultInterval( getTimeout()));
        return this;
    }

    /**
     * Changes the timeout interval (in milliseconds).
     */
    public void setTimeout( long timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the timeout interval (in milliseconds).
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Changes the polling interval.
     */
    public Finder checkingEvery( long duration, TimeUnit unit) {
        setInterval( unit.toMillis( duration));
        return this;
    }

    /**
     * Changes the polling interval (in milliseconds).
     */
    public void setInterval( long interval) {
        this.interval = interval;
    }

    /**
     * Returns the polling interval (in milliseconds).
     */
    public long getInterval() {
        return interval;
    }

    /**
     * Changes the minimum time that the value of {@link #getCondition the expected condition} must
     * remain unchanged before it is considered final.
     */
    public Finder whenStableFor( long duration, TimeUnit unit) {
        setMinStable( unit.toMillis( duration));
        return this;
    }

    /**
     * Changes the minimum time (in milliseconds) that the value of {@link #getCondition the expected condition} must
     * remain unchanged before it is considered final.
     */
    public void setMinStable( long minStable) {
        this.minStable = minStable;
    }

    /**
     * Returns the minimum time (in milliseconds) that the value of {@link #getCondition the expected condition} must
     * remain unchanged before it is considered final.
     */
    public long getMinStable() {
        return minStable;
    }

    /**
     * Changes the condition that must be satisfied by the element(s) returned.
     */
    public Finder when( Predicate<WebElement> condition) {
        setCondition( condition);
        return this;
    }

    /**
     * Changes the condition that must be satisfied by the element(s) returned.
     */
    public void setCondition( Predicate<WebElement> condition) {
        this.condition =
            condition == null
            ? PageUtils.always
            : condition;
    }

    /**
     * Returns the condition that must be satisfied by the element(s) returned.
     */
    public Predicate<WebElement> getCondition() {
        return condition;
    }

    /**
     * Given a timeout interval (in milliseconds), returns a default polling interval (in milliseconds)
     */
    private static long getDefaultInterval( long timeout) {
        return Math.min( timeout/4, 500);
    }

    /**
     * Given a polling interval (in milliseconds), returns a default minimum stability interval (in milliseconds)
     */
    private static long getDefaultMinStable( long interval) {
        return interval*2;
    }

    /**
     * Given the current {@link #getInterval polling} and {@link #getMinStable stability} intervals, returns the number of polls
     * needed to confirm stability. 
     */
    public int getMinStableIntervals() {
        return (int) (getRequestWait( getMinStable()) / getRequestWait( getInterval()));
    }

    /**
     * Returns the effective duration for the given wait interval (in milliseconds).
     */
    private long getRequestWait( long interval) {
        return getPage().getSite().getRequestWait( interval);
    }

    /**
     * Using the wait controls defined by this finder, returns a generic interface to await results from the given source object.
     */
    public <T> FluentWait<T> await( T source) {
        return
            new FluentWait<T>( source)
            .pollingEvery( getRequestWait( getInterval()), TimeUnit.MILLISECONDS)
            .withTimeout( getRequestWait( getTimeout()), TimeUnit.MILLISECONDS);
    }

    /**
     * Using the wait controls defined by this finder, returns an interface to await results from the root search context.
     */
    private FluentWait<SearchContext> getWait() {
        return await( getRoot());
    }

    public String toString() {
        return
            ToString.getBuilder( this)
            .append( getPage())
            .append( "timeout", getTimeout())
            .append( "interval", getInterval())
            .append( "latency", getPage().getSite().getDriverLatencyFactor())
            .toString();
    }

    /**
     * Returns any matching element.
     */
    private static class AnyElement implements Function<SearchContext,WebElement> {

        private final By locator;
        private final Predicate<WebElement> condition;
        
        /**
         * Creates a new AnyElement object.
         */
        public AnyElement( By locator, Predicate<WebElement> condition) {
            this.locator = locator;
            this.condition = condition;
        }

        public WebElement apply( SearchContext root) {
            WebElement found = root.findElement( locator);
            return condition.test( found)? found : null;
        }
    }

    /**
     * Returns all matching elements, waiting until matches have been found.
     */
    private static class AllElements implements Function<SearchContext,List<WebElement>> {

        private final By locator;
        private final Predicate<WebElement> condition;
        private final long stableMin;
        private int matches;
        private long stableStart;
        private List<WebElement> found = Collections.emptyList();
        
        /**
         * Creates a new AllElements object.
         */
        public AllElements( By locator, Predicate<WebElement> condition, long stableMin) {
            this.locator = locator;
            this.condition = condition;
            this.stableMin = stableMin;
            this.matches = 0;
            this.stableStart = 0;
        }

        public List<WebElement> apply( SearchContext root) {
            long findTime = System.currentTimeMillis();

            found =
                root.findElements( locator)
                .stream()
                .filter( condition)
                .collect( toList());

            int prevMatches = matches;
            matches = found.size();

            // Still waiting to find at least one match?
            if( !(stableStart == 0 && matches == 0)) {
                // No, but have matches changed?
                if( matches != prevMatches) {
                    // Yes, reset and resume waiting for count to stabilize
                    stableStart = findTime;
                }
            }

            // Keep looking for matches until we've found at least some matches and
            // the number of matches has stabilized.
            return
                stableStart > 0 && (findTime - stableStart) >= stableMin
                ? found
                : null;
        }

        public List<WebElement> getFound() {
            return found;
        }
    }

    /**
     * Returns true when no matches have been found.
     */
    private static class NoElements implements Function<SearchContext,Boolean> {

        private final By locator;
        private final Predicate<WebElement> condition;
        private final long stableMin;
        private long stableStart;
        private boolean found;
        
        /**
         * Creates a new NoElements object.
         */
        public NoElements( By locator, Predicate<WebElement> condition, long stableMin) {
            this.locator = locator;
            this.condition = condition;
            this.stableMin = stableMin;
            this.stableStart = 0;
            this.found = true;
        }

        public Boolean apply( SearchContext root) {
            boolean prevFound = found;
            long findTime = System.currentTimeMillis();

            found =
                root.findElements( locator)
                .stream()
                .anyMatch( condition);

            // Has the presence of a matching element changed?
            if( found != prevFound) {
                // Yes, matching element found?
                stableStart =
                    found
                    // Yes, reset and resume waiting for it to go away
                    ? 0

                    // No, start waiting for this to stabilize
                    : findTime;
            }

            // Keep looking for matches until we've found no matches and
            // the number of matches has stabilized.
            return stableStart > 0 && (findTime - stableStart) >= stableMin;
        }
    }
}

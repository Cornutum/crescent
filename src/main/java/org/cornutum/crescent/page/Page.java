package org.cornutum.crescent.page;

import org.cornutum.crescent.util.ToString;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Base class for page model implementations.
 */
public abstract class Page {
  
    private long maxAppWait;
    private Site site;
    private URI uri;
    private WindowHandle window;
    private Page parent;

    /**
     * Creates a new Page object.
     */
    public Page( Site site) {
        this( site, null, null);
    }
  
    /**
     * Creates a new Page object.
     */
    public Page( Site site, String uri) {
        this( site, null, Site.toURI( uri));
    }
  
    /**
     * Creates a new Page object.
     */
    public Page( Site site, URI uri) {
        this( site, null, uri);
    }
  
    /**
     * Creates a new Page object.
     */
    public Page( Site site, WindowHandle window) {
        this( site, window, null);
    }

    /**
     * Creates a new Page object.
     */
    public Page( Page parent) {
        this( parent, null, null);
    }
  
    /**
     * Creates a new Page object.
     */
    public Page( Page parent, String uri) {
        this( parent, null, Site.toURI( uri));
    }
  
    /**
     * Creates a new Page object.
     */
    public Page( Page parent, URI uri) {
        this( parent, null, uri);
    }
  
    /**
     * Creates a new Page object.
     */
    public Page( Page parent, WindowHandle window) {
        this( parent, window, null);
    }
  
    /**
     * Creates a new Page object.
     */
    protected Page( Page parent, WindowHandle window, URI uri) {
        this( parent.getSite(), parent, window, uri);
    }
  
    /**
     * Creates a new Page object.
     */
    protected Page( Site site, WindowHandle window, URI uri) {
        this( site, null, window, uri);
    }
  
    /**
     * Creates a new Page object.
     */
    protected Page( Site site, Page parent, WindowHandle window, URI uri) {
        setSite( site);
        setParent( parent);
        setMaxAppWait( site.getMaxAppWait());
        setWindow( window);
        setURI( uri);

        initPage();
    }

    /**
     * Changes the Site for this page.
     */
    public void setSite( Site site) {
        this.site = site;
    }

    /**
     * Returns the Site for this page.
     */
    public Site getSite() {
        return site;
    }

    /**
     * Returns the WebDriver for this page.
     */
    public WebDriver getDriver() {
        return getSite().getDriver();
    }

    /**
     * Changes the window handle for this page.
     */
    public void setWindow( WindowHandle window) {
        this.window = window;
    }

    /**
     * Returns the window handle for this page.
     */
    public WindowHandle getWindow() {
        return window;
    }

    /**
     * Changes the URI for this page.
     */
    public void setURI( URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the URI for this page.
     */
    public URI getURI() {
        return uri;
    }

    /**
     * Changes the parent of this page
     */
    public void setParent( Page parent) {
        this.parent = parent;
    }

    /**
     * Returns the parent of this page
     */
    public Page getParent() {
        return parent;
    }

    /**
     * Changes the maximum timeout (in milliseconds) to wait for the app to update elements on this page.
     */
    public void setMaxAppWait( long maxWait) {
        this.maxAppWait = maxWait;
    }

    /**
     * Returns the maximum timeout (in milliseconds) to wait for the app to update elements on this page.
     */
    public long getMaxAppWait() {
        return maxAppWait;
    }

    /**
     * Returns the effective maximum timeout (in milliseconds) for WebDriver requests for this page.
     */
    public long getMaxWait() {
        return getSite().getRequestWait( getMaxAppWait());
    }

    /**
     * Performs any post-construction initialization.
     */
    protected void initPage() {
        // By default, visit this page.
        visit();
    }

    /**
     * Initiates interaction with this page.
     */
    public void visit() {
        // Opened in new window?
        WebDriver driver = getDriver();
        WindowHandle window = getWindow();
        if( window != null) {
            // Yes, visit new window.
            driver.switchTo().window( window.toString());

            // Recover actual URI from window visited.
            setURI( null);
        }

        // URI defined?
        URI uri = getURI();
        if( uri != null) {
            // Yes, navigate to this URI.
            driver.get( getSite().getURI().resolve( uri).toASCIIString());
        }
        else {
            // No, acquire URI from current location.
            try {
                setURI( new URI( driver.getCurrentUrl()));
            }
            catch( Exception e) {
                throw new RuntimeException( String.valueOf( this) + ": Can't get new page URI", e);
            }
        }
    
        if( window == null) {
            setWindow( new WindowHandle( driver.getWindowHandle()));
        }

        visited();
    }

    /**
     * Notifies that this page has been visited. Performs any re-initialization needed for this page.
     */
    protected void visited() {
        // By default, nothing to do
    }

    /**
     * Moves back to the previous page in the browser history.
     */
    public Page back() {
        Page parent = getParent();

        if( parent == null || !parent.getWindow().equals( getWindow())) {
            throw new RequestException( this, "back", "No previous page known in the browser history for this window");
        }

        try {
            getDriver().navigate().back();
            parent.visited();
        }
        catch( Exception e) {
            throw new RequestException( this, "back", e);
        }
        
        return parent;
    }

    /**
     * Moves back to the previous page in the browser history.
     */
    public <P extends Page> P back( Class<P> parentType) {
        try {
            return parentType.cast( back());
        }
        catch( Exception e) {
            throw new RequestException( this, "back", "Expected " + parentType.getSimpleName(), e);
        }
    }

    /**
     * Closes the current window, switches the driver context to the Window of this page's parent, and returns this page's parent
     */
    public Page close() {
        WebDriver driver = getDriver();

        if( driver.getWindowHandles().size() == 1) {
            throw new InvalidStateException( this, "Can't close last open window");
        }

        if( getParent() == null) {
            throw new InvalidStateException( this, "Page has no parent");
        }

        if( getWindow().equals( parent.getWindow())) {
            throw new InvalidStateException( this, "Parent window equal to child window");
        }

        driver.close();
        try {
            driver.switchTo().window( getParent().getWindow().toString());
        }
        catch( NoSuchWindowException e) {
            throw new InvalidStateException( this, "Parent window could not be switched to or does not exist: " + getParent().getWindow().toString());
        }
        return getParent();
    }

    /**
     * Returns the element identified by the given locator. Throws ElementMissingException if not found.
     */
    public WebElement findElement( By locator) {
        return finder().findElement( locator);
    }

    /**
     * Returns the visible element identified by the given locator. Throws ElementMissingException if not found.
     */
    public WebElement findVisibleElement( By locator) {
        return finder().findVisibleElement( locator);
    }

    /**
     * Returns the element identified by the given locator.
     */
    public Optional<WebElement> findOptionalElement( By locator) {
        return finder().findOptionalElement( locator);
    }

    /**
     * Returns the elements identified by the given locator.
     */
    public List<WebElement> findElements( By locator) {
        return finder().findElements( locator);
    }

    /**
     * Returns when there are no elements identified by the given locator.
     */
    public void awaitNoElements( By locator) {
        finder().awaitNoElements( locator);
    }

    /**
     * Returns the visible elements identified by the given locator.
     */
    public List<WebElement> findVisibleElements( By locator) {
        return finder().findVisibleElements( locator);
    }

    /**
     * Returns a Finder that starts from the given element.
     */
    public Finder startingAt( WebElement element) {
        return finder().startingAt( element);
    }

    /**
     * Returns a Finder that waits for the given timeout interval.
     */
    public Finder waitingFor( long duration, TimeUnit unit) {
        return finder().waitingFor( duration, unit);
    }

    /**
     * Returns a Finder that waits for the given time interval for an expected condition to remain unchanged.
     */
    public Finder whenStableFor( long duration, TimeUnit unit) {
        return finder().whenStableFor( duration, unit);
    }

    /**
     * Returns a Finder that looks for elements satisfying the given condition.
     */
    public Finder when( Predicate<WebElement> condition) {
        return finder().when( condition);
    }

    /**
     * Returns a Finder for this page.
     */
    protected Finder finder() {
       return new Finder( this);
    }

    /**
     * Returns the result of performing the given {@link PageAction}.
     * If the action is undefined, throws an {@link InvalidStateException} with the given description.
     */
    public <T,A extends PageAction<?,T>> T perform( String description, Optional<A> action) {
        return
            action
            .orElseThrow( () -> new InvalidStateException( this, String.format( "%s: Action not found", description)))
            .perform();
    }

    /**
     * Returns the PageAction represented by the WebElement located by the given selector.
     */
    protected <T,P extends Page,A extends PageAction<P,T>> Optional<A> getAction( By selector, Class<A> actionType, Class<P> pageType) {
        return getAction( finder(), selector, actionType, pageType);
    }

    /**
     * Returns the PageAction represented by the WebElement located by the given selector.
     */
    protected <T,P extends Page,A extends PageAction<P,T>> Optional<A> getAction( Finder finder, By selector, Class<A> actionType, Class<P> pageType) {
        return
            finder.findOptionalElement( selector)
            .map( e -> {
                    try {
                        return
                            actionType
                            .getDeclaredConstructor( pageType, WebElement.class)
                            .newInstance( this, e);
                    }
                    catch( Exception ex) {
                        throw new InvalidStateException( this, "Can't create instance of " + actionType.getSimpleName(), ex);
                    }
                });
    }

    /**
     * Returns the BasicElementAction represented by the WebElement located by the given selector.
     */
    protected Optional<BasicElementAction> getBasicElementAction( By selector) {
        return getBasicElementAction( finder(), selector);
    }

    /**
     * Returns the BasicElementAction represented by the WebElement located by the given selector.
     */
    protected Optional<BasicElementAction> getBasicElementAction( Finder finder, By selector) {
        return getAction( finder, selector, BasicElementAction.class, Page.class);
    }

    public String toString() {
        return ToString.getBuilder( this).toString();
    }
}

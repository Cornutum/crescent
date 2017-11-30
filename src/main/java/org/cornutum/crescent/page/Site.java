package org.cornutum.crescent.page;

import org.cornutum.crescent.util.ToString;

import org.apache.http.client.fluent.Executor;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.net.URI;

/**
 * Defines a Web page provider.
 */
public class Site {

    private URI uri;
    private WebDriver driver;
    private long maxAppWait;
    private double driverLatencyFactor;

    /**
     * Creates a new Site object.
     */
    public Site() {
        this( (URI) null);
    }
  
    /**
     * Creates a new Site object.
     */
    public Site( String uri) {
        this( toURI( uri));
    }
  
    /**
     * Creates a new Site object.
     */
    public Site( URI uri) {
        setURI( uri);
        setMaxAppWait( 2000);
        setDriverLatencyFactor( 1.0);
    }

    /**
     * Changes the maximum timeout (in milliseconds) to wait for the app to update elements on this site.
     */
    public void setMaxAppWait( long maxWait) {
        this.maxAppWait = maxWait;
    }

    /**
     * Returns the maximum timeout (in milliseconds) to wait for the app to update elements on this site.
     */
    public long getMaxAppWait() {
        return maxAppWait;
    }

    /**
     * Changes the {@link #getDriverLatencyFactor latency factor} for WebDriver requests to this site.
     */
    public void setDriverLatencyFactor( double driverLatencyFactor) {
        this.driverLatencyFactor = driverLatencyFactor;
    }

    /**
     * Returns the latency factor for WebDriver requests to this site.
     * This value represents the relative network latency for requests to the WebDriver currently in use. Together with
     * the {@link #getMaxAppWait application update timeout}, it is used to determine {@link #getRequestWait effective wait durations}
     * for WebDriver requests for current UI state.
     * <P/>
     * By default, the basic latency factor (test, browser, and app running on the same host) is 1.0.
     * To adjust for greater network latencies when test, browser, or app are running on different hosts, a value greater than 1.0 may
     * prevent premature timeouts.
     */
    public double getDriverLatencyFactor() {
        return driverLatencyFactor;
    }

    /**
     * Returns the effective duration for the given wait interval (in milliseconds) for WebDriver requests to this site.
     */
    public long getRequestWait( long interval) {
        return (long) Math.round( interval * getDriverLatencyFactor());
    }

    /**
     * Changes the URI for this site.
     */
    public void setURI( URI uri) {
        this.uri = uri;
    }

    /**
     * Returns the URI for this site.
     */
    public URI getURI() {
        return uri;
    }
  
    /**
     * Returns the WebDriver for this site.
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Returns an HTTP request {@link Executor} for this site.
     */
    public Executor getRequestExecutor() {
        // Use cookies currently defined for this site.
        BasicCookieStore requestCookies = new BasicCookieStore();
        for( Cookie siteCookie : getDriver().manage().getCookies()) {
            BasicClientCookie requestCookie = new BasicClientCookie( siteCookie.getName(), siteCookie.getValue());
            requestCookie.setDomain( siteCookie.getDomain());
            requestCookie.setPath( siteCookie.getPath());
            requestCookie.setExpiryDate( siteCookie.getExpiry());
            requestCookies.addCookie( requestCookie);
        }
    
        return
            Executor
            .newInstance()
            .cookieStore( requestCookies);
    }

    /**
     * Initiate access to this site.
     */
    public void enter( WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Terminate access to this site.
     */
    public void exit() {
        if( driver != null) {
            try {
                driver.quit();
            }
            finally {
                driver = null;
            }
        }
    }

    /**
     * Returns the URI represented by the given string.
     */
    public static URI toURI( String string) {
        try {
            return
                string == null
                ? null
                : new URI( string);
        }
        catch( Exception e) {
            throw new IllegalArgumentException( "Invalid URI", e);
        }
    }

    public String toString() {
        return ToString.getBuilder( this).toString();
    } 
}

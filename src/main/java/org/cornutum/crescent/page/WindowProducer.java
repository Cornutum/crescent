package org.cornutum.crescent.page;

import org.cornutum.crescent.util.ToString;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.openqa.selenium.WebDriver;

import java.util.Set;

/**
 * Defines an action that opens content in a new window.
 */
public abstract class WindowProducer {

    private final Page page;
    
    /**
     * Opens content in a new window and returns the new window handle.
     */
    protected WindowProducer( Page page) {
        this.page = page;
    }
    
    /**
     * Opens content in a new window and returns the new window handle.
     */
    public WindowHandle open() {
        WebDriver driver = getDriver();
        Set<String> windowsBefore = driver.getWindowHandles();
        get();

        Set<String> windowsAfter;
        try {
            windowsAfter = 
                getFinder().await( driver)
                .until( d -> {
                        Set<String> windowsNow = driver.getWindowHandles();
                        return windowsNow.size() > windowsBefore.size()? windowsNow : null;
                    });
        }
        catch( Exception e) {
            throw new WindowException( getPage(), "No new window found", e);
        }

        Set<String> windowsNew = Sets.difference( windowsAfter, windowsBefore);
        if( windowsNew.size() != 1) {
            throw new WindowException( getPage(), "Unexpected windows created");
        }

        return new WindowHandle( Iterables.get( windowsNew, 0));
    }

    /**
     * Returns the page for this producer.
     */
    public Page getPage() {
        return page;
    }

    /**
     * Returns the WebDriver for this page.
     */
    public WebDriver getDriver() {
        return getPage().getDriver();
    }

    /**
     * Returns the {@link Finder} that this WindowProducer uses to wait for a new window.
     */
    protected Finder getFinder() {
        return getPage().finder();
    }

    /**
     * Performs the action that requests the new content.
     */
    protected abstract void get();

    public String toString() {
        return
            ToString.getBuilder( this)
            .append( getPage())
            .toString();
    }
}

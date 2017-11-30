package org.cornutum.crescent.page;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.function.Predicate;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;

import java.util.List;

/**
 * Provides methods to manage page elements.
 */
public final class PageUtils {

    private PageUtils() {
        // Static methods only
    }

    /**
     * Returns the content of the given text element, ignoring any leading or trailing whitespace.
     */
    public static String getText( WebElement element) {
        return element.getText().trim();
    }

    /**
     * Changes the content of the given text element.
     */
    public static void setText( WebElement element, String text) {
        enterText( element, text).sendKeys( Keys.TAB);
    }

    /**
     * Submits a change to the content of the given text element.
     */
    public static void submitText( WebElement element, String text) {
        enterText( element, text).sendKeys( Keys.RETURN);
    }

    /**
     * Replaces the content of the given text element.
     */
    public static WebElement enterText( WebElement element, String text) {
        // Set keyboard focus
        element.sendKeys();

        // Remove previous text
        element.clear();
        if( text != null) {
            // Enter new text
            element.sendKeys( text);
        }

        return element;
    }

    /**
     * Positions the pointer over the center of the given element.
     */
    public static void moveTo( Page page, WebElement element) {
        new Actions( page.getDriver()).moveToElement( element).perform();
    }

    /**
     * Selects the option with the given value.
     */
    public static void setSelectedValue( WebElement select, String value) {
        Select menu = new Select( select);
        String selectedValue;
        if( value != null) {
            menu.selectByValue( value);
        }
        else if( (selectedValue = getSelectedValue( select)) != null) {
            menu.deselectByValue( selectedValue);
        }
    }

    /**
     * Returns the value of the selected option.
     */
    public static String getSelectedValue( WebElement select) {
        Select menu = new Select( select);
        WebElement option = menu.getFirstSelectedOption();

        return
            option == null
            ? null
            : option.getAttribute( "value");
    }

    /**
     * Returns the values of all options for the given select element.
     */
    public static List<String> getOptionValues( WebElement select) {
        return
            new Select( select)
            .getOptions()
            .stream()
            .map( option -> option.getAttribute( "value"))
            .collect( toList());
    }

    /**
     * Returns the text of all options for the given select element.
     */
    public static List<String> getOptionText( WebElement select) {
        return
            new Select( select)
            .getOptions()
            .stream()
            .map( option -> option.getText())
            .collect( toList());
    }

    /**
     * Selects the option with the given text.
     */
    public static void setSelectedText( WebElement select, String text) {
        Select menu = new Select( select);
        String selectedValue;
        if( text != null) {
            menu.selectByVisibleText( text);
        }
        else if( (selectedValue = getSelectedValue( select)) != null) {
            menu.deselectByValue( selectedValue);
        }
    }

    /**
     * Returns the text of the selected option.
     */
    public static String getSelectedText( WebElement select) {
        Select menu = new Select( select);
        WebElement option = menu.getFirstSelectedOption();

        return
            option == null
            ? null
            : option.getText();
    }

    /**
     * Changes the value of the given boolean option control.
     */
    public static void setSelected( WebElement option, boolean selected) {
        if( option.isSelected() != selected) {
            option.click();
        }
    }

    /**
     * Returns true if the given element has all of the given classes.
     */
    public static boolean hasClass( WebElement element, String... classNames) {
        return hasClass( element.getAttribute( "class"), classNames);
    }

    /**
     * Returns true if the given class list has all of the given classes.
     */
    public static boolean hasClass( String classList, String... classNames) {
        String[] elementClasses = StringUtils.split( StringUtils.trimToEmpty( classList));
    
        boolean hasClasses = !(elementClasses.length == 0 && classNames.length > 0);
        for( int i = 0;
             hasClasses && i < classNames.length;
             hasClasses = ArrayUtils.contains( elementClasses, classNames[i++]));
    
        return hasClasses;
    }

    /**
     * Always returns true for any element.
     */
    public static Predicate<WebElement> always =
        new Predicate<WebElement>() {
            public boolean test( WebElement element) {
                return true;
            }
        };

    /**
     * Returns true if the given element is non-null and visible.
     */
    public static Predicate<WebElement> isVisible =
        new Predicate<WebElement>() {
            public boolean test( WebElement element) {
                return element != null && element.isDisplayed();
            }
        };


    /**
     * Returns true if the given element is non-null and enabled.
     */
    public static Predicate<WebElement> isEnabled =
        new Predicate<WebElement>() {
            public boolean test( WebElement element) {
                return element != null && element.isEnabled();
            }
        };


    /**
     * Returns a Predicate that returns true if the given element is non-null and
     * has a non-null attribute with the given name.
     */
    public static Predicate<WebElement> hasAttribute( final String attribute) {
        return
            new Predicate<WebElement>() {
                public boolean test( WebElement element) {
                    return element != null && element.getAttribute( attribute) != null;
                }
            };
    }

    /**
     * Returns the result of the given supplier when invoked in the context of the
     * given frame. On return, restores the context to the main page content.
     */
    public static <T> T withFrame( Page page, WebElement frame, Supplier<T> resultSupplier) {
        try {
            page.getDriver().switchTo().frame( frame);
            return resultSupplier.get();
        }
        finally {
            page.getDriver().switchTo().defaultContent();
        }
    }

    /**
     * Converts the content of the given element to an integer value, returning null for a blank string.
     * Throws an {@link InvalidStateException} if the element does not represent an integer value.
     */
    public static Integer getInteger( Page page, WebElement element, String valueDescription) {
        return getInteger( page, element.getText(), valueDescription);
    }

    /**
     * Converts the given text to an integer value, returning null for a blank string.
     * Throws an {@link InvalidStateException} if the text does not represent an integer value.
     */
    public static Integer getInteger( Page page, String text, String valueDescription) {
        try {
            String value = StringUtils.trimToNull( text);
            return value == null? null : Integer.valueOf( value);
        }
        catch( Exception e) {
            throw new InvalidStateException( page, valueDescription + " is not an integer", e);
        }
    }

    /**
     * Converts the content of the given element to an double value, returning null for a blank string.
     * Throws an {@link InvalidStateException} if the element does not represent an double value.
     */
    public static Double getDouble( Page page, WebElement element, String valueDescription) {
        return getDouble( page, element.getText(), valueDescription);
    }

    /**
     * Converts the given text to a double value, returning null for a blank string.
     * Throws an {@link InvalidStateException} if the text does not represent an double value.
     */
    public static Double getDouble( Page page, String text, String valueDescription) {
        try {
            String value = StringUtils.trimToNull( text);
            return value == null? null : Double.valueOf( value);
        }
        catch( Exception e) {
            throw new InvalidStateException( page, valueDescription + " is not a double", e);
        }
    }
}

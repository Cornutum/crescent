# Crescent
Simple modern page models for Selenium

### Overview

* [What is a page model?](#what-is-a-page-model)
* [Basics: Site](#basics-site)
* [Basics: Page](#basics-page)
* [Basics: Finder](#basics-finder)
* [Design principles](#design-principles)
* [Page components](#page-components)
* [Page actions](#page-actions)
* [Handling multiple windows](#handling-multiple-windows)
* [Failure exceptions](#failure-exceptions)

### What is a page model?

A page model is a class that models how a person interacts with a specific page in the UI. The public methods of a page
model represent the tasks that users perform on the page -- what they see and what they say to get things done and
to understand the results. The private methods of a page model implement the specific interactions with page content
needed to perform these tasks.

The page model pattern helps to keep UI tests more resilient to changes in the UI. Because tests use only the public
interface to pages, they are isolated from details of page construction. Page layout and content can be revised and
reorganized with requiring any changes to test scenarios or test assertions.

Page models are ultimately implemented using the [Selenium WebDriver](https://seleniumhq.github.io/docs/wd.html). For
details, see the [WebDriver API Javadoc](http://seleniumhq.github.io/selenium/docs/api/java/). 


### Basics: Site

Use the [`Site`](src/main/java/org/cornutum/crescent/page/Site.java) class to model the overall context for testing a specific Web app. This
context includes not only information about the app itself (for example, the [base
URI](src/main/java/org/cornutum/crescent/page/Site.java#L98) for app pages) but also information about the testing context (for example the
`WebDriver` instance used to access the UI).

All `Page` objects must be created in the context of a specific `Site`. That's why `Page` is a generic type parameterized by the type of its
associated `Site`.

A `Site` can be configured to reflect the latencies that occur when interacting with the app. For example, the [maximum app wait
time](src/main/java/org/cornutum/crescent/page/Site.java#L56) can be configured to reflect the maximum timeout to wait for this particular
app to update its page elements. In addition, the [driver latency factor](src/main/java/org/cornutum/crescent/page/Site.java#L77) can be
configured to reflect additional latencies incurred when using a particular remote `WebDriver`.

The `Site` class is typically used as a base class for classes that model additional application-specific
context. [`ResourceSite`](src/main/java/org/cornutum/crescent/page/ResourceSite.java) implements a special-purpose `Site` that provides Web
pages defined by resource files.

The `Site` class defines two basic behaviors for tests.

Method | What it does
------ | ------------
[`enter`](src/main/java/org/cornutum/crescent/page/Site.java#L132) | Associates the `Site` with a `WebDriver` instance
[`exit`](src/main/java/org/cornutum/crescent/page/Site.java#L139) | Terminates the `WebDriver` associated with the `Site`


### Basics: Page

The [`Page`](src/main/java/org/cornutum/crescent/page/Page.java) class is the foundation for all page models. This abstract base class offers many different forms
for constructors that model the various ways that a new page can appear in the UI.

* By direct navigation to specific URL
* By a request from a prior page (called the ["parent"](src/main/java/org/cornutum/crescent/page/Page.java#L169) page)
* By a request to open a new browser window

A `Page` is always associated with a `Site` instance and inherits many of its properties. For example, `Page.getDriver()` returns the
`WebDriver` used by the `Site` for this page. Similarly, by default, `Page.getMaxAppWait()` returns the maximum app wait time defined for
its `Site`. (But note that you can replace this default with a different maximum wait time for any specific `Page` instance.)

The `Page` class defines the basic methods used by a page model to:

* search for page elements, using a [`Finder`](#basics-finder) object,
* [handle pages in multiple windows](#handling-multiple-windows), and
* find and perform the [page actions](#page-actions) represented by page elements.

### Basics: Finder

A [`Finder`](src/main/java/org/cornutum/crescent/page/Finder.java) object defines how to search for a specific page element. Fundamentally,
a page element must be identified by matching it to some
[`By`](http://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/By.html) condition. (For example, `By.cssSelector` identifies
elements using a CSS selector expression.) But a `Finder` contains many other optional parameters that may be needed to reliably guide the
search.

* The [top-level element](src/main/java/org/cornutum/crescent/page/Finder.java#L155) within which the search is performed
* The [maximum time to wait](src/main/java/org/cornutum/crescent/page/Finder.java#L177) before giving up the search
* [How often to check](src/main/java/org/cornutum/crescent/page/Finder.java#L200) for the presence of matching element(s)
* Any additional [condition](src/main/java/org/cornutum/crescent/page/Finder.java#L247) that matching element(s) must satisfy

The `Finder` class defines the following basic search methods.

Method | What it does
------ | ------------
[`findElement`](src/main/java/org/cornutum/crescent/page/Finder.java#L62) | Returns a specific element that is assumed to exist
[`findElements`](src/main/java/org/cornutum/crescent/page/Finder.java#L96) | Returns a list of all matching elements
[`findOptionalElement`](src/main/java/org/cornutum/crescent/page/Finder.java#L81) | Returns an `Optional<WebElement>` that may or may not be present
[`findVisibleElement`](src/main/java/org/cornutum/crescent/page/Finder.java#L74) | Equivalent to `when( PageUtils.isVisible).findElement`
[`awaitNoElements`](src/main/java/org/cornutum/crescent/page/Finder.java#L119) | Returns successfully when no matching elements can be found

`Finder` also provides a convenient "fluent" interface for defining the search context, using sensible defaults for unspecified parameters. For example:

```
List<WebElement> hasDataDefined =
    startingAt( someDivElement)
    .waitingFor( 30, SECONDS)
    .checkingEvery( 200, MILLISECONDS)
    .when( PageUtils.hasAttribute( "data-defined"))
    .whenStableFor( 1, SECONDS)
    .findElements( By.cssSelector( ".someClass"));
```

Page models seldom create `Finder` instances directly. Instead, it's simpler to use the `Page` methods that provide 
the same interfaces for defining and executing an element search.


### Design principles

* **Less is more**. Crescent is a very minimal extension of the basic `WebDriver` API. It makes no rules about the internal structure of a
page model. It does not try to model individual interaction techniques, like menus or various `<input>` types. (Although the
[`PageUtils`](src/main/java/org/cornutum/crescent/page/PageUtils.java) class offers many useful helper methods for that sort of thing.)
Less framework means more freedom for you to choose the right way to model your UI.

* **BYO WebDriver**. Crescent has no support for creating a `WebDriver` and connecting it to a browser. Many are the ways in which this can
be done, depending on your test environment, your preferred tools for configuration, and so on. You are free to choose the best way to
handle this part of your test setup.

* **When a page is (re)visited, create a new `Page`**. Because this is a good practice for any `WebDriver` program, the `Page` class assumes
that you will create a new `Page` instance when the corresponding page is available for interaction in the UI. Therefore, every page
model constructor leads to the basic `Page` constructor, which calls `initPage()`. By default, this calls `visit()`, which performs the
following steps.

    * Synchronizes the state of the `Page` with the state of the UI. Depending on the constructor arguments, this can entail actively
navigating to a specific URL, passively querying the current location of the browser, or switching the `WebDriver` to a specified browser window.

    * Calls `visited()`. By default, this method does nothing. But you can override it to perform any page-specific actions needed after
arriving at a new instance of this page.

* **Error states are exceptions**. Users make mistakes. So good UIs help them recover by showing an "error state" -- for example, by
displaying an error message. And good tests verify that these error states appear as expected. But how? Tests use the public page model
interface, which models the user's tasks. But checking for errors is not the user's job! That job belongs to the page model
*implementation*. When an error state is detected (expected or not!), the page model should throw an exception. Crescent provides
several [basic exception types](#failure-exceptions) that are handy for reporting the details of an error state.


### Page components

For many Web apps, all individual pages share many common graphic design forms or interaction techniques. You can use the
[`Component`](src/main/java/org/cornutum/crescent/page/Component.java) class to model common UI elements that are shared by multiple page
models. A `Component` always belongs to a specific `Page` instance. That's why `Component` is a generic type parameterized by the type of
its associated `Page`.

### Page actions

Some page elements implement actions that produce a certain result in the UI. For example, clicking a button can cause a different page to
appear. It's not unusual for a UI to present multiple elements that all implement the same action. The `PageAction` class offers a general
way to model this kind of interaction.

`PageAction` is a generic type that is parameterized by the type of the source page and the type of the result produced by the action.
There are two basic `PageAction` types, each of which are abstract base classes you can use to model app-specific actions.

* [`ElementAction`](src/main/java/org/cornutum/crescent/page/ElementAction.java): An action triggered by an interaction with a single element

* [`NewWindowAction`](src/main/java/org/cornutum/crescent/page/NewWindowAction.java): An action that produces content in a new browser window

The `Page` class defines the following methods for handling page actions.

Method | What it does
------ | ------------
[`getAction`](src/main/java/org/cornutum/crescent/page/Page.java#L398) | Returns a specific type of `PageAction` that is implemented by a specific optional page element
[`perform`](src/main/java/org/cornutum/crescent/page/Page.java#L388) | Given an optional `PageAction` expected to be shown, either performs the action or reports a failure.


### Handling multiple windows

Although it's common for Web apps to display content in multiple browser windows, it's tricky to handle that using the basic `WebDriver`
API.  But the [`WindowProducer`](src/main/java/org/cornutum/crescent/page/WindowProducer.java) class makes things a bit simpler.

`WindowProducer` is an abstract base class for an action that opens a new window. The basic `WindowProducer.open()` handles the bookkeeping
needed to discover and return the `WindowHandle` for the expected new window. Together with a specific `WindowProducer`, you can use the
[`NewWindowAction`](#page-actions) class to model not only the interaction that produces the new window, but also the type of content now
available for interaction.

Given a specific `WindowHandle` argument for a new `Page`, the basic `visited()` method will automatically switch the focus of the
`WebDriver` to the specified window.  Afterwards, your test must be responsible for keeping track of which `Page` instances are modeling
content for different windows. But, for any `Page` instance, when you call `close()`, it will automatically switch the `WebDriver` focus
back to the window for the `getParent()` page.

### Failure exceptions

Because [error states should cause exceptions](#design-principles), Crescent provides several basic exception types that are handy for
reporting the details of an error state.

Exception | Purpose
------ | ------------
[`PageException`](src/main/java/org/cornutum/crescent/page/PageException.java) | Base class for all error states reported by a `Page` 
[`ElementMissingException`](src/main/java/org/cornutum/crescent/page/ElementMissingException.java) | Reports a failure when searching for a specific page element
[`InvalidFormException`](src/main/java/org/cornutum/crescent/page/InvalidFormException.java) | Reports a [`FieldFailure`](src/main/java/org/cornutum/crescent/page/FieldFailure.java) for each invalid value entered into a form
[`InvalidStateException`](src/main/java/org/cornutum/crescent/page/InvalidStateException.java) | Reports a general error state in a `Page`
[`RequestException`](src/main/java/org/cornutum/crescent/page/RequestException.java) | Reports a failure in a system request made from a `Page`
[`WindowException`](src/main/java/org/cornutum/crescent/page/WindowException.java) | Reports a failure to create a new browser window



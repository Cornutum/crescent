package org.cornutum.crescent.page;

import java.net.URI;

/**
 * Defines a provider of Web page resources.
 */
public class ResourceSite extends Site {

    /**
     * Creates a new ResourceSite that provides resources from the current ClassLoader.
     */
    public ResourceSite() {
        this( null);
    }

    /**
     * Creates a new ResourceSite that provides resources for the given
     * base class.
     */
    public ResourceSite( Class<?> baseClass) {
        this( baseClass, null);
    }

    /**
     * Creates a new ResourceSite that provides resources from the given base directory relative to the given
     * base class.
     */
    public ResourceSite( Class<?> baseClass, String baseDir) {
        super( toResourceURI( baseClass, baseDir));
    }

    /**
     * Creates a new ResourceSite that provides resources from the given base directory relative to the given
     * base class.
     */
    private static URI toResourceURI( Class<?> baseClass, String baseDir) {
        try {
            return
                baseClass != null
                ? baseClass.getResource( baseDir==null? "." : baseDir).toURI()
                : new URI( "file://" + (baseDir==null? "" : baseDir));
        }
        catch( Exception e) {
            throw new IllegalArgumentException( "Can't create URI", e);
        }
    }
}

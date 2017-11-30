package org.cornutum.crescent.page;

import org.cornutum.crescent.util.ToString;

/**
 * Represents a failure for a specific field.
 */
public class FieldFailure {

    private final String field;
    private final String description;
    
    /**
     * Creates a new FieldFailure object.
     */
    public FieldFailure( String field, String description) {
        this.field = field;
        this.description = description;
    }

    /**
     * Returns the id of the invalid field.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the description of the failure for this field.
     */
    public String getDescription() {
        return description;
    }

    public String toString() {
        return
            ToString.getBuilder( this)
            .append( getField())
            .append( getDescription())
            .toString();
    }
}

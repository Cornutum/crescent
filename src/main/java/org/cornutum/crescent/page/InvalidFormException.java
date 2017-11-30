package org.cornutum.crescent.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * Thrown when some fields in a form have invalid values.
 */
public class InvalidFormException extends PageException {

	private List<FieldFailure> failures = new ArrayList<FieldFailure>();

	private static final long serialVersionUID = -8448581461617430092L;
    
    /**
     * Creates a new InvalidFormException with the given field validation failures.
     */
    public InvalidFormException( Page page, FieldFailure... failures) {
        super( page);
        Arrays.stream( failures).forEach( f -> addFailure( f));
    }

    /**
     * Returns a new InvalidFormException if the given list of failures is non-null and non-empty.
     */
    public static Optional<InvalidFormException> of( Page page, List<FieldFailure> failures) {
        return
            Optional.ofNullable
            ( failures == null || failures.isEmpty()
              ? null
              : new InvalidFormException( page, failures.toArray( new FieldFailure[0])));
    }

    /**
     * Adds an invalid value failure to this exception.
     */
    public void addFailure( FieldFailure failure) {
        failures.add( failure);
    }

    /**
     * Returns the invalid value failures for this exception.
     */
    public List<FieldFailure> getFailures() {
        return failures;
    }

    public String getMessage() {
        StringBuilder msg = new StringBuilder();

        msg.append( getPage());
        if( !getFailures().isEmpty()) {
            msg.append( ": ").append( StringUtils.join( getFailures(), ", "));
        }
    
        return msg.toString();
    }
}


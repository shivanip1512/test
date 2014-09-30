package com.cannontech.web.picker;

import java.util.Collection;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;

/**
 * Override this class to implement a picker.  Instances of implementing
 * classes create pickers which can be used via the various picker tags or
 * the Javascript Picker class.
 */
public interface Picker<T> {
    /**
     * @return the name of the field which is to be treated as the id.
     */
    public String getIdFieldName();

    public MessageSourceResolvable getDialogTitle();

    /**
     * @return an array of instances of OutputColumn where each instance
     *         represents a column in the results displayed to the user.
     */
    public List<OutputColumn> getOutputColumns();

    /**
     * Search for data matching ss.
     * @param ss The query string entered. If this is null or empty, the query
     *            should return any valid matches.
     * @param start The zero-based index of the first match to return.
     * @param count The maximum number of matches to return. The size of the
     *            return value should be less than or equal to count.
     * @param extraArgs optional string which comes from the "extraArgs" argument to the 
     * @return An instance of SearchResult populated appropriately.
     */
    public SearchResults<T> search(String ss, int start, int count,
            String extraArgs, YukonUserContext userContext);

    /**
     * Perform a search for the specific ids requested.  This is used to
     * populate the picker if there were previously saved results.
     */
    public SearchResults<T> search(Collection<Integer> initialIds, String extraArgs, YukonUserContext userContext);
}

package com.cannontech.core.authorization.support;

import java.util.Collection;
import java.util.Queue;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface Authorization<T> {

    /**
     * Method to determine if a user is authorized for the given permission
     * with the given object
     * @param user - User asking permission
     * @param permission - Permission in question
     * @param object - Object the user needs permission on
     * @return True if the user is authorized
     */
    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission, T object);

    /**
     * Method to determine authorization for a queue of objects, a given user and a permission
     * @param inputQueue - Objects to determine authorization for
     * @param unknownQueue - Queue to put objects for which authorization can't be determined
     * @param authorizedObjects - Collection to add objects to which are authorized 
     * @param user - User asking permission
     * @param permission - Permission in question
     */
    public void process(Queue<T> inputQueue,
                        Queue<T> unknownQueue,
                        Collection<T> authorizedObjects,
                        LiteYukonUser user, 
                        Permission permission);
}

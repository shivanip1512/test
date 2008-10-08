package com.cannontech.clientutils;
/**
 *  @author ekhazon
 * interface WebUpdatedDAO exists to template the objects that
 * lighten the client-server load during AJAX interaction.
 * instead of updating all data in one page we selectively
 * update only the items that changed their state on the
 * server side AFTER the previous update.
 * All classes implementing this interface must choose some kind of
 * strategy for dealing with this situation.
 * see @WebUpdatedPAObjectMap class for the implementation
 */
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface WebUpdatedDAO<E> {

    public void remove(E e);
    
    public void remove(Collection<E> ids);
    
    public boolean containsKey(E e);
    
    public List<E> getUpdatedIdsSince(Date timeStamp, E... ids);
    
    public void manualUpdate(Iterable<E> ids);

}
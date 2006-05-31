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
import java.util.Date;

public interface WebUpdatedDAO {

	public String[] getUpdatedIdsSince(String[] ids, Date timeStamp);

}
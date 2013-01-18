package com.cannontech.dr.rfn.service;

import java.util.Collection;
import java.util.Set;

import com.cannontech.amr.rfn.service.RfnDeviceReadCompletionCallback;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastRequest;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastRequest;


public interface RfnExpressComMessageService {

    /**
     * Attempts to send a unicast request for a RFN device on the yukon.qr.obj.dr.rfn.ExpressComUnicastRequest queue.
     * Will use a separate thread to make the request.
     * Will expect TWO responses.
     * 
     * The first is a status message indicating this is a known device and a command
     * will be tried, or a command is not possible for this device.  This response should come back within seconds.
     * 
     *  The second response is the actual data.  This response is only expected if the first response
     *  was 'OK'.  This response can take anywhere from seconds to minutes to tens of minutes depending
     *  on network performance.
     *  
     *  The master.cfg can contain two parameters to define the timeouts:
     *  
     *  RFN_XCOMM_REQUEST_REPLY1_TIMEOUT
     *  RFN_XCOMM_REQUEST_REPLY2_TIMEOUT
     *  
     *  If not provided they default to 1 minute and 10 minutes.
     */
    public void sendUnicastDataRequest(final RfnExpressComUnicastRequest request, final RfnUnicastDataCallback callback);
    
    /**
     * Attempts to send a unicast request for a RFN device on the yukon.qr.obj.dr.rfn.ExpressComUnicastRequest queue.
     * Will use a separate thread to make the request.
     * Will expect ONE responses.
     * 
     * The response is a status message indicating this is a known device and a command
     * will be tried, or a command is not possible for this device.  This response should come back within seconds.
     * 
     *  The master.cfg can contain the RFN_XCOMM_REQUEST_REPLY1_TIMEOUT parameter to define the timeout
     *  
     *  If not provided they default to 1 minute.
     */
    public void sendUnicastRequest(RfnExpressComUnicastRequest request, RfnUnicastCallback callback);

    /**
     * Attempts to send unicast requests in bulk for a RFN devices on the yukon.qr.obj.dr.rfn.ExpressComBulkUnicastRequest queue.
     * Will use a separate thread to make the request.
     * Will expect ONE response however there could be a second (data) reponse that would come back on the 
     * yukon.qr.obj.dr.rfn.ExpressComBulkUnicastResponse queue.
     * 
     * The first is a status message indicating this is a known device and a command
     * will be tried, or a command is not possible for this device.  This response should come back within seconds.
     * 
     *  The second response if desired would be the actual data.  This response is only expected if the first response
     *  was 'OK' and the request's responseExpected field was set to true.  
     *  This response can take anywhere from seconds to minutes to tens of minutes depending
     *  on network performance.
     *  
     *  The master.cfg can contain the RFN_XCOMM_REQUEST_REPLY1_TIMEOUT parameter to define the initial timeout.
     *  
     *  If not provided it defaults to 1 minute.
     * @return set of message id's that were sent.
     */
    public Set<String> sendUnicastBulkRequest(Collection<RfnExpressComUnicastRequest> requests);
    
    /**
     * Sends a broadcast message request out to the entire RFN network.  Does not explicitly expect responses at this point.
     */
    public void sendBroadcastRequest(final RfnExpressComBroadcastRequest request);
    
    /**
     * Method to specifically send a 'read now' request to a RF ExpressCom device.  Will expect up to two responses, uses
     * {@link #sendUnicastDataRequest} to do the transaction.
     * 
     * @see {@link #sendUnicastDataRequest} for details of this transaction.
     * 
     * Used by the {@link DeviceAttributeReadService} via the {@link DeviceAttributeReadRfnStrategy}.
     */
    public void readDevice(RfnDevice device, RfnDeviceReadCompletionCallback<RfnExpressComUnicastReplyType, RfnExpressComUnicastDataReplyType> delegateCallback);

    /**
     * Sends a 'read now' message to a RF ExpressCom device.  Will only expect a status response.  This method is the preferred
     * way of sending a 'read now' command for now, until rf firmware is able to send a data reply.  The read data for now 
     * will be coming back unsolicoted on the 'yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest' queue.
     * @param device
     * @param callback
     */
    void readDevice(RfnDevice device, RfnUnicastCallback callback);
    
}
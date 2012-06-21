package com.cannontech.dr.rfn.service;

import java.util.Collection;

import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastRequest;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastRequest;


public interface RfnExpressComMessageService {

    /**
     * Attempts to send a unicast request for a RFN device on the yukon.qr.obj.dr.rfn.ExpressComUnicastRequest queue.
     * Will use a separate thread to make the request.
     * Will expect two responses.
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
     *  
     * @param request The request to send.
     * @param callback The callback to use for updating status, errors and data.
     */
    public void sendUnicast(final RfnExpressComUnicastRequest request, final RfnUnicastCompletionCallback callback);

    /**
     * Attempts to send unicast requests in bulk for a RFN devices on the yukon.qr.obj.dr.rfn.ExpressComBulkUnicastRequest queue.
     * Will use a separate thread to make the request.
     * Will expect two responses.
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
     *  
     * @param request The request to send.
     * @param callback The callback to use for updating status, errors and data.
     */
    public void sendUnicastBulk(Collection<RfnExpressComUnicastRequest> devices, RfnUnicastCompletionCallback callback);
    
    /**
     * Attempts to send a broadcast request on the yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest queue.
     * Will use a separate thread to make the request.
     * Will expect one responses.
     * 
     * The master.cfg can contain a parameter to define the timeout:
     *  
     *  RFN_XCOMM_REQUEST_REPLY_TIMEOUT
     * 
     * @param request
     * @param callback
     */
    public void sendBroadcast(final Collection<RfnExpressComBroadcastRequest> device, final RfnBroadcastCompletionCallback callback);
    
}
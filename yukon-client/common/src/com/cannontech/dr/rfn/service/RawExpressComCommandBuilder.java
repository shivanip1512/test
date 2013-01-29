package com.cannontech.dr.rfn.service;

import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;

public interface RawExpressComCommandBuilder {
    
    /**
     * Builds byte[] representing a 'read now' request to an RF ExpressCom device.
     */
    public byte[] getCommand(LmHardwareCommand command);

    /**
     * Builds byte[] representing a 'read now' request to an RF ExpressCom device
     * similar to getCommand(), except that the inner ExpressCom payload is written
     * as a byte array of ASCII characters that represent the hexadecimal bytes.
     * 
     * To illustrate the difference between getCommand() and getCommandAsHexStringByteArray, 
     * let's look at the read now command- inner payload is just one byte, 0xA2. 
     * 
     * getCommand()'s inner payload will be one byte long, since 0xA2 is a single 8-bit byte.
     * 
     * getCommandAsHexStringByteArray()'s inner payload will be two bytes, since 0xA2 when 
     * written as a string will require two characters: 'A' and '2'.  These characters are  
     * appended to an output string, and finally that string is converted back into a byte array.  
     * This effectively doubles the number of bytes used to represent the inner payload for any command.
     */
    public byte[] getCommandAsHexStringByteArray(LmHardwareCommand command);

    /**
     * Builds a raw ExpressCom command which will broadcast a command that cancels all
     * temporary out of service commands (opt outs).  Every device that responds to the 
     * specified SPID that was temp out of service will return to in service.
     *  
     * @param spid The SPID to use when broadcasting the cancel all opt out commands.  
     *             Comes from energy company role property BROADCAST_OPT_OUT_CANCEL_SPID.
     * @param outputBuffer The ByteBuffer that holds the resultant ExpressCom message.
     */
    public byte[] getBroadcastCancelAllTempOutOfServiceCommand(int spid);
    
    /**
     * This method determines if the SPID passed in is within the range of
     * valid values for the SPID address level.  The ExpressCom documentation
     * states that the valid SPID address range is 1-65534.
     * 
     * @param spid The SPID to test.
     * @return
     */
    public boolean isValidBroadcastSpid(int spid);
}

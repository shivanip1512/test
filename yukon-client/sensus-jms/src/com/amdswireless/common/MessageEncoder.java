/**
 * MessageEncoder.java part of com.amdswireless.agents.jhttp
 * AMDSJava
 * Author johng as of Oct 27, 2005
 * SVN Version $Id$
 */
package com.amdswireless.common;

import com.amdswireless.messages.rx.AppMessageType1;
import com.amdswireless.messages.rx.AppMessageType10;
import com.amdswireless.messages.rx.AppMessageType11;
import com.amdswireless.messages.rx.AppMessageType13;
import com.amdswireless.messages.rx.AppMessageType14;
import com.amdswireless.messages.rx.AppMessageType15;
import com.amdswireless.messages.rx.AppMessageType16;
import com.amdswireless.messages.rx.AppMessageType17;
import com.amdswireless.messages.rx.AppMessageType18;
import com.amdswireless.messages.rx.AppMessageType22;
import com.amdswireless.messages.rx.AppMessageType3;
import com.amdswireless.messages.rx.AppMessageType4;
import com.amdswireless.messages.rx.AppMessageType5;
import com.amdswireless.messages.rx.AppMessageType6;
import com.amdswireless.messages.rx.AppMessageType7;
import com.amdswireless.messages.rx.AppMessageType8;
import com.amdswireless.messages.rx.AppMessageType9;
import com.amdswireless.messages.rx.AppMessageTypeA;
import com.amdswireless.messages.rx.AppMessageTypeB;
import com.amdswireless.messages.rx.AppMessageTypeC;
import com.amdswireless.messages.rx.AppMessageTypeD;
import com.amdswireless.messages.rx.AppMessageTypeETier0;
import com.amdswireless.messages.rx.AppMessageTypeETier7;
import com.amdswireless.messages.rx.AppMessageTypeF;
import com.amdswireless.messages.rx.DataMessage;

/**
 * @author johng
 *
 */
public class MessageEncoder {
    //private static Logger log = Logger.getRootLogger();


    public DataMessage encodeMessage( char[] msg ) {
    	String st = new String(msg);
    	int startPos = st.indexOf( 0xfa );
    	if ( msg.length - startPos < 70 ) {
    		return null;
    	}
    	if ( startPos == -1 ) {
    		System.out.println("Unable to decode!");
    		return null;
    	}
    	char[] message = st.substring(startPos,startPos+66).toCharArray();
    	DataMessage am = null;
        int appCode = message[30];
        if ( appCode == 0x00 ) {
            return null;
        }
        if ( appCode == 0x01 ) {
            am = new AppMessageType1(message);
        }
        else if ( appCode == 0x03 ) {
            am = new AppMessageType3(message);
        }
        else if ( appCode == 0x04 ) {
            am = new AppMessageType4(message);
        }
        else if ( appCode == 0x05 ) {
            am = new AppMessageType5(message);
        }
        else if ( appCode == 0x06 ) {
            am = new AppMessageType6(message);
        }
        else if ( appCode == 0x07 ) {
            am = new AppMessageType7(message);
        }
        else if ( appCode == 0x08 ) {
            am = new AppMessageType8(message);
        }
        else if ( appCode == 0x09 ) {
            am = new AppMessageType9(message);
        }
        else if ( appCode == 0xA ) {
            am = new AppMessageTypeA(message);
        }
        else if ( appCode == 0xB ) {
            am = new AppMessageTypeB(message);
        } 
        else if ( appCode == 0xC ) {
            am = new AppMessageTypeC(message);
        } 
        else if ( appCode == 0xD ) {
            am = new AppMessageTypeD(message);
        } 
        else if ( appCode == 0xE ) {
            int tier = ( message[31] & 0x7 );
            if ( tier == 0x7 ) {
                am = new AppMessageTypeETier7(message);
            } else {
                am = new AppMessageTypeETier0(message);
            }
        }
        else if ( appCode == 0xF ) {
            am = new AppMessageTypeF(message);
        }
        else if ( appCode == 0x10 ) {
            am = new AppMessageType10(message);
        }
        else if ( appCode == 0x11 ) {
            am = new AppMessageType11(message);
        }
        else if ( appCode == 0x13 ) {
            am = new AppMessageType13(message);
        }
        else if ( appCode == 0x14 ) {
            am = new AppMessageType14(message);
        }
        else if ( appCode == 0x15 ) {
            am = new AppMessageType15(message);
        }
        else if ( appCode == 0x16 ) {
            am = new AppMessageType16(message);
        }
        else if ( appCode == 0x17 ) {
            am = new AppMessageType17(message);
        }
        else if ( appCode == 0x18 ) {
            am = new AppMessageType18(message);
        } 
        else if ( appCode == 0x22 ) {
            am = new AppMessageType22(message);
        } else {
            //log.info("Unknown message, appCode "+appCode );
        }
        return am;
    }

}

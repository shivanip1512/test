
/*-----------------------------------------------------------------------------*
*
* File:   portdialback
*
* Date:   2/16/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/05/05 15:31:43 $
*
* HISTORY      :
* $Log: portdialback.cpp,v $
* Revision 1.2  2004/05/05 15:31:43  cplender
* Implemented shared pointers.  GHMOOS.
*
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include <windows.h>
#include <process.h>
#include <iostream>
#include <iomanip>
#include <set>
using namespace std;

#include <rw/regexp.h>
#include <rw\ctoken.h>
#include <rw\thr\mutex.h>

#include "cticalls.h"
#include "ctitypes.h"
#include "cparms.h"
#include "dsm2.h"
#include "connection.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "msg_cmd.h"
#include "os2_2w32.h"
#include "pilserver.h"
#include "port_base.h"
#include "portglob.h"


VOID PortDialbackThread(void *pid)
{
    extern CtiConnection    VanGoghConnection;
    extern CtiPILServer     PIL;
    extern CtiPortManager   PortManager;
    extern CtiDeviceManager DeviceManager;

    INT            i, status = NORMAL;
    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!
    CtiPortSPtr    Port( PortManager.PortGetEqual( portid ) );      // Bump the reference count on the shared object!
    DWORD oldmask = 0, inmask = 0;
    ULONG bytesRead;
    RWCString byteString;
    bool copyBytes;
    int failedattempts;
    ULONG bytesWritten = 0;

    if(!Port)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " PortDialbackThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << " UNABLE TO START!" << endl;
        }

        return;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PortDialbackThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }

    while(!PorterQuit)
    {
        try
        {
            pair< bool, INT > portpair = Port->checkCommStatus(CtiDeviceSPtr());

            if(portpair.first)  // Port was opened on this pass.
            {
                if( portpair.second != NORMAL )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error initializing Virtual Port " << Port->getPortID() <<" on " << Port->getName() << endl;
                    }
                    continue;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Initializing Virtual Port " << Port->getPortID() <<" on " << Port->getName() << " for dialback" << endl;
                    }
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << Port->getName() << " Waiting for CD" << endl;
            }

            int tout = 0;

            try
            {
                while( !PorterQuit )
                {
                    if(!(tout++ % (4*3600)))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << Port->getName() << " - Waiting for DCD" << endl;
                        }
                    }

                    if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 500L) )
                    {
                        PorterQuit = TRUE;
                        break;
                    }
                    else if(Port->dcdTest())
                        break;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            if(PorterQuit)
            {
                break;
            }

            if(Port->dcdTest())
            {
                char mych = '\0';
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << Port->getName() << " line has been answered..." << endl;
                }

                bytesRead = -1;
                copyBytes = false;
                byteString = RWCString();

                failedattempts = 0;

                while(failedattempts < 30)
                {
                    Port->readPort(&mych, 1, 1, &bytesRead);

                    if(bytesRead != 0)
                    {
                        if(mych == 'B' || copyBytes)
                        {
                            copyBytes = true;
                            byteString.append(mych);

                            if(byteString.contains("END"))
                            {
                                break; // the while!
                            }
                        }
                    }
                    else
                    {
                        failedattempts++;
                    }
                }

                if(!byteString.isNull())
                {
                    // We need to look for the message.
                    RWCTokenizer tok(byteString);
                    RWCString tstr;
                    RWCString strdev;
                    RWCString strtime;
                    RWCString strpriority;
                    RWCString strmsg;
                    RWTime msgtime;


                    tstr = tok(); // Grab "BEGIN"
                    tstr = tok(); // Grab "ALARM"
                    if(!tstr.compareTo("ALARM"))
                    {
                        strdev = tok();         // Get the translation name from the ion.
                        strtime = tok();        // Unix time value!
                        strpriority = tok();    // Priority.

                        while(!(tstr = tok()).isNull())
                        {
                            tstr = tstr.strip(RWCString::trailing, '\r');

                            if(!tstr.compareTo("END"))
                            {
                                Port->writePort("ACK\r\n", 5, 5, &bytesWritten);
                                break;
                            }
                            else
                                strmsg += tstr + " ";
                        }
                    }

                    if(!strtime.isNull())
                    {
                        msgtime = RWTime( atoi(strtime.data()) + rwEpoch );
                    }

                    if(!strdev.isNull())
                    {
                        CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(SYS_PID_PORTER, 0, strdev + ": " + strmsg, RWCString("Priority ") + strpriority );
                        if(pSig)
                        {
                            pSig->setMessageTime(msgtime);
                            pSig->setUser("Port Control");
                            pSig->setSource("Port Control");
                            // pSig->dump();

                            VanGoghConnection.WriteConnQue( pSig );
                        }


                        CtiDeviceSPtr pDevice = DeviceManager.RemoteGetEqualbyName( strdev );
                        if(pDevice)
                        {
                            CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );

                            if(pAltRate)
                            {
                                pAltRate->insert(-1);                       // token, not yet used.
                                pAltRate->insert( pDevice->getID() );       // Device to poke.

                                int dbdelay = gConfigParms.getValueAsInt("PORTER_DIALBACK_DELAY", 0);

                                if( dbdelay )
                                {
                                    RWTime now;
                                    pAltRate->insert( (now.hour() * 3600) + (now.minute() * 60) + now.second() + dbdelay );  // Seconds since midnight, or NOW if negative.
                                }
                                else
                                {
                                    pAltRate->insert( -1 );                      // Seconds since midnight, or NOW if negative.
                                }

                                pAltRate->insert( 0 );                      // Duration of zero should cause 1 scan.

                                VanGoghConnection.WriteConnQue(pAltRate);

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Requesting scans at the alternate scan rate for " << pDevice->getName() << endl;
                                }
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Device " << strdev << " not found in the yukon database." << endl;
                        }

                        if(PorterDebugLevel & PORTER_DEBUG_DIALBACK_PILDIRECT)
                        {
                            PIL.putQueue( new CtiRequestMsg(pDevice->getID(), "scan general") );
                        }
                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Phone has NOT been answered..." << endl;
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << Port->getName() << " Hanging up the phone." << endl;
            }
            Port->disconnect(CtiDeviceSPtr(), true);
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Shutdown PortDialbackThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }


    return;
}

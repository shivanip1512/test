#include "precompiled.h"

#include <process.h>
#include <iostream>
#include <iomanip>
#include <set>

#include <rw/regexp.h>
#include <rw\ctoken.h>
#include <rw\thr\mutex.h>

#include "cticalls.h"

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

using namespace std;

void PortDialbackThread(void *pid)
{
    extern CtiConnection    VanGoghConnection;
    extern CtiPILServer     PIL;
    extern CtiPortManager   PortManager;
    extern CtiDeviceManager DeviceManager;

    INT            i, status = NORMAL;
    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!
    CtiPortSPtr    Port( PortManager.getPortById( portid ) );      // Bump the reference count on the shared object!
    DWORD oldmask = 0, inmask = 0;
    ULONG bytesRead;
    string byteString;
    bool copyBytes;
    int failedattempts;
    ULONG bytesWritten = 0;

    if(!Port)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PortDialbackThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << " UNABLE TO START!" << endl;
        }

        return;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PortDialbackThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
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
                        dout << CtiTime() << " Error initializing Virtual Port " << Port->getPortID() <<" on " << Port->getName() << endl;
                    }
                    continue;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Initializing Virtual Port " << Port->getPortID() <<" on " << Port->getName() << " for dialback" << endl;
                    }
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << Port->getName() << " Waiting for CD" << endl;
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
                            dout << CtiTime() << " " << Port->getName() << " - Waiting for DCD" << endl;
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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    dout << CtiTime() << " " << Port->getName() << " line has been answered..." << endl;
                }

                bytesRead = -1;
                copyBytes = false;
                byteString = string();

                failedattempts = 0;

                while(failedattempts < 30)
                {
                    Port->readPort(&mych, 1, 1, &bytesRead);

                    if(bytesRead != 0)
                    {
                        if(mych == 'B' || copyBytes)
                        {
                            copyBytes = true;
                            byteString.append((char*)mych);

                            if(byteString.find("END")!=string::npos)
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

                if(!byteString.empty())
                {
                    // We need to look for the message.
                    boost::tokenizer<> tok(byteString);
                    boost::tokenizer<>::iterator beg=tok.begin();


                    string tstr;
                    string strdev;
                    string strtime;
                    string strpriority;
                    string strmsg;
                    CtiTime msgtime;

                    if( beg != tok.end() )
                    {
                        tstr = *beg++; // Grab "BEGIN"
                    }

                    if( beg != tok.end() )
                    {
                        tstr = *beg++; // Grab "ALARM"
                    }
                    if(!tstr.compare("ALARM"))
                    {
                        if( beg != tok.end() )
                        {
                            strdev = *beg++;         // Get the translation name from the ion.
                        }
                        if( beg != tok.end() )
                        {
                            strtime = *beg++;        // Unix time value!
                        }
                        if( beg != tok.end() )
                        {
                            strpriority = *beg++;    // Priority.
                        }

                        while(beg != tok.end())
                        {
                            tstr = *beg++;
                            tstr = trim_right(tstr, "\r");

                            if(!tstr.compare("END"))
                            {
                                Port->writePort("ACK\r\n", 5, 5, &bytesWritten);
                                break;
                            }
                            else
                                strmsg += tstr + " ";
                        }
                    }

                    if(!strtime.empty())
                    {
                        msgtime = CtiTime( atoi(strtime.c_str()) );
                    }

                    if(!strdev.empty())
                    {
                        CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(SYS_PID_PORTER, 0, strdev + ": " + strmsg, string("Priority ") + strpriority );
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
                                    CtiTime now;
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
                                    dout << CtiTime() << " Requesting scans at the alternate scan rate for " << pDevice->getName() << endl;
                                }
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Device " << strdev << " not found in the yukon database." << endl;
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
                    dout << CtiTime() << " Phone has NOT been answered..." << endl;
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << Port->getName() << " Hanging up the phone." << endl;
            }
            Port->disconnect(CtiDeviceSPtr(), true);
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Shutdown PortDialbackThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }


    return;
}

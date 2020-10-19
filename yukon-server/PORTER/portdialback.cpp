#include "precompiled.h"

#include <process.h>
#include <iostream>
#include <iomanip>
#include <set>

#include "cticalls.h"

#include "cparms.h"
#include "dsm2.h"
#include "connection_client.h"
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
    extern CtiClientConnection  VanGoghConnection;
    extern Cti::Pil::PilServer PIL;
    extern CtiPortManager       PortManager;
    extern CtiDeviceManager     DeviceManager;
    extern std::atomic_int PortManagerThreadCount;

    INT            i, status = ClientErrors::None;
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
        CTILOG_ERROR(dout, "PortDialbackThread for port: "<< Port->getPortID() <<" / "<< Port->getName() <<"- Unable to start");
        return;
    }

    PortManagerThreadCount++;

    CTILOG_INFO(dout, "PortDialbackThread for port: "<< Port->getPortID() <<" / "<< Port->getName() <<" - Started");

    Port->startLog();

    while(!PorterQuit)
    {
        try
        {
            pair< bool, YukonError_t > portpair = Port->checkCommStatus(CtiDeviceSPtr());

            if(portpair.first)  // Port was opened on this pass.
            {
                if( portpair.second )
                {
                    CTILOG_ERROR(dout, "Cannot initialize Virtual Port "<< Port->getPortID() <<" on "<< Port->getName());
                    continue;
                }
                else
                {
                    CTILOG_INFO(dout, "Initializing Virtual Port "<< Port->getPortID() <<" on "<< Port->getName() <<" for dialback");
                }
            }

            int tout = 0;

            try
            {
                while( !PorterQuit )
                {
                    if(!(tout++ % (4*3600)))
                    {
                        CTILOG_INFO(dout, Port->getName() <<" - Waiting for DCD");
                    }
                    else
                    {
                        Port->getPortLog()->poke();  //  called 2x/second
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
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            if(PorterQuit)
            {
                break;
            }

            if(Port->dcdTest())
            {
                char mych = '\0';
                CTILOG_INFO(dout, Port->getName() <<" line has been answered...");

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

                            VanGoghConnection.WriteConnQue(pSig, CALLSITE);
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

                                VanGoghConnection.WriteConnQue(pAltRate, CALLSITE);

                                CTILOG_INFO(dout, "Requesting scans at the alternate scan rate for "<< pDevice->getName());
                            }
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "Device "<< strdev <<" not found in the yukon database.");
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
                CTILOG_ERROR(dout, Port->getName() <<" Phone has NOT been answered...")
            }

            CTILOG_INFO(dout, Port->getName() <<" Hanging up the phone.");
            Port->disconnect(CtiDeviceSPtr(), true);
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    CTILOG_INFO(dout, "Shutdown PortDialbackThread for port: "<< Port->getPortID() <<" / "<< Port->getName());

    PortManagerThreadCount--;
}


#include "precompiled.h"

/*-----------------------------------------------------------------------------*
*
* File:   fmu_application
*
* Date:   10/09/2006
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/fmu_application.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/26 20:20:18 $
*    History:
      $Log: fmu_application.cpp,v $
      Revision 1.1  2007/01/26 20:20:18  jrichter
      FMU stuff for jess....


*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "guard.h"
#include "logger.h"
#include "configparms.h"
#include "fmu_application.h"

using namespace std;

const CHAR * CtiFMUApplication::FMU_DEBUGLEVEL = "FMU_DEBUGLEVEL";
//=========================================================================================================================================
//=========================================================================================================================================

CtiFMUApplication::CtiFMUApplication()
{
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiFMUApplication::init( void )
{
    getDatalinkLayer().init();

}
//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUApplication::reinitialize( void )
{
   getDatalinkLayer().reinitialize();
   destroyMe();
}

void CtiFMUApplication::terminateSession( void )
{
}
//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUApplication::destroyMe( void )
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiFMUApplication::~CtiFMUApplication()
{
    destroyMe();
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiFMUApplication::generate(  BYTE *packetPtr, USHORT cmd, USHORT count, INT seq, ULONG address  )
{
    bool retFlag = false;
    BYTE *dataPtr;

    switch (cmd)
    {
        case ack:
        {
            //dataPtr = nothing;
            break;
        }
        case  nak:
        {
            //dataPtr = nothing;
            break;
        }
    case  commSync:
        {
            //dataPtr = nothing;
            break;
        }
        case  unSolMsg:
        {
            //dataPtr = nothing;
            break;
        }
        case  loCom:
        {
            //dataPtr = nothing;
            break;
        }
        case  timeSyncReq:
        {
            //dataPtr = nothing;
            break;
        }
        case  timeRead:
        {
            //dataPtr = nothing;
            break;
        }
        case  timeRsp:
        {
            //dataPtr = nothing;
            break;
        }
        case  dataReqCmd:
        {
            //dataPtr = nothing;
            break;
        }
        case  dataReqRsp:
        {
            //dataPtr = nothing;
            break;
        }
        case  resetMsgDataLog:
        {
            //dataPtr = nothing;
            break;
        }
        case  extDevDirCmd:
        {
            //dataPtr = nothing;
            break;
        }
        case  extDevDirRsp:
        {
            //dataPtr = nothing;
            break;
        }
        default:
            break;

    }

    getDatalinkLayer().assemblePacket( packetPtr, dataPtr, cmd,count, seq, address);
       //do next step to get logged in

     // make this generic
     //getDatalinkLayer().buildTableRequest( xfer, _currentTableID, pread_offset, _currentTableOffset, _currentType );
     //getDatalinkLayer().buildTableRequest( xfer, _currentTableID, pread_offset, _currentTableOffset, _currentType, _maxPktSize.sh, _maxNbrPkts );
     return retFlag;
}


//=========================================================================================================================================
//=========================================================================================================================================

void CtiFMUApplication::initializeDataLog( short aID, int aOffset, unsigned int aBytesExpected, BYTE aType, BYTE aOperation )
{



}
void CtiFMUApplication::buildAllUnreportedMsgsRequest(UCHAR *abuf, INT *buflen, INT xmitter)
{
    BYTE *dataPtr = NULL;

    //getDatalinkLayer().setSequence(sequence);
    dataPtr[0] = 0x01;
    INT sequence = 0xff;

    getDatalinkLayer().assemblePacket(abuf, dataPtr, 0x08, 1, sequence, xmitter);


}


//*************************************************************************************
//*************************************************************************************

bool CtiFMUApplication::decode( CtiXfer &xfer, int aCommStatus )
{
    bool retFlag = false;

    /****************************
    * returns true whether we received a valid piece of a packet
    *****************************
    */
    if (getDatalinkLayer().continueBuildingPacket( xfer, aCommStatus ))
    {
        // check if we have a full packet
        if (getDatalinkLayer().isPacketComplete())
        {
            // was CRC ok
            if (getDatalinkLayer().isCRCvalid())
            {
                // analyse the packet move on if successful
                if (analyzePacket())
                {
                    // rest number of tries
                    setRetries (MAXRETRIES);
                }
                else
                {
                    if( getRetries() > 0 )
                    {
                        setRetries( getRetries() - 1 );
                    }
                    else
                    {
                       _readFailed = true;
                    }
                }
            }
            else
            {


            }
        }
        else
        {
            // do nothing, continue in same state, datalink setup xfer appropriately
        }
    }
    else
    {
        if( getRetries() > 0 )
        {
            setRetries( getRetries() - 1 );
        }
        else
        {
            // retries exhausted, figure out how to get from here (terminate session?)
            _readFailed = true;
        }
    }
    return retFlag;
}

bool CtiFMUApplication::analyzePacket()
{
    // check length and checksum here
    bool retFlag = true;

    if( checkResponse( getDatalinkLayer().getCurrentPacket()[6] ) == true ||
        (getDatalinkLayer().getPacketPart() && !getDatalinkLayer().getPacketFirst()))
    {
        if (getDatalinkLayer().getPacketPart())
        {
            if (getDatalinkLayer().getPacketFirst())
            {
                memcpy (_currentTable+_totalBytesInTable,
                    getDatalinkLayer().getCurrentPacket()+9,
                    getDatalinkLayer().getPacketBytesReceived()-11); //header(6),crc(2),length(2),response(1)

                _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-11;

               if (getDatalinkLayer().getSequence() %2 !=0) //if odd, set toggle bit
               {
                    getDatalinkLayer().toggleToggle();
               }
            }

           else if (getDatalinkLayer().getSequence() == 0)
            {
                 // move the data into storage
                memcpy (_currentTable+_totalBytesInTable,
                   getDatalinkLayer().getCurrentPacket()+6,
                   getDatalinkLayer().getPacketBytesReceived()-9); //header(6),crc(2),cksm(1)

                _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-9;

            }
            else
            {
                 // move the data into storage
                memcpy (_currentTable+_totalBytesInTable,
                   getDatalinkLayer().getCurrentPacket()+6,
                   getDatalinkLayer().getPacketBytesReceived()-8); //header(6),crc(2)

                _totalBytesInTable += getDatalinkLayer().getPacketBytesReceived()-8;

            }
        }
    }

    return retFlag;
}

bool CtiFMUApplication::areThereMorePackets()
{
    bool retVal;
    if (getDatalinkLayer().getPacketPart() )
    {
        if(1)//getDatalinkLayer().getSequence() == 0 )
        {
            if (_totalBytesInTable < _currentBytesExpected)
            {
                retVal = true;
            }
            else
            {
                retVal = false;
            }
        }
        else
        {
            retVal = true;
        }
    }
    else
    {
        if (_totalBytesInTable < _currentBytesExpected)
        {
            retVal = true;
        }
        else
        {
            retVal = false;
        }
    }
    return retVal;
}


bool CtiFMUApplication::checkResponse( BYTE aResponseByte)
{
   bool     proceed = false;
   BYTE     msg;

   /*switch( aResponseByte )
   {
   case ok:
      {
         proceed = true;
      }
      break;

   case err:
      {
          CtiLockGuard< CtiLogger > doubt_guard( dout );
          dout << endl;
          dout << CtiTime::now() <<"The " << getFmuDeviceName() << " responded: Service Request Rejected"<< endl;
          msg = err;
      }
      break;

   case sns:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Service Not Supported" << endl;

         msg = sns;
      }
      break;

   case isc:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Insufficent Security Clearance" << endl;

         msg = isc;
      }
      break;

   case onp:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Operation Not Possible" << endl;

         msg = onp;
      }
      break;

   case iar:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Inappropriate Action Requested" << endl;

         msg = iar;
      }
      break;

   case bsy:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Device Busy" << endl;

         msg = bsy;
      }
      break;

   case dnr:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Data Not Ready" << endl;

         msg = dnr;
      }
      break;

   case dlk:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Data Locked" << endl;

         msg = dlk;
      }
      break;

   case rno:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Renegotiate Request" << endl;

         msg = rno;
      }
      break;

   case isss:
      {
         CtiLockGuard< CtiLogger > doubt_guard( dout );
         dout << endl;
         dout << CtiTime::now() << " The " << getFmuDeviceName() << " responded: Invalid Service Sequence State" << endl;

         msg = isss;
      }
      break;
   }  */
   return proceed;

}

//=========================================================================================================================================
//=========================================================================================================================================

BYTE* CtiFMUApplication::getCurrentTable( )
{
     return (_currentTable);

}


//=========================================================================================================================================
//=========================================================================================================================================

CtiFMUDatalink &CtiFMUApplication::getDatalinkLayer( void )
{
   return _datalinkLayer;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiFMUApplication::isReadComplete( void )
{
   return _readComplete;
}

bool CtiFMUApplication::isReadFailed( void )
{
   return _readFailed;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiFMUApplication &CtiFMUApplication::setRetries( int trysLeft )
{
   _retries = trysLeft;
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiFMUApplication::getRetries( void )
{
   return _retries;
}


bool CtiFMUApplication::getFMUDebugLevel(int mask)
{
    static time_t lastaccess;
    static int fmu_debuglevel;
    char *eptr;

    string   str;
    if( lastaccess + 300 < ::time(0) )
    {
        if( !(str = gConfigParms.getValueAsString(FMU_DEBUGLEVEL)).empty() )
        {
            fmu_debuglevel = strtoul(str.c_str(), &eptr, 16);
        }
        else
            fmu_debuglevel = 0;
        lastaccess = ::time(0);
    }

    return mask & fmu_debuglevel;
}



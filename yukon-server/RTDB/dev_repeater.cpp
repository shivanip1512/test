#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_repeater.cpp
*
* Date:   8/24/2001
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2002/05/16 14:47:35 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_repeater.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "prot_emetcon.h"
#include "utility.h"
#include "numstr.h"

set< CtiDLCCommandStore > CtiDeviceRepeater900::_commandStore;


bool CtiDeviceRepeater900::initCommandStore()
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd = CtiProtocolEmetcon::Scan_General;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_ModelAddr, 1);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::Command_Loop;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_ModelAddr, 1);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::PutConfig_Role;
    cs._io = IO_WRITE;
    cs._funcLen = make_pair((int)Rpt_RoleBaseAddr,
                            (int)Rpt_RoleLen);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::GetConfig_Role;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_RoleBaseAddr,
                            (int)Rpt_RoleLen);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::GetConfig_Model;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_ModelAddr,
                            (int)Rpt_ModelLen);
    _commandStore.insert( cs );

   return failed;
}


bool CtiDeviceRepeater900::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        CtiDeviceRepeater900::initCommandStore();
    }

    CTICMDSET::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        CtiDLCCommandStore &cs = *itr;
        function = cs._funcLen.first;             // Copy over the found function!
        length = cs._funcLen.second;              // Copy over the found length!
        io = cs._io;                              // Copy over the found io indicator!

        found = true;
    }

    return found;
}


INT CtiDeviceRepeater900::ExecuteRequest(CtiRequestMsg                  *pReq,
                                         CtiCommandParser               &parse,
                                         OUTMESS                        *&OutMessage,
                                         RWTPtrSlist< CtiMessage >      &vgList,
                                         RWTPtrSlist< CtiMessage >      &retList,
                                         RWTPtrSlist< OUTMESS >         &outList)
{
    INT   nRet = NoError;
    RWCString resultString;
    CtiRoute *Route = NULL;
    long  routeID;

    bool found = false;


    switch( parse.getCommand() )
    {
        case ScanRequest:
        {
            nRet = executeScan(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
        case LoopbackRequest:
        {
            nRet = executeLoopback(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
        case GetConfigRequest:
        {
            nRet = executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
        case PutConfigRequest:
        {
            nRet = executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
        case GetValueRequest:
        {
            nRet = executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand() << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if(nRet != NORMAL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Couldn't come up with an operation for device " << getName() << endl;
            dout << RWTime() << "   Command: " << pReq->CommandString() << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.insert(new CtiReturnMsg(getID(),
                                        RWCString(OutMessage->Request.CommandStr),
                                        resultString,
                                        nRet,
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.MacroOffset,
                                        OutMessage->Request.Attempt,
                                        OutMessage->Request.TrxID,
                                        OutMessage->Request.UserID,
                                        OutMessage->Request.SOE,
                                        RWOrdered()));
    }
    else
    {
        if(OutMessage != NULL)
        {
            outList.append( OutMessage );
            OutMessage = NULL;
        }
        /*
        ***************************** PASS OFF TO ROUTE BEYOND THIS POINT ****************************************
        */

        for(int i = outList.entries() ; i > 0; i-- )
        {
            OUTMESS *pOut = outList.get();

            if( pReq->RouteId() )
                routeID = pReq->RouteId();
            else
                routeID = getRouteID();

            pOut->RouteID         = routeID;
            pOut->Request.RouteID = routeID;

            EstablishOutMessagePriority( pOut, MAXPRIORITY - 4 );

            if( (Route = CtiDeviceBase::getRoute( routeID )) != NULL )    // This is "this's" route
            {
                pOut->TargetID                = getID();
                pOut->EventCode               = BWORD | RESULT | WAIT;

                if( parse.isKeyValid("noqueue") )
                {
                    pOut->EventCode |= DTRAN;
                }

                pOut->Buffer.BSt.Address      = getAddress();            // The DLC address of the MCT.
                pOut->Buffer.BSt.DeviceType   = getType();
                pOut->Buffer.BSt.SSpec        = getSSpec();

                /*
                 * OK, these are the items we are about to set out to perform..  Any additional signals will
                 * be added into the list upon completion of the Execute!
                 */
                if(parse.getActionItems().entries())
                {
                    for(size_t offset = 0 ; offset < parse.getActionItems().entries(); offset++)
                    {
                        RWCString actn = parse.getActionItems()[offset];
                        RWCString desc = getDescription(parse);
                        vgList.insert(new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                    }
                }

                /*
                 *  Form up the reply here since the ExecuteRequest funciton will consume the
                 *  OutMessage.
                 */
                CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                                      RWCString(pOut->Request.CommandStr),
                                                      Route->getName(),
                                                      nRet,
                                                      pOut->Request.RouteID,
                                                      pOut->Request.MacroOffset,
                                                      pOut->Request.Attempt,
                                                      pOut->Request.TrxID,
                                                      pOut->Request.UserID,
                                                      pOut->Request.SOE,
                                                      RWOrdered());

                // Start the control request on its route(s)
                if( (nRet = Route->ExecuteRequest(pReq, parse, pOut, vgList, retList, outList)) )
                {
                    resultString = "ERROR" + CtiNumStr(nRet).spad(3) + "performing command on route " + Route->getName().data();
                    pRet->setStatus(nRet);
                    pRet->setResultString(resultString);
                    retList.insert( pRet );
                }
                else
                {
                    delete pRet;
                }
            }
            else
            {
                nRet = NoRouteGroupDevice;

                resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();

                CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                                      RWCString(pOut->Request.CommandStr),
                                                      resultString,
                                                      nRet,
                                                      pOut->Request.RouteID,
                                                      pOut->Request.MacroOffset,
                                                      pOut->Request.Attempt,
                                                      pOut->Request.TrxID,
                                                      pOut->Request.UserID,
                                                      pOut->Request.SOE,
                                                      RWOrdered());

                retList.insert( pRet );
            }
        }
    }

    return nRet;
}


INT CtiDeviceRepeater900::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }


        if(getOperation(CtiProtocolEmetcon::Command_Loop, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            OutMessage->RouteID   = getRouteID();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = CtiProtocolEmetcon::Scan_General;     // Helps us figure it out later!
            OutMessage->Retry     = 3;

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strcpy(OutMessage->Request.CommandStr, "loop");

            outList.insert(OutMessage);
            OutMessage = NULL;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device " << getName() << endl;

            status = NoMethod;
        }
    }

    return status;
}


INT CtiDeviceRepeater900::executeLoopback(CtiRequestMsg                  *pReq,
                                          CtiCommandParser               &parse,
                                          OUTMESS                        *&OutMessage,
                                          RWTPtrSlist< CtiMessage >      &vgList,
                                          RWTPtrSlist< CtiMessage >      &retList,
                                          RWTPtrSlist< OUTMESS >         &outList)
{
    bool found = false;
    INT   nRet = NoError;
    CHAR Temp[80];

    INT function;

    function = CtiProtocolEmetcon::Command_Loop;
    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->RouteID   = getRouteID();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceRepeater900::executeGetConfig(CtiRequestMsg                  *pReq,
                                           CtiCommandParser               &parse,
                                           OUTMESS                        *&OutMessage,
                                           RWTPtrSlist< CtiMessage >      &vgList,
                                           RWTPtrSlist< CtiMessage >      &retList,
                                           RWTPtrSlist< OUTMESS >         &outList)
{
    bool found = false;
    INT   nRet = NoError;
    CHAR Temp[80];

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.

    //  get repeater role info
    if(parse.isKeyValid("rolenum"))
    {
        //  correct for 1-based numbering (C++ and repeater's memory want 0-based)
        int rolenum = parse.getiValue("rolenum") - 1;

        function = CtiProtocolEmetcon::GetConfig_Role;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        //  add on offset if it's role 2-24, else default to role 1 (no offset)
        if( rolenum > 0 && rolenum < 24 )
        {
            rolenum -= rolenum % 6;  //  start from the closest multiple of 6
            OutMessage->Buffer.BSt.Function += rolenum * Rpt_RoleLen;
        }

        //  get 6 roles
        OutMessage->Buffer.BSt.Length *= 6;
    }
    else if(parse.isKeyValid("model"))
    {
        function = CtiProtocolEmetcon::GetConfig_Model;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->RouteID   = getRouteID();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 3;

        strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceRepeater900::executePutConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   RWTPtrSlist< CtiMessage >      &vgList,
                                   RWTPtrSlist< CtiMessage >      &retList,
                                   RWTPtrSlist< OUTMESS >         &outList)
{
    bool  found = false;
    INT   nRet = NoError;
    CHAR  Temp[80];
    RWCString temp2;

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.

    //  get repeater role info
    if(parse.isKeyValid("rolenum"))
    {
       //  correct for 1-based numbering
       int rolenum = parse.getiValue("rolenum") - 1;

       function = CtiProtocolEmetcon::PutConfig_Role;
       found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

       //  add on offset if it's role 2-24, else default to role 1 (no offset)
       if( rolenum > 0 && rolenum < 24 )
       {
           OutMessage->Buffer.BSt.Function += rolenum * Rpt_RoleLen;
       }

       Temp[0]  = parse.getiValue("rolefixed") & 0x1F;
       Temp[0] |= parse.getiValue("roleout") << 5;
       Temp[1]  = (parse.getiValue("rolerpt") << 1) & 0x1F;
       Temp[1] |= parse.getiValue("rolein") << 5;
       Temp[1] |= 0x01;  //  set lowest bit to 1 instead of 0, per spec

       OutMessage->Buffer.BSt.Message[0] = Temp[0];
       OutMessage->Buffer.BSt.Message[1] = Temp[1];
    }

    if(!found)
    {
       nRet = NoMethod;
    }
    else
    {
       // Load all the other stuff that is needed
       OutMessage->DeviceID  = getID();
       OutMessage->TargetID  = getID();
       OutMessage->Port      = getPortID();
       OutMessage->Remote    = getAddress();
       OutMessage->RouteID   = getRouteID();
       OutMessage->TimeOut   = 2;
       OutMessage->Sequence  = function;     // Helps us figure it out later!
       OutMessage->Retry     = 3;

       // Tell the porter side to complete the assembly of the message.
       strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
    }

    return nRet;
}


INT CtiDeviceRepeater900::executeGetValue(CtiRequestMsg                  *pReq,
                                          CtiCommandParser               &parse,
                                          OUTMESS                        *&OutMessage,
                                          RWTPtrSlist< CtiMessage >      &vgList,
                                          RWTPtrSlist< CtiMessage >      &retList,
                                          RWTPtrSlist< OUTMESS >         &outList)
{
   bool found = false;
   INT   nRet = NoError;
   CHAR Temp[80];

   INT function;

   //  for the rpt 800
   if(parse.getFlags() & CMD_FLAG_GV_PFCOUNT)
   {
       function = CtiProtocolEmetcon::GetValue_PFCount;
       found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
   }

   if(!found)
   {
      nRet = NoMethod;
   }
   else
   {
      // Load all the other stuff that is needed
      OutMessage->DeviceID  = getID();
      OutMessage->TargetID  = getID();
      OutMessage->Port      = getPortID();
      OutMessage->Remote    = getAddress();
      OutMessage->RouteID   = getRouteID();
      OutMessage->TimeOut   = 2;
      OutMessage->Sequence  = function;         // Helps us figure it out later!
      OutMessage->Retry     = 3;

      // Tell the porter side to complete the assembly of the message.
      strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
   }

   return nRet;
}


INT CtiDeviceRepeater900::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;


   switch(InMessage->Sequence)
   {
   case (CtiProtocolEmetcon::Command_Loop):
      {
         status = decodeLoopback(InMessage, TimeNow, vgList, retList, outList);
         break;
      }

   case (CtiProtocolEmetcon::GetConfig_Model):
      {
         status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
         break;
      }

   case (CtiProtocolEmetcon::PutConfig_Role):
      {
         status = decodePutConfigRole(InMessage, TimeNow, vgList, retList, outList);
         break;
      }

   case (CtiProtocolEmetcon::GetConfig_Role):
      {
         status = decodeGetConfigRole(InMessage, TimeNow, vgList, retList, outList);
         break;
      }
   default:
      {
          {
             CtiLockGuard<CtiLogger> doubt_guard(dout);
             dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
             dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
          }
          status = NoMethod;
          break;
      }
   }

   return status;
}


INT CtiDeviceRepeater900::decodeLoopback(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   CtiCommandParser parse(InMessage->Return.CommandStr);

   resetScanPending();

   if(!decodeCheckErrorReturn(InMessage, retList))
   {
      // No error occured, we must do a real decode!

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
      CtiPointDataMsg      *pData = NULL;

      if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);

      ReturnMsg->setResultString( getName() + " / loopback successful" );

      retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
   }

   return status;
}


INT CtiDeviceRepeater900::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   CtiCommandParser parse(InMessage->Return.CommandStr);


   if(!decodeCheckErrorReturn(InMessage, retList))
   {
      // No error occured, we must do a real decode!

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
      CtiPointDataMsg      *pData = NULL;

      if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);

      int  sspec;
      char revision;
      RWCString modelStr;

      sspec  = DSt->Message[0] << 8;
      sspec |= DSt->Message[1];

      revision = DSt->Message[2] + '@';  //  '@' is just before 'A' - thus 1 represents 'A', as intended

      modelStr = getName() + " / sspec: " + CtiNumStr(sspec) + RWCString(revision);

      ReturnMsg->setResultString( modelStr );

      retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
   }

   return status;
}


INT CtiDeviceRepeater900::decodeGetConfigRole(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   CtiCommandParser parse(InMessage->Return.CommandStr);


   if(!decodeCheckErrorReturn(InMessage, retList))
   {
      // No error occured, we must do a real decode!

      int   rolenum;
      unsigned char *buf;
      RWCString roleStr, tmpStr;

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
      CtiPointDataMsg      *pData = NULL;

      if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);


      rolenum = parse.getiValue( "rolenum" );
      rolenum -= (rolenum - 1) % 6;
      buf = InMessage->Buffer.DSt.Message;

      for( int i = 0; i < 6; i++ )
      {
          tmpStr = getName() + " / role " + CtiNumStr(i+rolenum).spad(2) + ": ";
          tmpStr += "F = " + CtiNumStr((int)(buf[(i*2)+0] & 0x1F)).spad(2)        + ", " +
                    "O = " + CtiNumStr((int)((buf[(i*2)+0] & 0xE0) >> 5)).spad(2) + ", " +
                    "I = " + CtiNumStr((int)((buf[(i*2)+1] & 0xE0) >> 5)).spad(2) + ", " +
                    "S = " + CtiNumStr((int)((buf[(i*2)+1] & 0x1E) >> 1)).spad(2) + "\n";

          roleStr += tmpStr;
      }

      ReturnMsg->setResultString( roleStr );

      retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
   }

   return status;
}


INT CtiDeviceRepeater900::decodePutConfigRole(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;


   if(!decodeCheckErrorReturn(InMessage, retList))
   {
      INT   j;
      ULONG pfCount = 0;
      RWCString resultString;

      CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

      if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);

      resultString = getName() + " / command complete";

      ReturnMsg->setResultString( resultString );

      retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
   }

   return status;
}



INT CtiDeviceRepeater900::getSSpec() const
{
   return 0;
}

CtiDeviceRepeater900::CtiDeviceRepeater900() { }

CtiDeviceRepeater900::CtiDeviceRepeater900(const CtiDeviceRepeater900& aRef)
{
   *this = aRef;
}

CtiDeviceRepeater900::~CtiDeviceRepeater900() { }

CtiDeviceRepeater900& CtiDeviceRepeater900::operator=(const CtiDeviceRepeater900& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


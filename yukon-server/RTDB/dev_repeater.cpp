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
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2004/01/26 21:53:19 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <windows.h>
#include <rw\ctoken.h>

#include "device.h"
#include "devicetypes.h"
#include "dev_repeater.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "utility.h"
#include "numstr.h"

set< CtiDLCCommandStore > CtiDeviceRepeater900::_commandStore;


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


bool CtiDeviceRepeater900::initCommandStore()
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd = CtiProtocolEmetcon::Scan_General;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_ModelPos, 1);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::Command_Loop;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_ModelPos, 1);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::PutConfig_Role;
    cs._io = IO_WRITE;
    cs._funcLen = make_pair((int)Rpt_RoleBasePos,
                            (int)Rpt_RoleLen);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::GetConfig_Role;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_RoleBasePos,
                            (int)Rpt_RoleLen);
    _commandStore.insert( cs );

    cs._cmd = CtiProtocolEmetcon::GetConfig_Model;
    cs._io = IO_READ;
    cs._funcLen = make_pair((int)Rpt_ModelPos,
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

    DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

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
    int nRet = NoError;
    RWTPtrSlist< OUTMESS > tmpOutList;

    switch( parse.getCommand() )
    {
        case ScanRequest:
        {
            nRet = executeScan(pReq, parse, OutMessage, vgList, retList, tmpOutList);
            break;
        }
        case LoopbackRequest:
        {
            nRet = executeLoopback(pReq, parse, OutMessage, vgList, retList, tmpOutList);
            break;
        }
        case GetConfigRequest:
        {
            nRet = executeGetConfig(pReq, parse, OutMessage, vgList, retList, tmpOutList);
            break;
        }
        case PutConfigRequest:
        {
            nRet = executePutConfig(pReq, parse, OutMessage, vgList, retList, tmpOutList);
            break;
        }
        case GetValueRequest:
        {
            nRet = executeGetValue(pReq, parse, OutMessage, vgList, retList, tmpOutList);
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
        RWCString resultString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Couldn't come up with an operation for device " << getName() << endl;
            dout << RWTime() << "   Command: " << pReq->CommandString() << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.insert(CTIDBG_new CtiReturnMsg(getID(),
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
            tmpOutList.append( OutMessage );
            OutMessage = NULL;
        }

        executeOnDLCRoute(pReq, parse, OutMessage, tmpOutList, vgList, retList, outList, true);
    }

    return nRet;
}


INT CtiDeviceRepeater900::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
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
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = CtiProtocolEmetcon::Scan_General;     // Helps us figure it out later!
            OutMessage->Retry     = 3;

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            OutMessage->Request.RouteID = getRouteID();
            strncpy(OutMessage->Request.CommandStr, "loop", COMMAND_STR_SIZE);

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
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 3;
        OutMessage->Request.RouteID = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);
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
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 3;

        OutMessage->Request.RouteID = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);
    }

    return nRet;
}


INT CtiDeviceRepeater900::executePutConfig(CtiRequestMsg          *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   RWTPtrSlist< CtiMessage >      &vgList,
                                   RWTPtrSlist< CtiMessage >      &retList,
                                   RWTPtrSlist< OUTMESS >         &outList)
{
    int   i;
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
    else if(parse.isKeyValid("multi_rolenum"))
    {
       function = CtiProtocolEmetcon::PutConfig_Role;
       found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

       int fixbits;
       int varbits_out;
       int varbits_in;
       int stagestf;

       int j;
       int msgcnt = ((parse.getiValue("multi_rolecount") + 6) / 6);         // This is the number of OutMessages to send.
       int firstrole = parse.getiValue("multi_rolenum") - 1;                // The first role to send.
       int rolenum = parse.getiValue("multi_rolenum") - 1;                  // The first role to send.

       RWCString strFixed = parse.getsValue("multi_rolefixed");
       RWCString strVarOut = parse.getsValue("multi_roleout");
       RWCString strVarIn = parse.getsValue("multi_rolein");
       RWCString strStages = parse.getsValue("multi_rolerpt");

       RWCString strTemp;

       RWCTokenizer fixtok(strFixed);
       RWCTokenizer vouttok(strVarOut);
       RWCTokenizer vintok(strVarIn);
       RWCTokenizer stftok(strStages);

       for(j = 0; j < msgcnt; j++)
       {
           rolenum = firstrole + (j * 6);       // This is where we begin.

           OUTMESS *pOutMessage = CTIDBG_new OUTMESS(*OutMessage);  // Copy construct based upon OutMessage.

           // Ok, multi_role always fills the message (6 roles or bust), unless we are filling the high role(ers)
           if(rolenum > 18)
           {
               pOutMessage->Buffer.BSt.Length = pOutMessage->Buffer.BSt.Length * (24 - rolenum);
           }
           else
           {
               pOutMessage->Buffer.BSt.Length = pOutMessage->Buffer.BSt.Length * 6;
           }

           //  add on offset if it's role 2-24, else default to role 1 (no offset)
           if( rolenum > 0 && rolenum < 24 )
           {
               pOutMessage->Buffer.BSt.Function += rolenum * Rpt_RoleLen;
           }

           for(i = 0; i < pOutMessage->Buffer.BSt.Length; i = i + 2)       // This is the number of defined roles.
           {
               strTemp = fixtok();
               fixbits = !strTemp.isNull() ? atoi(strTemp.data()) : 31;

               strTemp = vouttok();
               varbits_out = !strTemp.isNull() ? atoi(strTemp.data()) : 7;

               strTemp = vintok();
               varbits_in = !strTemp.isNull() ? atoi(strTemp.data()) : 7;

               strTemp = stftok();
               stagestf = !strTemp.isNull() ? atoi(strTemp.data()) : 15;

               #if 0
               {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                   dout << " Role       " << rolenum + i/2 << endl;
                   dout << " Fix   bits " << fixbits << endl;
                   dout << " OD    bits " << varbits_out << endl;
                   dout << " ID    bits " << varbits_in << endl;
                   dout << " STF   bits " << stagestf << endl;
               }
               #endif

               Temp[0]  = fixbits & 0x1F;
               Temp[0] |= varbits_out << 5;
               Temp[1]  = (stagestf << 1) & 0x1F;
               Temp[1] |= varbits_in << 5;
               Temp[1] |= 0x01;  //  set lowest bit to 1 instead of 0, per spec

               pOutMessage->Buffer.BSt.Message[i] = Temp[0];
               pOutMessage->Buffer.BSt.Message[i+1] = Temp[1];
           }

           // Load all the other stuff that is needed
           pOutMessage->DeviceID  = getID();
           pOutMessage->TargetID  = getID();
           pOutMessage->Port      = getPortID();
           pOutMessage->Remote    = getAddress();
           pOutMessage->TimeOut   = 2;
           pOutMessage->Sequence  = function;     // Helps us figure it out later!
           pOutMessage->Retry     = 3;

           OutMessage->Request.RouteID = getRouteID();
           // Tell the porter side to complete the assembly of the message.
           strncpy(pOutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

           outList.insert( pOutMessage );
       }

       delete OutMessage;
       OutMessage = 0;          // Make the original go away!
    }

    if(!found)
    {
       nRet = NoMethod;
    }
    else if(OutMessage)
    {
       // Load all the other stuff that is needed
       OutMessage->DeviceID  = getID();
       OutMessage->TargetID  = getID();
       OutMessage->Port      = getPortID();
       OutMessage->Remote    = getAddress();
       OutMessage->TimeOut   = 2;
       OutMessage->Sequence  = function;     // Helps us figure it out later!
       OutMessage->Retry     = 3;

       OutMessage->Request.RouteID = getRouteID();
       // Tell the porter side to complete the assembly of the message.
       strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);
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
      OutMessage->TimeOut   = 2;
      OutMessage->Sequence  = function;         // Helps us figure it out later!
      OutMessage->Retry     = 3;

      OutMessage->Request.RouteID = getRouteID();
      // Tell the porter side to complete the assembly of the message.
      strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);
   }

   return nRet;
}


INT CtiDeviceRepeater900::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;


   switch(InMessage->Sequence)
   {
   case (CtiProtocolEmetcon::Scan_General):
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

   if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
   {
      // No error occured, we must do a real decode!

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
      CtiPointDataMsg      *pData = NULL;

      if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);

      ReturnMsg->setResultString( getName() + " / loopback successful" );

      retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
   }

   return status;
}


INT CtiDeviceRepeater900::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   CtiCommandParser parse(InMessage->Return.CommandStr);


   if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
   {
      // No error occured, we must do a real decode!

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
      CtiPointDataMsg      *pData = NULL;

      if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
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

      retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
   }

   return status;
}


INT CtiDeviceRepeater900::decodeGetConfigRole(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   CtiCommandParser parse(InMessage->Return.CommandStr);


   if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
   {
      // No error occured, we must do a real decode!

      int   rolenum;
      unsigned char *buf;
      RWCString roleStr, tmpStr;

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
      CtiPointDataMsg      *pData = NULL;

      if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
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

      retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
   }

   return status;
}


INT CtiDeviceRepeater900::decodePutConfigRole(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;


   if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
   {
      INT   j;
      ULONG pfCount = 0;
      RWCString resultString;

      CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

      if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

         return MEMORY;
      }

      ReturnMsg->setUserMessageId(InMessage->Return.UserID);

      resultString = getName() + " / command complete";

      ReturnMsg->setResultString( resultString );

      retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
   }

   return status;
}



INT CtiDeviceRepeater900::getSSpec() const
{
   return 0;
}



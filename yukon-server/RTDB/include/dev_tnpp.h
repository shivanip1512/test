/*-----------------------------------------------------------------------------*
*
* File:   dev_tnpp
*
* Class:  CtiDeviceTnppPagingTerminal
* Date:   5/9/2000
*
* Author: Jess Otteson
*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_tnpp.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:20:30 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_TNPP_H__
#define __DEV_TNPP_H__
#pragma warning( disable : 4786)



#include <rw\thr\mutex.h>

#include "tbl_dv_tnpp.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"

class IM_EX_DEVDB CtiDeviceTnppPagingTerminal  : public CtiDeviceRemote
{
protected:

   queue< CtiVerificationBase * >  _verification_objects;
   CtiTableDeviceTnpp              _table;

   BYTE                            _outBuffer[505];
   BYTE                            _inBuffer[100];
   ULONG                           _inCountActual;

public:

   typedef CtiDeviceRemote Inherited;

   CtiDeviceTnppPagingTerminal();
   CtiDeviceTnppPagingTerminal(const CtiDeviceTnppPagingTerminal& aRef);
   virtual ~CtiDeviceTnppPagingTerminal();

   CtiDeviceTnppPagingTerminal& operator=(const CtiDeviceTnppPagingTerminal& aRef);

   INT decode(CtiXfer &xfer,INT commReturnValue);
   INT generate(CtiXfer  &xfer);

   int recvCommRequest( OUTMESS *OutMessage );

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                          CtiCommandParser               &parse,
                          OUTMESS                        *&OutMessage,
                          RWTPtrSlist< CtiMessage >      &vgList,
                          RWTPtrSlist< CtiMessage >      &retList,
                          RWTPtrSlist< OUTMESS >         &outList);

   bool isTransactionComplete();

   void getVerificationObjects(queue< CtiVerificationBase * > &work_queue);

   enum CommandState
   {
       Normal=0,
       Complete
   };

   enum protocol
   {
       TnppPublicProtocolGolay = 6487
   };

   enum StateMachine
   {
       StateHandshakeInitialize = 0,
       StateEnd,
       StateDecodeResponse,
       StateDecodeHandshake,
       StateDoNothing,
       StateSendHandshakeResponse,
       StateGenerateZeroPacket,
       StateGeneratePacket


   };

   static string getBaseFromEncodedGolayCapcode(string &capcode);
   static int getFunctionfromEncodedGolayCapcode(string &capcode);
   static string createEncodedCapcodeFromBaseAndFunction(string &golayString, int function);

   //below enums???
protected:
   CommandState                  _command;

private:
    StateMachine         _previousState;
    StateMachine         _currentState;
    int                 _retryCount;
    int                 _serialNumber;
    int                 _transmissionCount;

   StateMachine getCurrentState();
   StateMachine getPreviousState();
   void resetStates();
   void setCurrentState(StateMachine newCurrentState);
   void setPreviousState(StateMachine newPreviousState);
   string getSerialNumber();
   const char* getPagerProtocol();
   const char* getPagerDataFormat();
   const char* getFunctionCode();
   string getGolayCapcode();

   int getExtendedFunctionCapcode(int a);//returns capcode for TNPP
   const char* getExtendedFunctionCode();//returns actual function code

   unsigned int crc16( const unsigned char *data, int length );

   OUTMESS _outMessage;

static const char *_SOH;
static const char *_STX;
static const char *_ETX;
static const char *_EOT;
static const char *_ENQ;
static const char *_ACK;
static const char *_NAK;
static const char *_RS;
static const char *_CAN;
static const char *_zero_origin;
static const char *_zero_serial;
static const char *_type_golay;    
static const char *_type_flex;      
static const char *_type_pocsag;
static const char *_type_pocsag_1200;
static const char *_type_pocsag_2400;
static const char *_type_numeric;
static const char *_type_alphanumeric;
static const char *_type_beep;
static const char *_function_1;
static const char *_function_2;
static const char *_function_3;
static const char *_function_4;

static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_0;  
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_2;  
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_4;  
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_6; 
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_8;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_10;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_12;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_14;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_16;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_18;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_20;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_22;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_24;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_26;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_28;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_30;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_32;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_34;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_36;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_38;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_40;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_42;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_44;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_46;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_48; 
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_50;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_52;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_54;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_56;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_58;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_60;
static const int CtiDeviceTnppPagingTerminal::_ext_function_capcode_62;
static const int CtiDeviceTnppPagingTerminal::_a_capcode_max;
static const int CtiDeviceTnppPagingTerminal::_a_capcode_min;

};                                
                                  
#endif // #ifndef __DEV_TNPP_H__  


#pragma warning( disable : 4786)
#ifndef __PORT_DIRECT_H__
#define __PORT_DIRECT_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_direct
*
* Class:  CtiPortDirect
* Date:   3/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_direct.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "port_base.h"
#include "tbl_port_serial.h"

class IM_EX_PRTDB CtiPortDirect : public CtiPort
{
protected:

   HANDLE                     _portHandle;
   CtiTablePortLocalSerial    _localSerial;

private:

public:

   typedef CtiPortBase  Inherited;

   CtiPortDirect();/* :
      _portHandle(NULL){}
                         */
   CtiPortDirect(const CtiPortDirect& aRef);/* :
      _portHandle(NULL)
   {
      *this = aRef;
   }                      */

   virtual ~CtiPortDirect();/*
   {
      if(_portHandle != NULL)
      {
         CloseHandle(_portHandle);
         _portHandle = NULL;
      }
   }                          */

   CtiPortDirect& operator=(const CtiPortDirect& aRef);/*
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);

         _portHandle    = aRef.getHandle();
         _localSerial   = aRef.getLocalSerial();
      }
      return *this;
   }                                                     */

   CtiTablePortLocalSerial          getLocalSerial() const;
   CtiTablePortLocalSerial&         getLocalSerial();

   CtiPortDirect& setLocalSerial(const CtiTablePortLocalSerial& aRef);

   virtual RWCString getPhysicalPort() const;

   virtual HANDLE          getHandle() const;
   virtual HANDLE&         getHandle();
   virtual HANDLE*         getHandlePtr();

   CtiPortDirect&          setHandle(const HANDLE& hdl);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT init();
   virtual INT close(INT trace);


   virtual INT baudRate(INT rate = 0) const;     // Set/reset the port's baud rate to the DB value
   virtual INT byteTime(ULONG bytes) const;
   virtual INT ctsTest() const;
   virtual INT dcdTest() const;
   virtual INT lowerRTS() const;
   virtual INT raiseRTS() const;
   virtual INT lowerDTR() const;
   virtual INT raiseDTR() const;

   virtual INT inClear() const;
   virtual INT outClear() const;

   virtual INT inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);

   INT checkCommStatus(INT trace);
   INT isCTS() const;
   INT isDCD() const;
   INT isDSR() const;


};
#endif // #ifndef __PORT_DIRECT_H__

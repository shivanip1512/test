
#pragma warning( disable : 4786)
#ifndef __PROT_711_H__
#define __PROT_711_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_711
*
* Class:  CtiProtocol711
* Date:   4/27/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/prot_711.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <utility>
using namespace std;

#include "porter.h"

class IM_EX_PROT CtiProtocol711
{
protected:

   BYTE  _masterToSlave[261];
   BYTE  _slaveToMaster[261];

private:

public:
   CtiProtocol711() {}

   CtiProtocol711(const CtiProtocol711& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiProtocol711() {}

   CtiProtocol711& operator=(const CtiProtocol711& aRef)
   {
      if(this != &aRef)
      {
      }
      return *this;
   }

   CtiProtocol711& setMasterRequest(const BYTE *ptr);
   CtiProtocol711& setSlaveResponse(const BYTE *ptr);

   void describeSlaveResponse() const;
   void describeMasterRequest() const;
   void describeACTNRequest(const BYTE *data, INT len) const;
   void describeDTRANRequest(const BYTE *data, INT len) const;
   void describeLGRPQRequest(const BYTE *data, INT len) const;
   void describeRCOLQRequest(const BYTE *data, INT len) const;
   void describeXTIMERequest(const BYTE *data, INT len) const;
   void describeSlaveStatS(const BYTE *stat) const;
   void describeSlaveStatD(const BYTE *stat) const;
   void describeSlaveStatP(const BYTE *stat) const;



   void describeRCOLQResponse(const BYTE *data, INT len) const;
   void describeACTNResponse() const;

};
#endif // #ifndef __PROT_711_H__


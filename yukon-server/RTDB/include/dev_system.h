/*-----------------------------------------------------------------------------*
*
* File:   dev_system
*
* Class:  CtiDeviceSystem
* Date:   3/21/2000
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_system.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SYSTEM_H__
#define __DEV_SYSTEM_H__
#pragma warning( disable : 4786)


#include "dev_base.h"


class CtiDeviceSystem : public CtiDeviceBase
{
private:

    typedef CtiDeviceBase Inherited;

protected:

public:

   CtiDeviceSystem();

   CtiDeviceSystem(const CtiDeviceSystem& aRef);

   virtual ~CtiDeviceSystem();

   CtiDeviceSystem& operator=(const CtiDeviceSystem& aRef);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual std::string getDescription(const CtiCommandParser &parse) const;

   virtual INT  ProcessResult(INMESS*, CtiTime&, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

};
#endif // #ifndef __DEV_SYSTEM_H__

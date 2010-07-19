/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_tnpp
*
* Date:   6/28/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_tnpp.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_TNPP_H__
#define __TBL_DV_TNPP_H__


#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include <string>
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceTnpp : public CtiMemDBObject
{
protected:

   LONG                 _deviceID;
  // INT                  _originAddress;   //computers address?
   unsigned short       _inertia;
   int                  _destinationAddress; //The tnpp devices address
   int                  _originAddress;
   string            _identifierFormat;
   string            _pagerProtocol;
   string            _dataFormat;
   string            _channel;
   string            _zone;
   string            _functionCode;
   string            _pagerID;
private:

public:

    CtiTableDeviceTnpp();

    CtiTableDeviceTnpp(const CtiTableDeviceTnpp& aRef);

    virtual ~CtiTableDeviceTnpp();

    CtiTableDeviceTnpp& operator=(const CtiTableDeviceTnpp& aRef);

    LONG getDeviceID() const;
    unsigned short getInertia() const;
    int getDestinationAddress() const; //The tnpp devices address
    int getOriginAddress() const; //The tnpp devices address
    string getIdentifierFormat();
    string getPagerProtocol();
    string getPagerDataFormat();
    string getChannel();
    string getZone();
    string getFunctionCode();
    string getPagerID();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    static string getTableName();
    virtual bool Update();
    virtual bool Insert();
    virtual bool Delete();
};
#endif // #ifndef __TBL_DV_TAPPAGING_H__

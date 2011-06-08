
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_golay
*
* Class:  CtiTableGolayGroup
* Date:   2/4/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
* HISTORY      :
* $Log: tbl_lmg_golay.h,v $
* Revision 1.2.24.1  2008/11/13 17:23:49  jmarks
* YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008
*
* Responded to reviewer comments again.
*
* I eliminated excess references to windows.h .
*
* This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.
*
* None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
* Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.
*
* In this process I occasionally deleted a few empty lines, and when creating the define, also added some.
*
* This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.
*
* Some of the date changes in BOOST
* esp. w.r.t. DST have yet to be fully resolved.
*
* Revision 1.2  2005/12/20 17:16:08  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_GOLAY_H__
#define __TBL_LMG_GOLAY_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "row_reader.h"

#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"


class IM_EX_CTIYUKONDB CtiTableGolayGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    std::string _operationalAddress;
    int _nominalTimeout;        // Switch is hardcoded to be off for this duration in seconds!
    int _virtualTimeout;        // Group is desired to control for this duration in seconds.  If these are not equal multiple control messages must be sent to make it occur (master cycle-like)

public:

    CtiTableGolayGroup();
    CtiTableGolayGroup(const CtiTableGolayGroup& aRef);
    virtual ~CtiTableGolayGroup();

    CtiTableGolayGroup& operator=(const CtiTableGolayGroup& aRef);

    LONG getLmGroupId() const;
    LONG getRouteId() const;
    std::string getOperationalAddress() const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    int getFunction() const;
    int getNominalTimeout() const;
    int getVirtualTimeout() const;

    CtiTableGolayGroup& setLmGroupId(LONG newVal);
    CtiTableGolayGroup& setRouteId(LONG newVal);
    CtiTableGolayGroup& setOperationalAddress(std::string newVal);
    CtiTableGolayGroup& setFunction(int newVal);          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableGolayGroup& setNominalTimeout(int newVal);
    CtiTableGolayGroup& setVirtualTimeout(int newVal);

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
#endif // #ifndef __TBL_LMG_GOLAY_H__

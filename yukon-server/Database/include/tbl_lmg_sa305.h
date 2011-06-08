
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa305
*
* Class:  CtiTableSA305LoadGroup
* Date:   1/14/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
* HISTORY      :
* $Log: tbl_lmg_sa305.h,v $
* Revision 1.3.24.1  2008/11/13 17:23:49  jmarks
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
* Revision 1.3  2005/12/20 17:16:08  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.2.4.1  2005/07/12 21:08:34  jliu
* rpStringWithoutCmpParser
*
* Revision 1.2  2004/03/18 19:46:44  cplender
* Added code to support the SA305 protocol and load group
*
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_SA305_H__
#define __TBL_LMG_SA305_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>


#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableSA305LoadGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    int _utility;           // 4 bit address
    int _group;             // 6 bit address
    int _division;          // 6 bit address
    int _substation;        // 10 bit address
    int _individual;        // 22 bit serial number.  4173802 = 3FAFEA is an all call.

    int _rateFamily;        // 3 bits
    int _rateMember;        // 4 bits
    int _hierarchy;         // 1 bit

    int _function;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    std::string _addressUsage;      // Identifies which addressing components to use.


private:

public:

    typedef CtiMemDBObject Inherited;


    CtiTableSA305LoadGroup();
    CtiTableSA305LoadGroup(const CtiTableSA305LoadGroup& aRef);

    virtual ~CtiTableSA305LoadGroup();

    CtiTableSA305LoadGroup& operator=(const CtiTableSA305LoadGroup& aRef);

    LONG getLmGroupId() const;
    LONG getRouteId() const;
    int getUtility() const;           // 4 bit address
    int getGroup() const;             // 6 bit address
    int getDivision() const;          // 6 bit address
    int getSubstation() const;        // 10 bit address
    int getIndividual() const;        // 22 bit serial number.  4173802 = 3FAFEA is an all call.
    int getRateFamily() const;        // 3 bits
    int getRateMember() const;        // 4 bits
    int getHierarchy() const;         // 1 bit
    int getFunction() const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    std::string  getAddressUsage() const;

    CtiTableSA305LoadGroup& setLmGroupId(LONG newVal);
    CtiTableSA305LoadGroup& setRouteId(LONG newVal);
    CtiTableSA305LoadGroup& setUtility(int newVal);           // 4 bit address
    CtiTableSA305LoadGroup& setGroup(int newVal);             // 6 bit address
    CtiTableSA305LoadGroup& setDivision(int newVal);          // 6 bit address
    CtiTableSA305LoadGroup& setSubstation(int newVal);        // 10 bit address
    CtiTableSA305LoadGroup& setIndividual(int newVal);        // 22 bit serial number.  4173802 = 3FAFEA is an all call.
    CtiTableSA305LoadGroup& setRateFamily(int newVal);        // 3 bits
    CtiTableSA305LoadGroup& setRateMember(int newVal);        // 4 bits
    CtiTableSA305LoadGroup& setHierarchy(int newVal);         // 1 bit
    CtiTableSA305LoadGroup& setFunction(int newVal);          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableSA305LoadGroup& setAddressUsage(std::string  newVal);      //

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
#endif // #ifndef __TBL_LMG_SA305_H__

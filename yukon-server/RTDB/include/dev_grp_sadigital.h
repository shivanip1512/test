
/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sadigital
*
* Class:  CtiDeviceGroupSADigital
* Date:   4/21/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2006/02/24 00:19:13 $
* HISTORY      :
* $Log: dev_grp_sadigital.h,v $
* Revision 1.4  2006/02/24 00:19:13  tspar
* First Series of replacements of RWTPtrSlist to std::list. Scanner, Pil, Porter.
*
* Revision 1.3  2005/12/20 17:20:29  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.2  2005/09/02 16:19:47  cplender
* Modified the getPutConfigAssignment() method to allow modifier parameters.
*
* Revision 1.1  2004/04/29 20:24:40  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_SADIGITAL_H__
#define __DEV_GRP_SADIGITAL_H__

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sasimple.h"
#include "tbl_lmg_sasimple.h"

class IM_EX_DEVDB CtiDeviceGroupSADigital : public CtiDeviceGroupBase
{
protected:

    CtiTableSASimpleGroup   _loadGroup;

private:

public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupSADigital();
    CtiDeviceGroupSADigital(const CtiDeviceGroupSADigital& aRef);
    virtual ~CtiDeviceGroupSADigital();

    CtiDeviceGroupSADigital& operator=(const CtiDeviceGroupSADigital& aRef);

    CtiTableSASimpleGroup getLoadGroup() const;
    CtiTableSASimpleGroup& getLoadGroup();
    CtiDeviceGroupSADigital& setLoadGroup(const CtiTableSASimpleGroup& aRef);

    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList);

};
#endif // #ifndef __DEV_GRP_SADIGITAL_H__

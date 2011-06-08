/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_golay
*
* Class:  CtiDeviceGroupGolay
* Date:   4/21/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
* HISTORY      :
* $Log: dev_grp_golay.h,v $
* Revision 1.7  2008/10/28 19:21:43  mfisher
* YUK-6589 Scanner should not load non-scannable devices
* refreshList() now takes a list of paoids, which may be empty if it's a full reload
* Changed CtiDeviceBase, CtiPointBase, and CtiRouteBase::getSQL() (and all inheritors) to be const
* Removed a couple unused Porter functions
* Added logger unit test
* Simplified DebugTimer's variables
*
* Revision 1.6  2006/09/21 21:31:38  mfisher
* privatized Inherited typedef
*
* Revision 1.5  2006/02/27 23:58:32  tspar
* Phase two of RWTPtrSlist replacement.
*
* Revision 1.4  2006/02/24 00:19:13  tspar
* First Series of replacements of RWTPtrSlist to std::list. Scanner, Pil, Porter.
*
* Revision 1.3  2005/12/20 17:20:29  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.2  2005/09/02 16:19:47  cplender
* Modified the getPutConfigAssignment() method to allow modifier parameters.
*
* Revision 1.1  2004/04/29 20:23:50  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_GOLAY_H__
#define __DEV_GRP_GOLAY_H__

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sasimple.h"
#include "tbl_lmg_sasimple.h"

class IM_EX_DEVDB CtiDeviceGroupGolay : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTableSASimpleGroup   _loadGroup;

public:

    CtiDeviceGroupGolay();
    CtiDeviceGroupGolay(const CtiDeviceGroupGolay& aRef);
    virtual ~CtiDeviceGroupGolay();

    CtiDeviceGroupGolay& operator=(const CtiDeviceGroupGolay& aRef);

    CtiTableSASimpleGroup getLoadGroup() const;
    CtiTableSASimpleGroup& getLoadGroup();
    CtiDeviceGroupGolay& setLoadGroup(const CtiTableSASimpleGroup& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

};
#endif // #ifndef __DEV_GRP_GOLAY_H__

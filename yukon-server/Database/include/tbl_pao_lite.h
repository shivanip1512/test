/*-----------------------------------------------------------------------------*
*
* File:   tbl_pao_lite
*
* Date:   8/15/2008
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pao.cpp-arc  $
* REVISION     :  $Revision: 1.3.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 2008 Cooper Industries. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PAO_LITE_H__
#define __TBL_PAO_LITE_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <limits.h>

#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "row_reader.h"

//This is the lite version of CtiTblPAO. The only string stored by this object is the name.
class IM_EX_CTIYUKONDB CtiTblPAOLite : public CtiMemDBObject
{

protected:

    LONG           _paObjectID;
    INT            _class;
    std::string         _name;
    INT            _type;

    bool           _disableFlag;

public:

    typedef CtiMemDBObject Inherited;

    CtiTblPAOLite();

    CtiTblPAOLite(const CtiTblPAOLite& aRef);

    virtual ~CtiTblPAOLite();

    CtiTblPAOLite& operator=(const CtiTblPAOLite& aRef);

    LONG   getID()    const;
    INT    getClass() const;
    std::string getName()  const;
    INT    getType()  const;

    CtiTblPAOLite& setID( LONG paoid );
    CtiTblPAOLite& setType(const INT &type);

    bool isInhibited() const;

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual void DumpData();
};

inline bool CtiTblPAOLite::isInhibited() const { return _disableFlag; }

#endif // #ifndef __TBL_PAO_LITE_H__

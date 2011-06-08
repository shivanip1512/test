/*-----------------------------------------------------------------------------*
*
* File:   pt_dyn_dispatch
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_dyn_dispatch.cpp-arc  $
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2008/06/30 15:24:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "pt_dyn_dispatch.h"
#include "logger.h"

using boost::shared_ptr;

CtiDynamicPointDispatch::CtiDynamicPointDispatch(LONG id, double initialValue, INT qual) :
_conditionActive(0),
_inDelayedData(false),
_archivePending(FALSE),
_lastSignal(-1),
_dispatch(id,initialValue,qual)
{

}

CtiDynamicPointDispatch::CtiDynamicPointDispatch(const CtiDynamicPointDispatch& aRef) :
_conditionActive(0),
_inDelayedData(false),
_lastSignal(-1),
_archivePending(FALSE)
{
    *this = aRef;
}

CtiDynamicPointDispatch::~CtiDynamicPointDispatch() {}

CtiDynamicPointDispatch& CtiDynamicPointDispatch::operator=(const CtiDynamicPointDispatch& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _dispatch = aRef.getDispatch();
        _archivePending = aRef.getArchivePending();
    }
    return *this;
}


double CtiDynamicPointDispatch::getValue() const
{
    return getDispatch().getValue();
}

UINT CtiDynamicPointDispatch::getQuality() const
{
    return getDispatch().getQuality();
}

CtiTime CtiDynamicPointDispatch::getTimeStamp() const
{
    return getDispatch().getTimeStamp();
}

UINT CtiDynamicPointDispatch::getTimeStampMillis() const
{
    return getDispatch().getTimeStampMillis();
}

BOOL CtiDynamicPointDispatch::getArchivePending() const
{
    return _archivePending;
}

BOOL CtiDynamicPointDispatch::isArchivePending() const
{
    return _archivePending;
}

CtiDynamicPointDispatch& CtiDynamicPointDispatch::setArchivePending(BOOL b)
{
    _archivePending = b;
    return *this;
}

CtiDynamicPointDispatch& CtiDynamicPointDispatch::setPoint(const CtiTime &NewTime, UINT millis, double Val, int Qual, UINT tag_mask)
{
    {
        getDispatch().resetTags( MASK_RESETTABLE_TAGS );       // Clear out any value based tags..

        getDispatch().setValue(Val);
        getDispatch().setQuality(Qual);
        getDispatch().setTimeStamp(NewTime);
        getDispatch().setTimeStampMillis(millis);

        getDispatch().setTags( tag_mask );

        getDispatch().setDirty(TRUE);
    }

    return *this;
}

CtiTime CtiDynamicPointDispatch::getNextArchiveTime() const
{
    return getDispatch().getNextArchiveTime();
}

CtiDynamicPointDispatch& CtiDynamicPointDispatch::setNextArchiveTime(const CtiTime &aTime)
{
    getDispatch().setNextArchiveTime(CtiTime(aTime));
    return *this;
}


const CtiTablePointDispatch& CtiDynamicPointDispatch::getDispatch() const
{
    return _dispatch;
}
CtiTablePointDispatch& CtiDynamicPointDispatch::getDispatch()
{
    return _dispatch;
}


CtiDynamicPointBase*  CtiDynamicPointDispatch::replicate() const
{
    CtiDynamicPointBase *pNew = CTIDBG_new CtiDynamicPointDispatch(*this);

    return((CtiDynamicPointBase*)pNew);

}

bool CtiDynamicPointDispatch::inDelayedData() const
{
    return _inDelayedData;
}

CtiDynamicPointDispatch&  CtiDynamicPointDispatch::setInDelayedData(const bool in)
{
    _inDelayedData = in;
    return *this;
}

void CtiDynamicPointDispatch::setConditionActive(int alarm_condition, bool active)
{
    if(active)
    {
        _conditionActive |= (0x00000001 << alarm_condition);
    }
    else
    {
        _conditionActive &= ~(0x00000001 << alarm_condition);
    }

    return;
}
bool CtiDynamicPointDispatch::isConditionActive(int alarm_condition) const
{
    return _conditionActive & (0x00000001 << alarm_condition);
}


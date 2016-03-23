#include "precompiled.h"

#include "pt_dyn_dispatch.h"


CtiDynamicPointDispatch::CtiDynamicPointDispatch(LONG id, double initialValue, INT qual) :
_inDelayedData(false),
_archivePending(false),
_wasArchived(false),
_dispatch(id,initialValue,qual)
{
}


double CtiDynamicPointDispatch::getValue() const
{
    return getDispatch().getValue();
}

unsigned CtiDynamicPointDispatch::getQuality() const
{
    return getDispatch().getQuality();
}

CtiTime CtiDynamicPointDispatch::getTimeStamp() const
{
    return getDispatch().getTimeStamp();
}

unsigned CtiDynamicPointDispatch::getTimeStampMillis() const
{
    return getDispatch().getTimeStampMillis();
}

bool CtiDynamicPointDispatch::isArchivePending() const
{
    return _archivePending;
}

void CtiDynamicPointDispatch::setArchivePending(bool pending)
{
    _archivePending = pending;
}

bool CtiDynamicPointDispatch::wasArchived() const
{
    return _wasArchived;
}

void CtiDynamicPointDispatch::setWasArchived(bool archived)
{
    _wasArchived = archived;
}

void CtiDynamicPointDispatch::setPoint(const CtiTime &NewTime, unsigned millis, double Val, int Qual, unsigned tag_mask)
{
    _wasArchived = false;

    getDispatch().resetTags( MASK_RESETTABLE_TAGS );       // Clear out any value based tags..

    getDispatch().setValue(Val);
    getDispatch().setQuality(Qual);
    getDispatch().setTimeStamp(NewTime);
    getDispatch().setTimeStampMillis(millis);

    getDispatch().setTags( tag_mask );

    getDispatch().setDirty(true);
}

CtiTime CtiDynamicPointDispatch::getNextArchiveTime() const
{
    return getDispatch().getNextArchiveTime();
}

void CtiDynamicPointDispatch::setNextArchiveTime(const CtiTime &aTime)
{
    getDispatch().setNextArchiveTime(CtiTime(aTime));
}


const CtiTablePointDispatch& CtiDynamicPointDispatch::getDispatch() const
{
    return _dispatch;
}
CtiTablePointDispatch& CtiDynamicPointDispatch::getDispatch()
{
    return _dispatch;
}


bool CtiDynamicPointDispatch::inDelayedData() const
{
    return _inDelayedData;
}

void CtiDynamicPointDispatch::setInDelayedData(const bool delayed)
{
    _inDelayedData = delayed;
}

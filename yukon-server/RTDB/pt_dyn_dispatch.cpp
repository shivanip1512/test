
#include "pt_dyn_dispatch.h"
#include "logger.h"
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   pt_dyn_dispatch
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_dyn_dispatch.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/11/15 14:08:22 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


CtiDynamicPointDispatch::CtiDynamicPointDispatch(LONG id, double initialValue, INT qual) :
_archivePending(FALSE),
_lastSignal(-1),
_attachment(NULL),
_dispatch(id,initialValue,qual)
{

}

CtiDynamicPointDispatch::CtiDynamicPointDispatch(const CtiDynamicPointDispatch& aRef) :
_attachment(NULL),
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

        setLastSignal( aRef.getLastSignal() );
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

RWTime CtiDynamicPointDispatch::getTimeStamp() const
{
    return getDispatch().getTimeStamp().rwtime();
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

CtiDynamicPointDispatch& CtiDynamicPointDispatch::setPoint(const RWTime &NewTime, double Val, int Qual, UINT tag_mask)
{
    {
        LockGuard guard( monitor() );    // This is the point's "mux" from parent.

        getDispatch().resetTags( MASK_RESETTABLE_TAGS );       // Clear out any value based tags..

        getDispatch().setValue(Val);
        getDispatch().setQuality(Qual);
        getDispatch().setTimeStamp(NewTime);

        getDispatch().setTags( tag_mask );

        getDispatch().setDirty(TRUE);
    }

    return *this;
}

VOID* CtiDynamicPointDispatch::getAttachment()
{
    return _attachment;
}

void CtiDynamicPointDispatch::setAttachment(VOID *aptr)
{
    _attachment = aptr;
}


RWTime CtiDynamicPointDispatch::getNextArchiveTime() const
{
    return getDispatch().getNextArchiveTime().rwtime();
}

CtiDynamicPointDispatch& CtiDynamicPointDispatch::setNextArchiveTime(const RWTime &aTime)
{
    getDispatch().setNextArchiveTime(RWDBDateTime(aTime));
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

INT CtiDynamicPointDispatch::getLastSignal( ) const
{
    return _lastSignal;
}

CtiDynamicPointDispatch& CtiDynamicPointDispatch::setLastSignal( const INT &aInt )
{
    _lastSignal = aInt;
    return *this;
}

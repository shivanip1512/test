/*-----------------------------------------------------------------------------*
*
* File:   dev_grp
*
* Class:  CtiDeviceGroupBase
* Date:   2/2/2000
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_nloc.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2003/05/23 22:22:51 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_H__
#define __DEV_GRP_H__
#pragma warning( disable : 4786)


#define GRP_CONTROL_STATUS      1       // status point which can intiate control on the group.  Also indicates control status.

#include "msg_lmcontrolhistory.h"
#include "msg_pdata.h"
#include "pt_status.h"
#include "pt_analog.h"

class CtiDeviceGroupBase : public CtiDeviceBase
{
protected:

    INT _isShed;
    RWCString _lastCommand;

private:

public:

    typedef CtiDeviceBase Inherited;

    CtiDeviceGroupBase()  :
        _isShed(UNCONTROLLED)
    {}

    CtiDeviceGroupBase(const CtiDeviceGroupBase& aRef) :
        _isShed(UNCONTROLLED)
    {
        *this = aRef;
    }

    virtual ~CtiDeviceGroupBase() {}

    virtual LONG getRouteID() = 0;      // Must be defined!

    CtiDeviceGroupBase& operator=(const CtiDeviceGroupBase& aRef)
    {
        if(this != &aRef)
        {
            Inherited::operator=(aRef);
            _isShed = aRef._isShed;
        }
        return *this;
    }

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
    {
        Inherited::getSQL(db, keyTable, selector);
    }

    virtual void DecodeDatabaseReader(RWDBReader &rdr)
    {
        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    }

    void reportControlStart(int isshed, int shedtime, int reductionratio, RWTPtrSlist< CtiMessage >  &vgList, RWCString cmd = RWCString("") )
    {
        /*
         *  This is the CONTROL STATUS point (offset) for the group.
         */
        CtiPointStatus *pControlStatus = (CtiPointStatus*)getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );

        if(pControlStatus != 0)
        {
            CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( getID(), pControlStatus->getPointID(), isshed, RWTime(), (isshed == CONTROLLED ? shedtime : RESTORE_DURATION), reductionratio);

            hist->setControlType( cmd );      // Could be the state group name ????
            hist->setActiveRestore( shedtime > 0 ? LMAR_TIMED_RESTORE : LMAR_MANUAL_RESTORE);
            hist->setMessagePriority( hist->getMessagePriority() + 1 );
            vgList.insert( hist );

            if(pControlStatus->isPseudoPoint())
            {
                // There is no physical point to observe and respect.  We lie to the control point.
                CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pControlStatus->getPointID(), (DOUBLE)(isshed), NormalQuality, StatusPointType, (isshed == CONTROLLED ? RWCString(getName() + " controlling") : RWCString(getName() + " restoring")));
                pData->setMessagePriority( pData->getMessagePriority() + 1 );
                vgList.insert(pData);
            }

            if(isshed == CONTROLLED && shedtime > 0)
            {
                // Present the restore as a delayed update to dispatch.  Note that the order of opened and closed have reversed
                CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pControlStatus->getPointID(), (DOUBLE)UNCONTROLLED, NormalQuality, StatusPointType, RWCString(getName() + " restoring (delayed)"), TAG_POINT_DELAYED_UPDATE);
                pData->setTime( RWTime() + shedtime );
                pData->setMessagePriority( pData->getMessagePriority() - 1 );
                vgList.insert(pData);
            }
        }

        CtiPointAnalog *pAnalog = (CtiPointAnalog*)getDevicePointOffsetTypeEqual( CONTROLSTOPCOUNTDOWNOFFSET, AnalogPointType );
        if(pAnalog)
        {
            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pAnalog->getPointID(), pAnalog->computeValueForUOM((isshed == CONTROLLED ? (DOUBLE)(shedtime) : (DOUBLE)(0.0))) , NormalQuality, AnalogPointType, (isshed == CONTROLLED ? RWCString(getName() + " controlling") : RWCString(getName() + " restoring")));
            pData->setMessagePriority( pData->getMessagePriority() + 1 );
            vgList.insert(pData);
        }
    }

    RWCString getLastCommand() const
    {
        return _lastCommand;
    }

    CtiDeviceGroupBase& setShed( INT shed )
    {
        _isShed = shed;
        return *this;
    }

};
#endif // #ifndef __DEV_GRP_H__

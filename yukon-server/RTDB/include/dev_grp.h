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
* REVISION     :  $Revision: 1.19 $
* DATE         :  $Date: 2006/03/23 15:29:19 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_H__
#define __DEV_GRP_H__
#pragma warning( disable : 4786)


#define GRP_CONTROL_STATUS      1       // status point which can intiate control on the group.  Also indicates control status.

#include "cparms.h"
#include "msg_lmcontrolhistory.h"
#include "msg_pcrequest.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "pt_status.h"
#include "pt_analog.h"

#include "dev_base.h"

class CtiDeviceGroupBase : public CtiDeviceBase
{
protected:

    INT _isShed;
    string _lastCommand;

private:

    ULONG _lastCommandExpiration;

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

    void reportActionItemsToDispatch(CtiRequestMsg *pReq, CtiCommandParser &parse, list< CtiMessage* > &vgList)
    {
        CtiTime now;
        string prevLastAction = _lastCommand;    // Save a temp copy.
        _lastCommand = string();                 // Blank it!

        //
        // OK, these are the items we are about to set out to perform..  Any additional signals will
        // be added into the list upon completion of the Execute!
        //
        if(parse.getActionItems().entries())
        {
            bool reducelogs = !stringCompareIgnoreCase(gConfigParms.getValueAsString("REDUCE_CONTROL_REPORTS_TO_SYSTEM_LOG"),"true");

            for(size_t offset = 0 ; offset < parse.getActionItems().entries(); offset++)
            {
                string actn = parse.getActionItems()[offset];
                string desc = getDescription(parse);

                if(offset > 0) _lastCommand += " / ";
                _lastCommand += actn;

                // Check if this is a repeat of a previous control.  We should suppress repeats.
                if( !reducelogs || now.seconds() > _lastCommandExpiration || !prevLastAction.find(actn) )
                {
                    CtiPointSPtr pControlStatus = getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
                    LONG pid = ( (pControlStatus) ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );

                    vgList.push_back(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                }
            }
        }

        _lastCommandExpiration = now.seconds() + parse.getiValue("control_interval", 0);
    }

    void reportControlStart(int isshed, int shedtime, int reductionratio, list< CtiMessage* >  &vgList, string cmd = string("") )
    {
        /*
         *  This is the CONTROL STATUS point (offset) for the group.
         */
        CtiPointSPtr pControlStatus = getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
        CtiMultiMsg *pMulti = new CtiMultiMsg;

        if(pControlStatus)
        {
            CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( getID(), pControlStatus->getPointID(), isshed, CtiTime(), (isshed == CONTROLLED ? shedtime : RESTORE_DURATION), (isshed == CONTROLLED ? reductionratio : 0));

            hist->setControlType( cmd );      // Could be the state group name ????
            hist->setActiveRestore( shedtime > 0 ? LMAR_TIMED_RESTORE : LMAR_MANUAL_RESTORE);
            hist->setMessagePriority( hist->getMessagePriority() + 1 );
            // vgList.push_back( hist );
            pMulti->insert(hist);

            if(pControlStatus->isPseudoPoint())
            {
                // There is no physical point to observe and respect.  We lie to the control point.
                CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pControlStatus->getPointID(), (DOUBLE)(isshed), NormalQuality, StatusPointType, (isshed == CONTROLLED ? string(getName() + " controlling") : string(getName() + " restoring")));
                pData->setMessagePriority( pData->getMessagePriority() + 1 );
                //vgList.push_back(pData);
                pMulti->insert(pData);
            }

            if(isshed == CONTROLLED && shedtime > 0)
            {
                // Present the restore as a delayed update to dispatch.  Note that the order of opened and closed have reversed
                CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pControlStatus->getPointID(), (DOUBLE)UNCONTROLLED, NormalQuality, StatusPointType, string(getName() + " restoring (delayed)"), TAG_POINT_DELAYED_UPDATE);
                pData->setTime( CtiTime() + shedtime );
                pData->setMessagePriority( pData->getMessagePriority() - 1 );
                //vgList.push_back(pData);
                pMulti->insert(pData);
            }
        }

        CtiPointSPtr point = getDevicePointOffsetTypeEqual( CONTROLSTOPCOUNTDOWNOFFSET, AnalogPointType );
        CtiPointAnalogSPtr pAnalog = boost::static_pointer_cast<CtiPointAnalog>(point);
        if(pAnalog)
        {
            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pAnalog->getPointID(), pAnalog->computeValueForUOM((isshed == CONTROLLED ? (DOUBLE)(shedtime) : (DOUBLE)(0.0))) , NormalQuality, AnalogPointType, (isshed == CONTROLLED ? string(getName() + " controlling") : string(getName() + " restoring")));
            pData->setMessagePriority( pData->getMessagePriority() + 1 );
            //vgList.push_back(pData);
            pMulti->insert(pData);
        }

        vgList.push_back(pMulti);
    }

    string getLastCommand() const
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

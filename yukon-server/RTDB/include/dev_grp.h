#pragma once

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
using boost::weak_ptr;

static const std::string GROUP_CONTROL_EXPIRATION = "STANDARD_CONTROL_EXPIRATION";
class CtiDeviceGroupBase;
typedef shared_ptr< CtiDeviceGroupBase > CtiDeviceGroupBaseSPtr;
typedef weak_ptr< CtiDeviceGroupBase > CtiDeviceGroupBaseWPtr;

class CtiDeviceGroupBase : public CtiDeviceBase
{
    typedef CtiDeviceBase Inherited;

    ULONG _lastCommandExpiration;

protected:

    INT _isShed;
    std::string _lastCommand;

public:

    CtiDeviceGroupBase()  :
        _isShed(UNCONTROLLED)
    {}

    virtual LONG getRouteID() = 0;      // Must be defined!

    void DecodeDatabaseReader(Cti::RowReader &rdr) override
    {
        Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    }

    void reportActionItemsToDispatch(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList)
    {
        CtiTime now;
        std::string prevLastAction = _lastCommand;    // Save a temp copy.
        _lastCommand = std::string();                 // Blank it!

        //
        // OK, these are the items we are about to set out to perform..  Any additional signals will
        // be added into the list upon completion of the Execute!
        //
        if(parse.getActionItems().size())
        {
            int offset = 0;
            bool reducelogs = gConfigParms.isTrue("REDUCE_CONTROL_REPORTS_TO_SYSTEM_LOG");
            for(std::list< std::string >::const_iterator itr = parse.getActionItems().begin();
                 itr != parse.getActionItems().end();
                 ++itr )
            {
                std::string actn = *itr;
                std::string desc = getDescription(parse);

                if(offset > 0) _lastCommand += " / ";
                _lastCommand += actn;

                // Check if this is a repeat of a previous control.  We should suppress repeats.
                if( !reducelogs || now.seconds() > _lastCommandExpiration || !prevLastAction.find(actn) )
                {
                    CtiPointSPtr pControlStatus = getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
                    LONG pid = ( (pControlStatus) ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );

                    vgList.push_back(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                }
                ++offset;
            }
        }

        _lastCommandExpiration = now.seconds() + parse.getiValue("control_interval", 0);
    }

    virtual void reportControlStart(int isshed, int shedtime, int reductionratio, CtiMessageList  &vgList, std::string cmd = std::string(""), int controlPriority = 0 )
    {
        /*
         *  This is the CONTROL STATUS point (offset) for the group.
         */
        CtiPointSPtr pControlStatus = getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
        CtiMultiMsg *pMulti = new CtiMultiMsg;

        if(pControlStatus)
        {
            CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( getID(), pControlStatus->getPointID(), isshed, CtiTime(), (isshed == CONTROLLED ? shedtime : RESTORE_DURATION), (isshed == CONTROLLED ? reductionratio : 0), controlPriority);

            hist->setControlType( cmd );      // Could be the state group name ????
            hist->setActiveRestore( shedtime > 0 ? LMAR_TIMED_RESTORE : LMAR_MANUAL_RESTORE);
            hist->setMessagePriority( hist->getMessagePriority() + 1 );
            // vgList.push_back( hist );
            pMulti->insert(hist);
        }

        CtiPointSPtr point = getDevicePointOffsetTypeEqual( CONTROLSTOPCOUNTDOWNOFFSET, AnalogPointType );
        CtiPointAnalogSPtr pAnalog = boost::static_pointer_cast<CtiPointAnalog>(point);
        if(pAnalog)
        {
            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pAnalog->getPointID(), pAnalog->computeValueForUOM((isshed == CONTROLLED ? (DOUBLE)(shedtime) : (DOUBLE)(0.0))) , NormalQuality, AnalogPointType, (isshed == CONTROLLED ? (getName() + " controlling") : (getName() + " restoring")));
            pData->setMessagePriority( pData->getMessagePriority() + 1 );
            //vgList.push_back(pData);
            pMulti->insert(pData);
        }

        vgList.push_back(pMulti);
    }

    std::string getLastCommand() const
    {
        return _lastCommand;
    }

    CtiDeviceGroupBase& setShed( INT shed )
    {
        _isShed = shed;
        return *this;
    }

    // Takes in a command string and removes the data from it that can change (countdown, ect..)
    // Dispatch does a direct comparison of text so it needs to consider these commands to be identical
    std::string removeCommandDynamicText(std::string command)
    {
        std::string match = " count";
        int begin, end;
        if( (begin = command.find(match)) != std::string::npos )
        {
            //control xcom cycle 50 count 8 period 30 truecycle

            //We start at the " " after count and look for the number
            end = command.find_first_not_of(" ", begin + match.length());

            if( end != std::string::npos )
            {
                //Here we start at the number (8) and try to find a " "
                end = command.find_first_of(" ", end);
            }

            if( end != std::string::npos )
            {
                command.erase(begin, end - begin);
            }
        }

        match = " bump stage";
        if( (begin = command.find(match)) != std::string::npos )
        {
            //control xcom cycle 50 count 8 period 30 truecycle

            //We start at the " " after count and look for the number
            end = command.find_first_not_of(" ", begin + match.length());

            if( end != std::string::npos )
            {
                //Here we start at the number (8) and try to find a " "
                end = command.find_first_of(" ", end);
            }

            if( end != std::string::npos )
            {
                command.erase(begin, end - begin);
            }
        }

        return command;
    }

    enum ADDRESSING_COMPARE_RESULT
    {
        NO_RELATIONSHIP,  // Neither is a parent or child.
        THIS_IS_PARENT,   // *this is the parent
        OPERAND_IS_PARENT,   // The operand (otherGroup) is the parent
        ADDRESSING_EQUIVALENT, // The two groups have identical addrsesing
        NO_COMPARISON_POSSIBLE, //No comparison is possible for this group
    };

    // Returns the result of *this compared to *other. THIS_IS_PARENT means *this is the parent.
    virtual ADDRESSING_COMPARE_RESULT compareAddressing(CtiDeviceGroupBaseSPtr otherGroup)
    {
        return NO_COMPARISON_POSSIBLE;
    }

    virtual bool isAParent() { return false; }
    virtual void addChild(CtiDeviceGroupBaseSPtr child) {}
    virtual void removeChild(long child) {}
    virtual void clearChildren() {}

};

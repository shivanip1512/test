#pragma once

#include "MsgCapControlMessage.h"
#include <string>

class CtiCCEventLogMsg : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( CtiCCEventLogMsg )

    private:
        typedef CapControlMessage Inherited;

    public:

        virtual ~CtiCCEventLogMsg();

        CtiCCEventLogMsg(LONG logId, LONG pointId, LONG spAreaId, LONG areaId, LONG stationId, LONG subId, LONG feederId, LONG eventType, LONG seqId, LONG value,
                         std::string text, std::string userName, DOUBLE kvarBefore= 0, DOUBLE kvarAfter = 0, DOUBLE kvarChange = 0,
                         std::string ipAddress = std::string("(N/A)"), LONG actionId = -1, std::string stateInfo = std::string("(N/A)"),
                         DOUBLE aVar = 0, DOUBLE bVar = 0, DOUBLE cVar = 0,  int regulatorId = 0);

        CtiCCEventLogMsg(std::string text, int regulatorId = 0, LONG eventType = -1);

        CtiCCEventLogMsg (const CtiCCEventLogMsg& aRef);

        long getLogId() const;
        CtiTime getTimeStamp() const;
        long getPointId() const;
        long getSubId() const;
        long getStationId() const;
        long getAreaId() const;
        long getSpecialAreaId() const;
        long getFeederId() const;
        long getEventType() const;
        long getSeqId() const;
        long getValue() const;
        std::string getText() const;
        std::string getUserName() const;
        double getKvarBefore() const;
        double getKvarAfter() const;
        double getKvarChange() const;
        std::string getIpAddress() const;
        long getActionId() const;
        std::string getStateInfo() const;
        double getAVar() const;
        double getBVar() const;
        double getCVar() const;
        int getRegulatorId() const;

        void setLogId(long logId);
        void setActionId(long actionId);
        void setStateInfo(std::string stateInfo);

        void setAVar(double val);
        void setBVar(double val);
        void setCVar(double val);
        void setABCVar(double aVal, double bVal, double cVal);

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        virtual CtiMessage* replicateMessage() const;

        CtiCCEventLogMsg& operator=(const CtiCCEventLogMsg& right);
    private:
        //provided for polymorphic persitence only
        CtiCCEventLogMsg();

        long _logId;
        CtiTime _timeStamp;
        long _pointId;
        long _spAreaId;
        long _areaId;
        long _stationId;
        long _subId;
        long _feederId;
        long _eventType;
        long _seqId;
        long _value;
        std::string _text;
        std::string _userName;
        double _kvarBefore;
        double _kvarAfter;
        double _kvarChange;
        std::string _ipAddress;
        long _actionId;
        std::string _stateInfo;
        int _regulatorId;

        double _aVar;
        double _bVar;
        double _cVar;
};


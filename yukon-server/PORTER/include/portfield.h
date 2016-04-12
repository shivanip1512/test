#pragma once

#include "optional.h"
#include "macro_offset.h"

bool isTimedOut( const CtiTime &start_time, const unsigned int &duration_seconds);
bool deviceCanSurviveThisStatus(INT status);
BOOL isTAPTermPort(LONG PortNumber);
void RequeueReportError(YukonError_t status, OUTMESS *OutMessage, INMESS *InMessage=NULL);
INT PostCommQueuePeek(CtiPortSPtr Port, CtiDeviceSPtr &Device);
YukonError_t ResetCommsChannel(CtiPortSPtr &Port, CtiDeviceSPtr &Device);
YukonError_t CheckInhibitedState(CtiPortSPtr Port, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
YukonError_t ValidateDevice(CtiPortSPtr Port, CtiDeviceSPtr &Device, OUTMESS *&OutMessage);
INT VTUPrep(CtiPortSPtr Port, INMESS &InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
YukonError_t EstablishConnection(CtiPortSPtr Port, INMESS &InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
YukonError_t DevicePreprocessing(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
void processPreloads(CtiPortSPtr Port);
YukonError_t CommunicateDevice(const CtiPortSPtr &Port, INMESS &InMessage, OUTMESS *OutMessage, const CtiDeviceSPtr &Device);
YukonError_t NonWrapDecode(const INMESS &InMessage, CtiDeviceSPtr &Device);
YukonError_t CheckAndRetryMessage(YukonError_t CommResult, CtiPortSPtr Port, INMESS &InMessage, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
YukonError_t DoProcessInMessage(YukonError_t CommResult, CtiPortSPtr Port, INMESS &InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
Cti::Optional<repeater_info> findRepeaterInRouteByAddress(int routeId, const Cti::MacroOffset &macroOffset, const unsigned echo_address);
YukonError_t ReturnResultMessage(YukonError_t CommResult, INMESS &InMessage, OUTMESS *&OutMessage);

YukonError_t InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr aIEDDevice, std::list< CtiMessage* > &traceList);
YukonError_t TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr aIEDDevice, std::list< CtiMessage* > &traceList);
YukonError_t PerformRequestedCmd ( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, const INMESS *aInMessage, OUTMESS *aOutMessage, std::list< CtiMessage* > &traceList);
YukonError_t ReturnLoadProfileData ( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, const INMESS &aInMessage, OUTMESS *aOutMessage, std::list< CtiMessage* > &traceList);
YukonError_t LogonToDevice( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, INMESS &aInMessage, OUTMESS *aOutMessage, std::list< CtiMessage* > &traceList);

void ShuffleVTUMessage( CtiPortSPtr &Port, CtiDeviceSPtr &Device, CtiOutMessage *OutMessage );
INT GetPreferredProtocolWrap( const CtiPortSPtr &Port, const CtiDeviceSPtr &Device );
BOOL findExclusionFreeOutMessage(void *data, void* d);
bool ShuffleQueue( CtiPortSPtr shPort, OUTMESS *&OutMessage, CtiDeviceSPtr &device );
INT CheckIfOutMessageIsExpired(OUTMESS *&OutMessage);
YukonError_t ProcessExclusionLogic(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr Device);
INT ProcessPortPooling(CtiPortSPtr Port);
YukonError_t ResetChannel(CtiPortSPtr &Port, CtiDeviceSPtr &Device);
INT IdentifyDeviceFromOutMessage(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
YukonError_t GetWork(CtiPortSPtr Port, CtiOutMessage *&OutMessage, ULONG &QueEntries, bool timesyncPreference);

YukonError_t OutMessageRequeueOnExclusionFail(CtiPortSPtr &Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device, CtiTablePaoExclusion &exclusion);

CtiOutMessage *GetLGRippleGroupAreaBitMatch(CtiPortSPtr Port, CtiOutMessage *&OutMessage);
BOOL searchFuncForRippleOutMessage(void *firstOM, void* om);
bool processCommStatus(INT CommResult, LONG DeviceID, LONG TargetID, bool RetryGTZero, const CtiDeviceSPtr &Device);
void getNextExpirationTime(LONG timesPerDay, CtiTime &time);
UINT purgeExpiredQueueEntries(CtiPortSPtr port);


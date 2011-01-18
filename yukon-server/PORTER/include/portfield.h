#pragma once

#include "optional.h"

bool isTimedOut( const CtiTime &start_time, const unsigned int &duration_seconds);
bool deviceCanSurviveThisStatus(INT status);
BOOL isTAPTermPort(LONG PortNumber);
INT RequeueReportError(INT status, OUTMESS *OutMessage, INMESS *InMessage=NULL);
INT PostCommQueuePeek(CtiPortSPtr Port, CtiDeviceSPtr &Device);
INT ResetCommsChannel(CtiPortSPtr &Port, CtiDeviceSPtr &Device);
INT CheckInhibitedState(CtiPortSPtr Port, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT ValidateDevice(CtiPortSPtr Port, CtiDeviceSPtr &Device, OUTMESS *&OutMessage);
INT VTUPrep(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT EstablishConnection(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT DevicePreprocessing(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
void processPreloads(CtiPortSPtr Port);
INT CommunicateDevice(const CtiPortSPtr &Port, INMESS *InMessage, OUTMESS *OutMessage, const CtiDeviceSPtr &Device);
INT NonWrapDecode(INMESS *InMessage, CtiDeviceSPtr &Device);
INT CheckAndRetryMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
INT DoProcessInMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
Cti::Optional<ESTRUCT::repeater_info> findRepeaterInRouteByAddress(const int routeId, const int macroOffset, const unsigned echo_address);
INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);

INT InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr aIEDDevice, list< CtiMessage* > &traceList);
INT TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr aIEDDevice, list< CtiMessage* > &traceList);
INT PerformRequestedCmd ( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, INMESS *aInMessage, OUTMESS *aOutMessage, list< CtiMessage* > &traceList);
INT ReturnLoadProfileData ( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, INMESS *aInMessage, OUTMESS *aOutMessage, list< CtiMessage* > &traceList);
INT LogonToDevice( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, INMESS *aInMessage, OUTMESS *aOutMessage, list< CtiMessage* > &traceList);

void ShuffleVTUMessage( CtiPortSPtr &Port, CtiDeviceSPtr &Device, CtiOutMessage *OutMessage );
INT GetPreferredProtocolWrap( const CtiPortSPtr &Port, const CtiDeviceSPtr &Device );
BOOL findExclusionFreeOutMessage(void *data, void* d);
bool ShuffleQueue( CtiPortSPtr shPort, OUTMESS *&OutMessage, CtiDeviceSPtr &device );
INT CheckIfOutMessageIsExpired(OUTMESS *&OutMessage);
INT ProcessExclusionLogic(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr Device);
INT ProcessPortPooling(CtiPortSPtr Port);
INT ResetChannel(CtiPortSPtr &Port, CtiDeviceSPtr &Device);
INT IdentifyDeviceFromOutMessage(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
INT GetWork(CtiPortSPtr Port, CtiOutMessage *&OutMessage, ULONG &QueEntries, bool timesyncPreference);

INT OutMessageRequeueOnExclusionFail(CtiPortSPtr &Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device, CtiTablePaoExclusion &exclusion);

CtiOutMessage *GetLGRippleGroupAreaBitMatch(CtiPortSPtr Port, CtiOutMessage *&OutMessage);
BOOL searchFuncForRippleOutMessage(void *firstOM, void* om);
bool processCommStatus(INT CommResult, LONG DeviceID, LONG TargetID, bool RetryGTZero, const CtiDeviceSPtr &Device);
void getNextExpirationTime(LONG timesPerDay, CtiTime &time);
UINT purgeExpiredQueueEntries(CtiPortSPtr port);


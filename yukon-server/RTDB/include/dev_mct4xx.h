
/*-----------------------------------------------------------------------------*
*
* File:   dev_mct4xx
*
* Class:  CtiDeviceMCT4xx
* Date:   10/5/2005
*
* Author: Jess M. Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct4xx.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2006/07/18 15:21:53 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT4XX_H__
#define __DEV_MCT4XX_H__
#pragma warning( disable : 4786)
#include "yukon.h"

#include "dev_mct.h"
#include <vector>
#include "config_base.h"
#include "config_device.h"
#include "config_parts.h"


class IM_EX_DEVDB CtiDeviceMCT4xx : public CtiDeviceMCT
{
public:
    typedef vector<const char *> ConfigPartsList;

private:
    static ConfigPartsList initConfigParts();
    static const ConfigPartsList _config_parts;
    int executePutConfigSingle( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

protected:

    virtual int executePutConfigDst(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigVThreshold(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigAddressing(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigDisconnect(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigOptions(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigHoliday(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigUsage(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigLongLoadProfile(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigLoadProfileChannel(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigRelays(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigPrecannedTable(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);
    virtual int executePutConfigCentron(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList);

    static const char *PutConfigPart_all;
    static const char *PutConfigPart_tou;
    static const char *PutConfigPart_dst;
    static const char *PutConfigPart_vthreshold;
    static const char *PutConfigPart_demand_lp;
    static const char *PutConfigPart_options;
    static const char *PutConfigPart_addressing;
    static const char *PutConfigPart_disconnect;
    static const char *PutConfigPart_holiday;
    static const char *PutConfigPart_usage;
    static const char *PutConfigPart_llp;
    static const char *PutConfigPart_lpchannel;
    static const char *PutConfigPart_relays;
    static const char *PutConfigPart_precanned_table;
    static const char *PutConfigPart_centron;

public:
    typedef CtiDeviceMCT Inherited;

    CtiDeviceMCT4xx( );
    CtiDeviceMCT4xx( const CtiDeviceMCT4xx &aRef );

    virtual ~CtiDeviceMCT4xx( );

    CtiDeviceMCT4xx& operator=( const CtiDeviceMCT4xx &aRef );

    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodePutConfig(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    
    virtual ConfigPartsList getPartsList();
};

#endif // #ifndef __DEV_MCT4xx_H__

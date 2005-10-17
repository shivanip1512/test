
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/10/17 16:58:10 $
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

private:
    typedef vector<const char *> ConfigPartsList;

    static ConfigPartsList initConfigParts();

protected:
    virtual int executePutConfigDst(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,RWTPtrSlist< CtiMessage >&vgList,RWTPtrSlist< CtiMessage >&retList,RWTPtrSlist< OUTMESS >   &outList);
    virtual int executePutConfigVThreshold(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,RWTPtrSlist< CtiMessage >&vgList,RWTPtrSlist< CtiMessage >&retList,RWTPtrSlist< OUTMESS >   &outList);
    virtual int executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,RWTPtrSlist< CtiMessage >&vgList,RWTPtrSlist< CtiMessage >&retList,RWTPtrSlist< OUTMESS >   &outList);

    Cti::Config::CtiConfigDeviceSPtr _deviceConfig;

public:

    typedef CtiDeviceMCT Inherited;

    CtiDeviceMCT4xx( );
    CtiDeviceMCT4xx( const CtiDeviceMCT4xx &aRef );

    virtual ~CtiDeviceMCT4xx( );

    CtiDeviceMCT4xx& operator=( const CtiDeviceMCT4xx &aRef );

    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    virtual void setDeviceConfig(Cti::Config::CtiConfigDeviceSPtr config);

protected:

    static const char *PutConfigPart_all;
    static const char *PutConfigPart_tou;
    static const char *PutConfigPart_dst;
    static const char *PutConfigPart_vthreshold;
    static const char *PutConfigPart_demand_lp;
    static const char *PutConfigPart_configuration;
    static const char *PutConfigPart_addressing;

    static const ConfigPartsList _config_parts;
};

#endif // #ifndef __DEV_MCT4xx_H__

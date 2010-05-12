
/*-----------------------------------------------------------------------------*
*
* File:   dev_focus.h
*
* Class:
* Date:   4/08/2010
*
* Author: Julie Richter
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __DEV_FOCUS_H__
#define __DEV_FOCUS_H__


#include "dev_meter.h"
#include "dlldefs.h"
#include "dev_sentinel.h"

#include "prot_ansi_focus.h"
#include "prot_ansi_sentinel.h"
#include "dsm2.h"

#include "types.h"
#include "dllyukon.h"

class IM_EX_DEVDB CtiDeviceFocus : public CtiDeviceSentinel
{

public:

    typedef CtiDeviceSentinel Inherited;

   CtiDeviceFocus();
   virtual ~CtiDeviceFocus();
   virtual CtiProtocolANSI_focus& getANSIProtocol( void );

   int buildScannerTableRequest (BYTE *ptr, UINT flags);
   int buildCommanderTableRequest (BYTE *ptr, UINT flags);


private:
   CtiProtocolANSI_focus   _stdAnsiProtocol;

};


#endif // #ifndef __DEV_FOCUS_H__

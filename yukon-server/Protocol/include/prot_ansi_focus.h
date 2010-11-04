
/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_focus.h
*
* Class:
* Date:   8/27/2004
*
* Author: Julie Richter
*
*
*-----------------------------------------------------------------------------*/
#ifndef __PROT_ANSI_FOCUS_H__
#define __PROT_ANSI_FOCUS_H__
#pragma warning( disable : 4786)


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "prot_ansi.h"

using namespace Ansi;
class IM_EX_PROT  CtiProtocolANSI_focus: public CtiProtocolANSI
{
    typedef CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_focus();
        virtual ~CtiProtocolANSI_focus();

        virtual void setAnsiDeviceType();
        virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime);

   private:


};


#endif // #ifndef __PROT_ANSI_FOCUS_H__



#pragma warning( disable : 4786)
/*---------------------------------------------------------------------------------*
*
* File:   ansi_kv2_mtable_onehundredten.cpp
*
* Class:
* Date:   2/20/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_kv2_mtable_onehundredten.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/01/25 18:33:51 $
*    History: 
      $Log: ansi_kv2_mtable_onehundredten.cpp,v $
      Revision 1.1  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.2  2004/09/30 21:37:16  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2

*----------------------------------------------------------------------------------*/

#include "logger.h"
#include "ansi_kv2_mtable_onehundredten.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableOnehundredten::CtiAnsiKV2ManufacturerTableOnehundredten( BYTE *dataBlob )
{
    memcpy( (void *)&_presentRegTbl, dataBlob, sizeof( unsigned char ) * 166);
    dataBlob +=  (sizeof( unsigned char ) * 166);
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTableOnehundredten::~CtiAnsiKV2ManufacturerTableOnehundredten()
{
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiKV2ManufacturerTableOnehundredten::printResult(  )
{
    UINT32 *value32;
    UINT16 *value16;
    UINT8  *value8;
    int i;
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  kV2 MFG Table 110  ========================" << endl;
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "     PRESENT REGISTER DATA TABLE " << endl;
        dout << "        Prev Intvl Demand:  ";
    }
    value32 =  getPreviousIntvlDemands();
    for (i = 0; i < 5; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "        Momentary Interval Data -  "<<endl;
        dout << "                   Demand:  ";
    }
    value32 =  getDemands();
    for (i = 0; i < 5; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "       KW Demand Fund+Harm:  ";
    }
    value32 =  getKWDmdFundPlus();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "       KW Demand Fund Only:  ";
    }
    value32 =  getKWDmdFundOnly();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "     KVAR Demand Fund+Harm:  ";
    }
    value32 =  getKVARDmdFundPlus();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "     KVAR Demand Fund Only:  ";
    }
    value32 =  getKVARDmdFundOnly();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "     Distortion KVA Demand:  ";
    }
    value32 =  getDistortionKVADmd();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "       Apparent KVA Demand:  ";
    }
    value32 =  getApparentKVADmd();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<value32[i];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "     Voltage l-n Fund+Harm:  ";
    }
    value16 =  getVlnFundPlus();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value16[i]/(float)10);       //raw volt quantities need to be scaled by factor of 1/10
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "     Voltage l-n Fund Only:  ";
    }
    value16 =  getVlnFundOnly();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value16[i]/(float)10);         //raw volt quantities need to be scaled by factor of 1/10
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "     Voltage l-l Fund+Harm:  ";
    }
    value16 =  getVllFundPlus();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value16[i]/(float)10);        //raw volt quantities need to be scaled by factor of 1/10
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "     Voltage l-l Fund Only:  ";
    }
    value16 =  getVllFundOnly();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value16[i]/(float)10);        //raw volt quantities need to be scaled by factor of 1/10
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "         Current Fund+Harm:  ";
    }
    value16 =  getCurrFundPlus();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value16[i])/(float)10;        //raw current quantities need to be scaled by factor of 1/10
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "         Current Fund Only:  ";
    }
    value16 =  getCurrFundOnly();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value16[i]/(float)10);       //raw current quantities need to be scaled by factor of 1/10
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "   Imputed Neutral Current:    "<<(getImputedNeutralCurr()/(float)10)<<endl;   //raw current quantities need to be scaled by factor of 1/10
        dout << "              Power Factor:    "<<(getPowerFactor()/(float)100)<<endl;    //raw pf quantities need to be scaled by factor of 1/100
        dout << "                 Frequency:    "<<(getFrequency()/(float)100)<<endl;  //raw freq. quantities need to be scaled by factor of 1/100
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "         Total Demand Distortion:  ";
    }
    value8 =  getTDD();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value8[i]/(float)100);         //raw distortion quantities need to be scaled by factor of 1/100
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "    Current Total Harm Distortion:  ";
    }
    value8 =  getITHD();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value8[i]/(float)100);      //raw distortion quantities need to be scaled by factor of 1/100
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "    Voltage Total Harm Distortion:  ";
    }
    value8 =  getVTHD();
    for (i = 0; i < 3; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value8[i]/(float)100);      //raw distortion quantities need to be scaled by factor of 1/100
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
        dout << "          Distortion Power Factor:  ";
    }
    value8 =  getDistortionPF();
    for (i = 0; i < 4; i++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(value8[i] /(float) 100);      //raw distortion quantities need to be scaled by factor of 1/100
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl;
    }
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getPreviousIntvlDemands()
{
    return (UINT32 *)_presentRegTbl.previousIntvlDemand;
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getDemands()
{
    return (UINT32 *)_presentRegTbl.demands;
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getKWDmdFundPlus()
{
    return (UINT32 *)_presentRegTbl.kwDmdFundPlus;
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getKWDmdFundOnly()
{
    return (UINT32 *)_presentRegTbl.kwDmdFundOnly;
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getKVARDmdFundPlus()
{
    return (UINT32 *)_presentRegTbl.kvarDmdFundPlus;
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getKVARDmdFundOnly()
{
    return (UINT32 *)_presentRegTbl.kvarDmdFundOnly;
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getDistortionKVADmd()
{
    return (UINT32 *)_presentRegTbl.distortionKVADmd;
}

UINT32* CtiAnsiKV2ManufacturerTableOnehundredten::getApparentKVADmd()
{
    return (UINT32 *)_presentRegTbl.apparentKVADmd;
}

UINT16* CtiAnsiKV2ManufacturerTableOnehundredten::getVlnFundPlus()
{
    return (UINT16 *)_presentRegTbl.vlnFundPlus;
}

UINT16* CtiAnsiKV2ManufacturerTableOnehundredten::getVlnFundOnly()
{
    return (UINT16 *)_presentRegTbl.vlnFundOnly;
}

UINT16* CtiAnsiKV2ManufacturerTableOnehundredten::getVllFundPlus()
{
    return (UINT16 *)_presentRegTbl.vllFundPlus;
}

UINT16* CtiAnsiKV2ManufacturerTableOnehundredten::getVllFundOnly()
{
    return (UINT16 *)_presentRegTbl.vllFundOnly;
}

UINT16* CtiAnsiKV2ManufacturerTableOnehundredten::getCurrFundPlus()
{
    return (UINT16 *)_presentRegTbl.currFundPlus;
}

UINT16* CtiAnsiKV2ManufacturerTableOnehundredten::getCurrFundOnly()
{
    return (UINT16 *)_presentRegTbl.currFundOnly;
}

UINT16 CtiAnsiKV2ManufacturerTableOnehundredten::getImputedNeutralCurr()
{
    return _presentRegTbl.imputedNeutralCurr;
}

UINT8 CtiAnsiKV2ManufacturerTableOnehundredten::getPowerFactor()
{
    return _presentRegTbl.powerFactor;
}

UINT16 CtiAnsiKV2ManufacturerTableOnehundredten::getFrequency()
{
    return _presentRegTbl.frequency;
}

UINT8* CtiAnsiKV2ManufacturerTableOnehundredten::getTDD()
{
    return (UINT8*)_presentRegTbl.tdd;
}

UINT8* CtiAnsiKV2ManufacturerTableOnehundredten::getITHD()
{
    return (UINT8*)_presentRegTbl.ithd;
}

UINT8* CtiAnsiKV2ManufacturerTableOnehundredten::getVTHD()
{
    return (UINT8*)_presentRegTbl.vthd;
}

UINT8* CtiAnsiKV2ManufacturerTableOnehundredten::getDistortionPF()
{
    return (UINT8*)_presentRegTbl.distortionPF;
}


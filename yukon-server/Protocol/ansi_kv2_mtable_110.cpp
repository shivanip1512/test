/*---------------------------------------------------------------------------------*
*
* File:   ansi_kv2_mtable_110.cpp
*
* Class:
* Date:   2/20/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/ansi_kv2_mtable_110.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2008/10/21 16:30:30 $
*    History:
      $Log: ansi_kv2_mtable_onehundredten.cpp,v $
      Revision 1.3  2008/10/21 16:30:30  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.2  2005/02/10 23:23:56  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.1  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.2  2004/09/30 21:37:16  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:54:54  dsutton
      Ansi protocol tables specific to the implementation of the KV2

*----------------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "ansi_kv2_mtable_110.h"

using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable110::CtiAnsiKV2ManufacturerTable110( BYTE *dataBlob )
{
    memcpy( (void *)&_presentRegTbl, dataBlob, sizeof( unsigned char ) * 166);
    dataBlob +=  (sizeof( unsigned char ) * 166);
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiKV2ManufacturerTable110::~CtiAnsiKV2ManufacturerTable110()
{
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiKV2ManufacturerTable110::printResult(  )
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

UINT32* CtiAnsiKV2ManufacturerTable110::getPreviousIntvlDemands()
{
    return (UINT32 *)_presentRegTbl.previousIntvlDemand;
}

UINT32* CtiAnsiKV2ManufacturerTable110::getDemands()
{
    return (UINT32 *)_presentRegTbl.demands;
}

UINT32* CtiAnsiKV2ManufacturerTable110::getKWDmdFundPlus()
{
    return (UINT32 *)_presentRegTbl.kwDmdFundPlus;
}

UINT32* CtiAnsiKV2ManufacturerTable110::getKWDmdFundOnly()
{
    return (UINT32 *)_presentRegTbl.kwDmdFundOnly;
}

UINT32* CtiAnsiKV2ManufacturerTable110::getKVARDmdFundPlus()
{
    return (UINT32 *)_presentRegTbl.kvarDmdFundPlus;
}

UINT32* CtiAnsiKV2ManufacturerTable110::getKVARDmdFundOnly()
{
    return (UINT32 *)_presentRegTbl.kvarDmdFundOnly;
}

UINT32* CtiAnsiKV2ManufacturerTable110::getDistortionKVADmd()
{
    return (UINT32 *)_presentRegTbl.distortionKVADmd;
}

UINT32* CtiAnsiKV2ManufacturerTable110::getApparentKVADmd()
{
    return (UINT32 *)_presentRegTbl.apparentKVADmd;
}

UINT16* CtiAnsiKV2ManufacturerTable110::getVlnFundPlus()
{
    return (UINT16 *)_presentRegTbl.vlnFundPlus;
}

UINT16* CtiAnsiKV2ManufacturerTable110::getVlnFundOnly()
{
    return (UINT16 *)_presentRegTbl.vlnFundOnly;
}

UINT16* CtiAnsiKV2ManufacturerTable110::getVllFundPlus()
{
    return (UINT16 *)_presentRegTbl.vllFundPlus;
}

UINT16* CtiAnsiKV2ManufacturerTable110::getVllFundOnly()
{
    return (UINT16 *)_presentRegTbl.vllFundOnly;
}

UINT16* CtiAnsiKV2ManufacturerTable110::getCurrFundPlus()
{
    return (UINT16 *)_presentRegTbl.currFundPlus;
}

UINT16* CtiAnsiKV2ManufacturerTable110::getCurrFundOnly()
{
    return (UINT16 *)_presentRegTbl.currFundOnly;
}

UINT16 CtiAnsiKV2ManufacturerTable110::getImputedNeutralCurr()
{
    return _presentRegTbl.imputedNeutralCurr;
}

UINT8 CtiAnsiKV2ManufacturerTable110::getPowerFactor()
{
    return _presentRegTbl.powerFactor;
}

UINT16 CtiAnsiKV2ManufacturerTable110::getFrequency()
{
    return _presentRegTbl.frequency;
}

UINT8* CtiAnsiKV2ManufacturerTable110::getTDD()
{
    return (UINT8*)_presentRegTbl.tdd;
}

UINT8* CtiAnsiKV2ManufacturerTable110::getITHD()
{
    return (UINT8*)_presentRegTbl.ithd;
}

UINT8* CtiAnsiKV2ManufacturerTable110::getVTHD()
{
    return (UINT8*)_presentRegTbl.vthd;
}

UINT8* CtiAnsiKV2ManufacturerTable110::getDistortionPF()
{
    return (UINT8*)_presentRegTbl.distortionPF;
}


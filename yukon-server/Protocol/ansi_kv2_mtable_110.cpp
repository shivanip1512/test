#include "precompiled.h"

#include <boost/lexical_cast.hpp>
#include "logger.h"
#include "ansi_kv2_mtable_110.h"

using std::endl;

namespace {

/**
 * convert array to string
 */
template <typename T>
std::string arrToStr(const T* const arr, unsigned size)
{
    if( ! size )
    {
        return "";
    }

    std::string str = boost::lexical_cast<std::string>(arr[0]);
    for (unsigned index = 1; index < size; index++)
    {
        str += "  " + boost::lexical_cast<std::string>(arr[index]);
    }

    return str;
}

/**
 * convert array to string and divide each item
 */
template <typename T, typename DividerType>
std::string arrToStr(const T* const arr, unsigned size, DividerType div)
{
    if( ! size )
    {
        return "";
    }

    std::string str = boost::lexical_cast<std::string>(arr[0]/div);
    for (unsigned index = 1; index < size; index++)
    {
        str += "  " + boost::lexical_cast<std::string>(arr[index]/div);
    }

    return str;
}

} // namespace anonymous


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
    Cti::FormattedList itemList;

    itemList.add("Prev Intvl Demand")             << arrToStr(getPreviousIntvlDemands(), 5);

    itemList <<"Momentary Interval Data";
    itemList.add("Demand")                        << arrToStr(getDemands(),          5);
    itemList.add("KW Demand Fund+Harm")           << arrToStr(getKWDmdFundPlus(),    3);
    itemList.add("KW Demand Fund Only")           << arrToStr(getKWDmdFundOnly(),    3);
    itemList.add("KVAR Demand Fund+Harm")         << arrToStr(getKVARDmdFundPlus(),  3);
    itemList.add("KVAR Demand Fund Only")         << arrToStr(getKVARDmdFundOnly(),  3);
    itemList.add("Distortion KVA Demand")         << arrToStr(getDistortionKVADmd(), 3);
    itemList.add("Apparent KVA Demand")           << arrToStr(getApparentKVADmd(),   3);
    itemList.add("Voltage l-n Fund+Harm")         << arrToStr(getVlnFundPlus(),      3, (float)10);  //raw volt quantities need to be scaled by factor of 1/10
    itemList.add("Voltage l-n Fund Only")         << arrToStr(getVlnFundOnly(),      3, (float)10);  //raw volt quantities need to be scaled by factor of 1/10
    itemList.add("Voltage l-l Fund+Harm")         << arrToStr(getVllFundPlus(),      3, (float)10);  //raw volt quantities need to be scaled by factor of 1/10
    itemList.add("Voltage l-l Fund Only")         << arrToStr(getVllFundOnly(),      3, (float)10);  //raw volt quantities need to be scaled by factor of 1/10
    itemList.add("Current Fund+Harm")             << arrToStr(getCurrFundPlus(),     3, (float)10);  //raw current quantities need to be scaled by factor of 1/10
    itemList.add("Current Fund Only")             << arrToStr(getCurrFundOnly(),     3, (float)10);  //raw current quantities need to be scaled by factor of 1/10
    itemList.add("Imputed Neutral Current")       << (getImputedNeutralCurr()         / (float)10);  //raw current quantities need to be scaled by factor of 1/10
    itemList.add("Power Factor")                  << (getPowerFactor()                / (float)100); //raw pf quantities need to be scaled by factor of 1/100
    itemList.add("Frequency")                     << (getFrequency()                  / (float)100); //raw freq. quantities need to be scaled by factor of 1/100
    itemList.add("Total Demand Distortion")       << arrToStr(getTDD(),              3, (float)100); //raw distortion quantities need to be scaled by factor of 1/100
    itemList.add("Current Total Harm Distortion") << arrToStr(getITHD(),             3, (float)100); //raw distortion quantities need to be scaled by factor of 1/100
    itemList.add("Voltage Total Harm Distortion") << arrToStr(getVTHD(),             3, (float)100); //raw distortion quantities need to be scaled by factor of 1/100
    itemList.add("Distortion Power Factor")       << arrToStr(getDistortionPF(),     4, (float)100); //raw distortion quantities need to be scaled by factor of 1/100

    CTILOG_INFO(dout,
            endl << formatTableName("kV2 MFG Table 110") <<
            endl <<"PRESENT REGISTER DATA TABLE"<<
            itemList
            );
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


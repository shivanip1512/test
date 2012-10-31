#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_dyn_paoinfo.h"
#include "database_reader.h"
#include "database_writer.h"

#include <boost/assign.hpp>
#include <boost/optional.hpp>

using namespace std;


const string EmptyString = "(empty)";

//  !!!  Any changes to these strings will require a DB update - this is what the DB keys on  !!!
const CtiTableDynamicPaoInfo::owner_map_t CtiTableDynamicPaoInfo::_owner_map =
    boost::assign::map_list_of
        (Application_Dispatch, "dispatch")
        (Application_Porter,   "porter"  )
        (Application_Scanner,  "scanner" );

//  !!!  Any changes to these strings will require a DB update - this is what the DB keys on  !!!
const CtiTableDynamicPaoInfo::key_map_t   CtiTableDynamicPaoInfo::_key_map =
    boost::assign::map_list_of
        (Key_MCT_SSpec,
            "mct sspec")
        (Key_MCT_SSpecRevision,
            "mct sspec revision")
        (Key_MCT_LoadProfileConfig,
            "mct load profile config")
        (Key_MCT_LoadProfileInterval,
            "mct load profile interval")
        (Key_MCT_LoadProfileInterval2,
            "mct load profile interval 2")
        (Key_MCT_IEDLoadProfileInterval,
            "mct ied load profile rate")

        (Key_FreezeCounter,
            "freeze counter")
        (Key_FreezeExpected,
            "expected freeze")
        (Key_VerificationSequence,
            "verification sequence")

        (Key_MCT_TimeAdjustTolerance,
            "mct time adjust tolerance")
        (Key_MCT_DSTStartTime,
            "mct dst start time")
        (Key_MCT_DSTEndTime,
            "mct dst end time")
        (Key_MCT_TimeZoneOffset,
            "mct time zone offset")
        (Key_MCT_OverVoltageThreshold,
            "mct over voltage threshold")
        (Key_MCT_UnderVoltageThreshold,
            "mct under voltage threshold")
        (Key_MCT_DemandInterval,
            "mct demand interval")
        (Key_MCT_VoltageLPInterval,
            "mct voltage profile interval")
        (Key_MCT_VoltageDemandInterval,
            "mct voltage demand interval")
        (Key_MCT_ScheduledFreezeDay,
            "mct scheduled freeze day")
        (Key_MCT_ScheduledFreezeConfigTimestamp,
            "mct scheduled freeze config timestamp")
        (Key_MCT_DayTable,
            "mct day table")
        (Key_MCT_DaySchedule1,
            "mct day schedule 1")
        (Key_MCT_DaySchedule2,
            "mct day schedule 2")
        (Key_MCT_DaySchedule3,
            "mct day schedule 3")
        (Key_MCT_DaySchedule4,
            "mct day schedule 4")
        (Key_MCT_DefaultTOURate,
            "mct default tou rate")
        (Key_MCT_AddressBronze,
            "mct bronze address")
        (Key_MCT_AddressLead,
            "mct lead address")
        (Key_MCT_AddressCollection,
            "mct collection address")
        (Key_MCT_AddressServiceProviderID,
            "mct service provider id")
        (Key_MCT_Configuration,
            "mct configuration")
        (Key_MCT_Options,
            "mct options")
        (Key_MCT_EventFlagsMask1,
            "mct event flags mask 1")
        (Key_MCT_EventFlagsMask2,
            "mct event flags mask 2")
        (Key_MCT_MeterAlarmMask,
            "mct meter alarm mask")
        (Key_MCT_OutageCycles,
            "mct outage cycles")
        (Key_MCT_DemandThreshold,
            "mct demand limit")
        (Key_MCT_ConnectDelay,
            "mct connect delay")
        (Key_MCT_DisconnectMinutes,
            "mct disconnect minutes")
        (Key_MCT_ConnectMinutes,
            "mct connect minutes")
        (Key_MCT_Holiday1,
            "mct holiday 1")
        (Key_MCT_Holiday2,
            "mct holiday 2")
        (Key_MCT_Holiday3,
            "mct holiday 3")
        (Key_MCT_LLPChannel1Len,
            "mct llp channel 1 length")
        (Key_MCT_LLPChannel2Len,
            "mct llp channel 2 length")
        (Key_MCT_LLPChannel3Len,
            "mct llp channel 3 length")
        (Key_MCT_LLPChannel4Len,
            "mct llp channel 4 length")

        (Key_MCT_LoadProfileChannelConfig1,
            "mct load profile channel 1 config")
        (Key_MCT_LoadProfileChannelConfig2,
            "mct load profile channel 2 config")
        (Key_MCT_LoadProfileChannelConfig3,
            "mct load profile channel 3 config")
        (Key_MCT_LoadProfileChannelConfig4,
            "mct load profile channel 4 config")
        (Key_MCT_LoadProfileMeterRatio1,
            "mct load profile meter ratio 1")
        (Key_MCT_LoadProfileMeterRatio2,
            "mct load profile meter ratio 2")
        (Key_MCT_LoadProfileMeterRatio3,
            "mct load profile meter ratio 3")
        (Key_MCT_LoadProfileMeterRatio4,
            "mct load profile meter ratio 4")
        (Key_MCT_LoadProfileKRatio1,
            "mct load profile k ratio 1")
        (Key_MCT_LoadProfileKRatio2,
            "mct load profile k ratio 2")
        (Key_MCT_LoadProfileKRatio3,
            "mct load profile k ratio 3")
        (Key_MCT_LoadProfileKRatio4,
            "mct load profile k ratio 4")

        (Key_MCT_RelayATimer,
            "mct relay a timer")
        (Key_MCT_RelayBTimer,
            "mct relay b timer")

        (Key_MCT_DisplayParameters,
            "mct display parameters")
        (Key_MCT_TransformerRatio,
            "mct transformer ratio")

        (Key_MCT_PrecannedTableReadInterval,
            "mct precanned table read interval")
        (Key_MCT_PrecannedMeterNumber,
            "mct precanned meter number")
        (Key_MCT_PrecannedTableType,
            "mct precanned table type")

        (Key_MCT_LLPInterest_Time,
            "mct llp interest time")
        (Key_MCT_LLPInterest_Channel,
            "mct llp interest channel")
        (Key_MCT_LLPInterest_RequestBegin,
            "mct llp interest request begin")
        (Key_MCT_LLPInterest_RequestEnd,
            "mct llp interest request end")

        (Key_MCT_LoadProfilePeakReportTimestamp,
            "mct load profile peak report timestamp")

        (Key_MCT_DailyReadInterestChannel,
            "mct daily read interest channel")

        (Key_MCT_DNP_AccumulatorCRC1,
            "mct dnp accumulator crc1")
        (Key_MCT_DNP_AccumulatorCRC2,
            "mct dnp accumulator crc2")
        (Key_MCT_DNP_AccumulatorCRC3,
            "mct dnp accumulator crc3")
        (Key_MCT_DNP_AccumulatorCRC4,
            "mct dnp accumulator crc4")
        (Key_MCT_DNP_AnalogCRC1,
            "mct dnp analog crc1")
        (Key_MCT_DNP_AnalogCRC2,
            "mct dnp analog crc2")
        (Key_MCT_DNP_AnalogCRC3,
            "mct dnp analog crc3")
        (Key_MCT_DNP_AnalogCRC4,
            "mct dnp analog crc4")
        (Key_MCT_DNP_AnalogCRC5,
            "mct dnp analog crc5")
        (Key_MCT_DNP_RealTime1CRC,
            "mct dnp realtime1 crc")
        (Key_MCT_DNP_RealTime2CRC,
            "mct dnp realtime2 crc")
        (Key_MCT_DNP_BinaryCRC,
            "mct dnp binary crc")

        (Key_MCT_LcdMetric01,
            "mct lcd metric 01")
        (Key_MCT_LcdMetric02,
            "mct lcd metric 02")
        (Key_MCT_LcdMetric03,
            "mct lcd metric 03")
        (Key_MCT_LcdMetric04,
            "mct lcd metric 04")
        (Key_MCT_LcdMetric05,
            "mct lcd metric 05")
        (Key_MCT_LcdMetric06,
            "mct lcd metric 06")
        (Key_MCT_LcdMetric07,
            "mct lcd metric 07")
        (Key_MCT_LcdMetric08,
            "mct lcd metric 08")
        (Key_MCT_LcdMetric09,
            "mct lcd metric 09")
        (Key_MCT_LcdMetric10,
            "mct lcd metric 10")
        (Key_MCT_LcdMetric11,
            "mct lcd metric 11")
        (Key_MCT_LcdMetric12,
            "mct lcd metric 12")
        (Key_MCT_LcdMetric13,
            "mct lcd metric 13")
        (Key_MCT_LcdMetric14,
            "mct lcd metric 14")
        (Key_MCT_LcdMetric15,
            "mct lcd metric 15")
        (Key_MCT_LcdMetric16,
            "mct lcd metric 16")
        (Key_MCT_LcdMetric17,
            "mct lcd metric 17")
        (Key_MCT_LcdMetric18,
            "mct lcd metric 18")
        (Key_MCT_LcdMetric19,
            "mct lcd metric 19")
        (Key_MCT_LcdMetric20,
            "mct lcd metric 20")
        (Key_MCT_LcdMetric21,
            "mct lcd metric 21")
        (Key_MCT_LcdMetric22,
            "mct lcd metric 22")
        (Key_MCT_LcdMetric23,
            "mct lcd metric 23")
        (Key_MCT_LcdMetric24,
            "mct lcd metric 24")
        (Key_MCT_LcdMetric25,
            "mct lcd metric 25")
        (Key_MCT_LcdMetric26,
            "mct lcd metric 26")

        (Key_MCT_WaterMeterReadInterval,
            "mct water meter read interval")

        (Key_FrozenRateAPeakTimestamp,
            "frozen rate a peak timestamp")
        (Key_FrozenRateBPeakTimestamp,
            "frozen rate b peak timestamp")
        (Key_FrozenRateCPeakTimestamp,
            "frozen rate c peak timestamp")
        (Key_FrozenRateDPeakTimestamp,
            "frozen rate d peak timestamp")
        (Key_FrozenDemandPeakTimestamp,
            "frozen demand peak timestamp")
        (Key_FrozenDemand2PeakTimestamp,
            "frozen channel 2 demand peak timestamp")
        (Key_FrozenDemand3PeakTimestamp,
            "frozen channel 3 demand peak timestamp")
        (Key_DemandFreezeTimestamp,
            "demand freeze timestamp")
        (Key_VoltageFreezeTimestamp,
            "voltage freeze timestamp")

        (Key_UDP_IP,
            "udp ip")
        (Key_UDP_Port,
            "udp port")
        (Key_UDP_Sequence,
            "udp sequence")

        (Key_LCR_SSpec,
            "lcr sspec")
        (Key_LCR_SSpecRevision,
            "lcr sspec revision")
        (Key_LCR_SerialAddress,
            "lcr serial address")
        (Key_LCR_Spid,
            "lcr ssid")
        (Key_LCR_GeoAddress,
            "lcr geo address")
        (Key_LCR_Substation,
            "lcr substation")
        (Key_LCR_Feeder,
            "lcr feeder")
        (Key_LCR_ZipCode,
            "lcr zip code")
        (Key_LCR_Uda,
            "lcr uda")
        (Key_LCR_ProgramAddressRelay1,
            "lcr relay 1 program address")
        (Key_LCR_ProgramAddressRelay2,
            "lcr relay 2 program address")
        (Key_LCR_ProgramAddressRelay3,
            "lcr relay 3 program address")
        (Key_LCR_ProgramAddressRelay4,
            "lcr relay 4 program address")
        (Key_LCR_SplinterAddressRelay1,
            "lcr relay 1 splinter address")
        (Key_LCR_SplinterAddressRelay2,
            "lcr relay 2 splinter address")
        (Key_LCR_SplinterAddressRelay3,
            "lcr relay 3 splinter address")
        (Key_LCR_SplinterAddressRelay4,
            "lcr relay 4 splinter address")

        (Key_RPT_SSpec,
            "rpt sspec")
        (Key_RPT_SSpecRevision,
            "rpt sspec revision")

        (Key_MCT_Holiday4,
            "mct holiday 4")
        (Key_MCT_Holiday5,
            "mct holiday 5")
        (Key_MCT_Holiday6,
            "mct holiday 6")
        (Key_MCT_Holiday7,
            "mct holiday 7")
        (Key_MCT_Holiday8,
            "mct holiday 8")
        (Key_MCT_Holiday9,
            "mct holiday 9")
        (Key_MCT_Holiday10,
            "mct holiday 10")
        (Key_MCT_Holiday11,
            "mct holiday 11")
        (Key_MCT_Holiday12,
            "mct holiday 12")
        (Key_MCT_Holiday13,
            "mct holiday 13")
        (Key_MCT_Holiday14,
            "mct holiday 14")
        (Key_MCT_Holiday15,
            "mct holiday 15")
        (Key_MCT_Holiday16,
            "mct holiday 16")
        (Key_MCT_Holiday17,
            "mct holiday 17")
        (Key_MCT_Holiday18,
            "mct holiday 18")
        (Key_MCT_Holiday19,
            "mct holiday 19")
        (Key_MCT_Holiday20,
            "mct holiday 20")
        (Key_MCT_Holiday21,
            "mct holiday 21")
        (Key_MCT_Holiday22,
            "mct holiday 22")
        (Key_MCT_Holiday23,
            "mct holiday 23")
        (Key_MCT_Holiday24,
            "mct holiday 24")
        (key_MCT_PhaseLossPercent,
            "mct phase loss percent")
        (key_MCT_PhaseLossSeconds,
            "mct phase loss seconds")
        ;

CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo() :
    _entry_id(-1),
    _pao_id(-1),
    _owner_id(Application_Invalid),
    _key(Key_Invalid),
    _value("")
{
}


CtiTableDynamicPaoInfo::CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k) :
    _entry_id(-1),
    _pao_id(paoid),
    _owner_id(Application_Invalid),
    _key(k),
    _value("")
{
}


bool CtiTableDynamicPaoInfo::operator<(const CtiTableDynamicPaoInfo &rhs) const
{
    //  there should not be more than one of these in any device's collection of table entries, so this is safe for a total ordering
    //    it makes set-based lookups possible, as well - i didn't want to use a map in the device
    return getKey() < rhs.getKey();
}


string CtiTableDynamicPaoInfo::getTableName()
{
    return string("DynamicPaoInfo");
}


bool CtiTableDynamicPaoInfo::Insert(Cti::Database::DatabaseConnection &conn)
{
    bool success = true;

    boost::optional<string> tmp_owner, tmp_key;
    string tmp_value;

    if( _owner_map.find(getOwnerID()) != _owner_map.end() )
    {
        tmp_owner = (_owner_map.find(getOwnerID()))->second;
    }

    if( _key_map.find(getKey()) != _key_map.end() )
    {
        tmp_key = (_key_map.find(getKey()))->second;
    }

    getValue(tmp_value);
    if( tmp_value.empty() )
    {
        tmp_value = EmptyString;
    }

    if( (getPaoID() >= 0) && tmp_owner && tmp_key )
    {
        static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?, ?, ?, ?)";

        Cti::Database::DatabaseWriter   inserter(conn, sql);

        inserter
            << getEntryID()     //  MUST be set before we try to insert
            << getPaoID()
            << *tmp_owner
            << *tmp_key
            << tmp_value
            << CtiTime();

        if(isDebugLudicrous())
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << CtiTime() << " **** INSERT Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
        }

        success = inserter.execute();

        if( ! success )    // error occured!
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** SQL FAILED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
        }
        else
        {
            resetDirty(FALSE);
        }
    }
    else
    {
        if( ! tmp_owner )  tmp_owner = EmptyString;
        if( ! tmp_key   )  tmp_key   = EmptyString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid attempt to insert into " << getTableName() << " - paoid = " << getPaoID() << ", tmp_owner = \"" << *tmp_owner << "\", and tmp_key = \"" << *tmp_key << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return success;
}

bool CtiTableDynamicPaoInfo::Update(Cti::Database::DatabaseConnection &conn)
{
    bool success = true;

    boost::optional<string> tmp_owner, tmp_key;
    string tmp_value;

    if( _owner_map.find(getOwnerID()) != _owner_map.end() )
    {
        tmp_owner = (_owner_map.find(getOwnerID()))->second;
    }

    if( _key_map.find(getKey()) != _key_map.end() )
    {
        tmp_key = (_key_map.find(getKey()))->second;
    }

    getValue(tmp_value);
    if( tmp_value.empty() )
    {
        tmp_value = EmptyString;
    }

    if( getEntryID() && tmp_owner && tmp_key )
    {
        static const std::string sql = "update " + getTableName() +
                                       " set "
                                            "value = ?, "
                                            "updatetime = ?"
                                        " where "
                                            "paobjectid = ? and "
                                            "owner = ? and "
                                            "infokey = ?";

        Cti::Database::DatabaseWriter   updater(conn, sql);

        updater
            << tmp_value.c_str()
            << CtiTime::now()
            << getPaoID()
            << *tmp_owner
            << *tmp_key;

        success = executeUpdater(updater);

        if( success )
        {
            setDirty(false);
        }
        //  we'll be doing this in mgr_device, because we need to assign a new entryid on insert
/*        else
        {
            stat = Insert(conn);        // Try a vanilla insert if the update failed!
        }*/
    }
    else
    {
        if( ! tmp_owner )  tmp_owner = EmptyString;
        if( ! tmp_key   )  tmp_key   = EmptyString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid attempt to update " << getTableName() << " - paoid = " << getPaoID() << ", tmp_owner = \"" << *tmp_owner << "\", and tmp_key = \"" << *tmp_key << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return success;
}

string CtiTableDynamicPaoInfo::getSQLCoreStatement(CtiApplication_t _app_id)
{
    owner_map_t::const_iterator o_itr;

    static const string sqlStart =  "SELECT DPI.entryid, DPI.paobjectid, DPI.owner, DPI.infokey, DPI.value "
                                    "FROM DynamicPaoInfo DPI ";

    o_itr = _owner_map.find(_app_id);

    if( o_itr != _owner_map.end() )
    {
        const string sqlWhere = "WHERE DPI.owner = '" + o_itr->second + "'";
        return string(sqlStart + sqlWhere);
    }
    else
    {
        return string("");
    }
}

void CtiTableDynamicPaoInfo::DecodeDatabaseReader(Cti::RowReader& rdr)
{
    string tmp_owner, tmp_key, tmp_value;
    long tmp_entryid, tmp_paoid;

    key_map_t::const_iterator k_itr;
    owner_map_t::const_iterator o_itr;

    rdr["entryid"]    >> _entry_id;
    rdr["paobjectid"] >> _pao_id;
    rdr["owner"]      >> tmp_owner;
    rdr["infokey"]    >> tmp_key;
    rdr["value"]      >> tmp_value;

    o_itr = _owner_map.begin();
    while( o_itr != _owner_map.end() )
    {
        if( tmp_owner == o_itr->second )
        {
            setOwner(o_itr->first);
            o_itr = _owner_map.end();
        }
        else
        {
            o_itr++;
        }
    }

    k_itr = _key_map.begin();
    while( k_itr != _key_map.end() )
    {
        if( tmp_key == k_itr->second )
        {
            setKey(k_itr->first);
            k_itr = _key_map.end();
        }
        else
        {
            k_itr++;
        }
    }

    //  should we turn _empty_string into ""?
    setValue(tmp_value);

    //  make sure this happens at the end, so we reset the dirty bit AFTER all of those above calls set it dirty
    resetDirty();
}


long CtiTableDynamicPaoInfo::getEntryID() const
{
    return _entry_id;
}
long CtiTableDynamicPaoInfo::getPaoID() const
{
    return _pao_id;
}
CtiApplication_t CtiTableDynamicPaoInfo::getOwnerID() const
{
    return _owner_id;
}
CtiTableDynamicPaoInfo::PaoInfoKeys CtiTableDynamicPaoInfo::getKey() const
{
    return _key;
}
string CtiTableDynamicPaoInfo::getKeyString() const
{
    key_map_t::const_iterator key = _key_map.find(_key);

    if( key == _key_map.end() )
    {
        return string();
    }

    return key->second;
}

string CtiTableDynamicPaoInfo::getValue() const
{
    return _value;
}

string CtiTableDynamicPaoInfo::getOwnerString() const
{
    owner_map_t::const_iterator owner = _owner_map.find(_owner_id);

    if( owner == _owner_map.end() )
    {
        return string();
    }

    return owner->second;
}

//  these may need to become individually named get functions, if the assignment idiom doesn't work out
void CtiTableDynamicPaoInfo::getValue(string &destination) const
{
    destination = _value;
}
void CtiTableDynamicPaoInfo::getValue(int &destination) const
{
    destination = atoi(_value.c_str());
}

void CtiTableDynamicPaoInfo::getValue(long &destination) const
{
    destination = atol(_value.c_str());
}
void CtiTableDynamicPaoInfo::getValue(unsigned long &destination) const
{
    double tmp;
    getValue(tmp);

    if( tmp >= 0 )
    {
        destination = (unsigned long)tmp;
    }
    else
    {
        destination = 0UL;
    }
}
void CtiTableDynamicPaoInfo::getValue(double &destination) const
{
    destination = atof(_value.c_str());
}


void CtiTableDynamicPaoInfo::setEntryID(long entry_id)
{
    _entry_id = entry_id;

    setDirty();
}
void CtiTableDynamicPaoInfo::setOwner(CtiApplication_t owner_id)
{
    _owner_id = owner_id;

    setDirty();
}
void CtiTableDynamicPaoInfo::setKey(PaoInfoKeys k)
{
    _key = k;

    setDirty();
}

void CtiTableDynamicPaoInfo::setValue(const string &s)
{
    //  maybe put in a null check, and assign "(empty)"
    _value = s;

    setDirty();
}
void CtiTableDynamicPaoInfo::setValue(double d)
{
    _value = CtiNumStr(d);

    setDirty();
}
void CtiTableDynamicPaoInfo::setValue(int i)
{
    _value = CtiNumStr(i);

    setDirty();
}
void CtiTableDynamicPaoInfo::setValue(unsigned int i)
{
    _value = CtiNumStr(i);

    setDirty();
}
void CtiTableDynamicPaoInfo::setValue(long l)
{
    _value = CtiNumStr(l);

    setDirty();
}
void CtiTableDynamicPaoInfo::setValue(unsigned long ul)
{
    _value = CtiNumStr(ul);

    setDirty();
}


void CtiTableDynamicPaoInfo::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "getEntryID() " << getEntryID() << endl;
        dout << "getPaoID()   " << getPaoID() << endl;
        dout << "getOwnerID() " << getOwnerID() << endl;
        dout << "getKey()     " << getKey() << endl;
        dout << "getValue()   " << getValue() << endl;
    }
}

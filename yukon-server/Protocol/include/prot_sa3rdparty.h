#pragma once

#include "cmdparse.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"
#include "protocol_sa.h"        // Telvent provided sa library.

#define MAX_SAERR_MSG_SIZE 256

#define GRP_SA_RTM 100              // Must be greater than any REAL grouptype.

                                    // CPARMS for setting "PROTOCOL_SA_TELVENT"
#define CTIPROT_ABRUPT_RESTORE                  0x00000010          // Using this could make the restores work WRONG...  Remove this and code wrapped one day soon.

class IM_EX_PROT CtiProtocolSA3rdParty : public Cti::Protocol::Interface
{
protected:

    enum
    {
        sac_unspec = 16,
        sac_toos,               // _sTime = hours off
        sac_address_config      // ???
    };

    CtiSAData _sa;

    CtiTime _onePeriodTime;

    bool _messageReady;

    CHAR _errorBuf[MAX_SAERR_MSG_SIZE];
    int _errorLen;

    INT assembleControl(CtiCommandParser &parse);
    INT assemblePutConfig(CtiCommandParser &parse);

    INT loadControl();                 // This is a shed!
    INT addressAssign(INT &len, USHORT slot);
    INT restoreLoadControl();
    //INT formRTMRequest(USHORT command);

    /*
     * This method used the input strategy or the period and percentage to produce a strategy value.
     * The produced value will be equal or larger than the requested parse.  If the parse is unsuccessful, the result will be -1.
     */
    int solveStrategy(CtiCommandParser &parse);

private:

    // The data below is used to identify the strategy in a Versacomesque fashion.  This data is converted to the closest equal or higher control values.
    int _period;          // Cycle Time. 7.5*60, 15*60, 20*60, 30*60, & 60*60 are valid values.
    int _timeout;         // Percentage of the cycle that the loads are to be shed (in seconds = timeout.).

    void computeShedTimes(int shed_time);
    void processResult(INT retCode);

    SA_CODE _sa_code;   // This structure is filled out based upon all else.  It can be used later to generate a string command.
    X205CMD _sa_x205cfg;

public:

    CtiProtocolSA3rdParty();
    CtiProtocolSA3rdParty(const CtiSAData sa);

    virtual ~CtiProtocolSA3rdParty();

    CtiProtocolSA3rdParty& operator=(const CtiProtocolSA3rdParty& aRef);
    int parseCommand(CtiCommandParser &parse);

    bool messageReady() const;

    CtiProtocolSA3rdParty& setTransmitterAddress( int val );
    CtiProtocolSA3rdParty& setGroupType( int val );
    CtiProtocolSA3rdParty& setShed( bool val );
    CtiProtocolSA3rdParty& setFunction( int val );
    CtiProtocolSA3rdParty& setCode205( int val );
    CtiProtocolSA3rdParty& setCodeGolay( std::string val );
    CtiProtocolSA3rdParty& setCodeSADigital( std::string val );
    CtiProtocolSA3rdParty& setSwitchTimeout( int val );
    CtiProtocolSA3rdParty& setCycleTime( int val );
    CtiProtocolSA3rdParty& setRepeats( int val );
    CtiProtocolSA3rdParty& setLBTMode( int val );
    CtiProtocolSA3rdParty& setDelayTxTime( int val );
    CtiProtocolSA3rdParty& setMaxTxTime( int val );

    void copyMessage(std::string &str) const;

    void getBuffer(BYTE *dest, ULONG &len) const;
    void appendVariableLengthTimeSlot(int transmitter,BYTE *dest,ULONG &len, BYTE dlyToTx = 0, BYTE maxTx = 0, BYTE lbtMode = 0);
    void statusScan(int transmitter, BYTE *dest, ULONG &len);

    INT getSABufferLen() const;
    CtiSAData getSAData() const;
    CtiProtocolSA3rdParty& setSAData(const CtiSAData &sa);

    int getStrategySTime() const;
    int getStrategyCTime() const;
    CtiTime getStrategyOnePeriodTime() const;

    const SA_CODE& getSACode() const { return _sa_code; };
    const X205CMD& getX205Cmd() const { return _sa_x205cfg; };

    static INT formatTMScmd (UCHAR *abuf, INT *buflen, USHORT TMS_cmd_type, USHORT xmitter);
    static INT TMSlen (UCHAR *abuf, INT *len);
    static INT procTMSmsg(UCHAR *abuf, INT len, SA_CODE *scode, X205CMD *x205cmd);

    static std::pair< unsigned long, unsigned > parseGolayAddress(unsigned long code);
    static std::pair< unsigned long, unsigned > parseGolayAddress(const std::string &golay_code);

    static std::string asString(const CtiSAData &sa);
    static std::string strategyAsString(const CtiSAData &sa);
    static std::string functionAsString(const CtiSAData &sa);
    static std::pair< int, int > computeSnCTime(const int swTimeout, const int cycleTime);
    static std::pair< int, int > computeSWnCTTime(const int sTime, const int cTime, bool gt105 = false);

    static std::string asString(const SA_CODE &sa);
    static std::string asString(const X205CMD &cmd);

};

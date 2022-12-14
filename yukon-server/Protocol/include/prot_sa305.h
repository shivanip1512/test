#pragma once

#include <vector>

#include "cmdparse.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dsm2.h"
#include "pointtypes.h"

class IM_EX_PROT CtiProtocolSA305 : private boost::noncopyable
{
    enum {
        AddressTypeGroupFlag = 0x20,
        GroupFlag = 0x10,
        DivisionFlag = 0x08,
        SubstationFlag = 0x04,
        CommandTypeOperationFlag = 0x00,
        CommandTypeAsignmentFlag = 0x02,
        CommandDescription_DIMode = 0x01,
        CommandDescription_DLCMode = 0x00,
        CommandDescription_29Bit = 0x01,
        CommandDescription_13Bit = 0x00,

        SA305StartBits = 0x04,
        CV326StartBits = 0x05,
    };

    int _padBits;            // For RTC trx this is 00b.
    int _startBits;         // May be 100 = "4" for SA305 or 101 = "5" for SA326 adaptive algorithm.
    bool _messageReady;

    int _serial;            // 22-bit
    int _group;             // 6 bits. Optional group address
    int _division;          // 6-bits. Optional group address
    int _substation;        // 10-bits. Optional group address

    int _utility;           // 4-bits. Always sent.
    int _rateFamily;        // 3-bits. Always sent with a group addressed message.
    int _rateMember;        // 4-bits. Always sent with a group addressed message.
    int _rateHierarchy;     // 1-bits. Always sent with a group addressed message.

    int _addressUsage;      // Used to specify which address fields are sent no matter which are set.

    // The flags member is generated by other calls and cannot be directly edited.
    // Flags F5 - Address Type - if set, one or more group/optional addresses are included.
    // Flags F4 - Group - if set, the group address is included in the message.
    // Flags F3 - Division - if set, the division address is included in the message.
    // Flags F2 - Substation - if set, the substation address is included in the message.
    // Flags F1 - Command Type -
    //              If F1 is set a configuration message.
    //              If F1 clear an operational command is sent in the message.
    // Flags F0 - Command Description -
    //              F1=0: If F1 set, the command is a 29-bit assignement.
    //              F1=0: If F1 clear, the command is a 13-bit general purpose assignement.
    int _flags;              // 6-bits

    // The following data members are used if the F1 Flag is CLEAR.  This is an operational command and is 13-bits in length.
    int _strategy;          // 6-bits. Describes the timeouts and cycle times. Found in table Appendix A.
    int _functions;         // 4-bits. FN4-FN1.  All four or any combination may be represented in this way.
    int _repetitions;       // 4-bits. ATTN: 0 in this field means 1 repetition.  Max is 13 meaning 14 repitions.  Indicates the number of times the strategy is executed.
    int _priority;          // 2-bits. 0 is the lowest priority and 3 the highest.

    // The following data members are used if the F1 Flag is SET.  This is an assignment or configuration address.
    int _dataType;          // 5-bits. Describes the assignment function.
    int _data;              // 8-bits. Defines the data to download.


    // The data below is used to identify the strategy in a Versacomesque fashion.  This data is converted to the closest equal or higher control values.
    float _period;          // Cycle Time. 7.5, 15, 20, 30, & 60 are valid values.
    float _percentageOff;   // Percentage of the cycle that the loads are to be shed.


    int _transmitterType;       // ? Transmitter
    int _transmitterAddress;    // Used for RTC targeted 305 messages
    BYTE _rtcResponse;          // Should the RTC respond to commands?


    std::vector< BYTE > _messageBits;  // Store it as bits.  Not sure if this will cost me in the long run...

    void addBits(unsigned int src, int num);
    UINT getBits(unsigned int &offset, int len) const;

    void resetMessage();
    void addressMessage(int command_type = CommandTypeOperationFlag, int command_description = CommandDescription_DIMode);
    INT assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage);
    INT assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage);

    INT timedLoadControl();                 // This is a shed!
    INT restoreLoadControl();
    INT cycleLoadControl();

    /*
     * This method used the input strategy or the period and percentage to produce a strategy value.
     * The produced value will be equal or larger than the requested parse.  If the parse is unsuccessful, the result will be -1.
     */
    int solveStrategy(CtiCommandParser &parse);

    std::string _bitStr;

    static const std::string _strategyStr[64];

    unsigned char addBitToCRC(unsigned char crc, unsigned char bit); // bit is 0 or 1
    void appendCRCToMessage();

    void setStartBits(int val);

public:

    CtiProtocolSA305();
    CtiProtocolSA305(const BYTE *bytestr, UINT bytelen);

    int parseCommand(CtiCommandParser &parse, CtiOutMessage &OutMessage);

    bool messageReady() const;
    int buildNumericPageMessage(CHAR *buffer) const;      // Returns the length in characters of this message.
    int buildHexMessage(CHAR *buffer) const;      // Returns the length in characters of this message.

    void setTransmitterAddress( int val );
    void setTransmitterType( int trans );

    std::string getBitString() const;
    std::string getAsciiString() const;
    std::string getDescription() const;
};

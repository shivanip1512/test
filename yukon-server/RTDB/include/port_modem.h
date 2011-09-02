#pragma once

#include "port_base.h"

#include <boost/noncopyable.hpp>

class CtiHayesModem : boost::noncopyable
{
public:

    typedef void (*CHAR_PRINTER)( char c );

    typedef enum
    {
        ASGENERALERROR         = -1,
        ASINVPORT              = -2,
        ASINUSE                = -3,
        ASINVBUFSIZE           = -4,
        ASNOMEMORY             = -5,
        ASNOTSETUP             = -6,
        ASINVPAR               = -7,
        ASBUFREMPTY            = -8,
        ASBUFRFULL             = -9,
        ASTIMEOUT              = -10,
        ASNOCTS                = -11,
        ASNOCD                 = -12,
        ASNODSR                = -13,
        ASNO8250               = -14,
        ASXMSTATUS             = -15,
        ASUSERABORT            = -16,
        ASFILERR               = -17,
        ASXMERROR              = -18,
        ASNOWIDERX             = -19,
        ASCONFLICT             = -20,
        ASCRCMODE              = -21,
        ASNOHAYESOK            = -22,
        ASNOHAYESRESPONSE      = -23,
        ASNOTSUPPORTED         = -24,
        ASILLEGALBAUDRATE      = -25,
        ASILLEGALPARITY        = -26,
        ASILLEGALWORDLENGTH    = -27,
        ASILLEGALSTOPBITS      = -28,
        ASNOCOPYRIGHTNOTICE    = -29,
        ASDRIVERNOTINSTALLED   = -30,
        ASOVERFLOW             = -31,
        ASCONNECTFAILURE       = -32,
        ASDOSEXTENDERERROR     = -33,
        ASILLEGALBOARDNUMBER   = -34,
        ASBOARDINUSE           = -35,
        ASHANDSHAKEBLOCK       = -36,
        ASMAXPORTSEXCEEDED     = -37,
        ASILLEGALIRQ           = -38,
        ASIRQINUSE             = -39,
        AS_THUNK_SETUP_FAILED  = -40,
        AS_NASI_ERROR          = -41,
        AS_PACKING_ERROR       = -42,
        ASUSERDEFINEDERROR     = -75

    } CtiHayesModemErrors;

protected:

    int _status;        // Modem status.
    int _delay_value;
    std::string _match_string;
    CHAR_PRINTER _character_printer;

private:

    int hm3(  char *s, int ctrl );

    CtiPort *_port;                     // Not ours to touch or delete or own!

public:

    CtiHayesModem();
    virtual ~CtiHayesModem();

    CtiHayesModem& setPort( CtiPort *port );


    int reset(  );
    int getRegister(  int modem_register );
    int setRegister(  int modem_register, int value );
    int setAutoAnswerRingCount(  int count );
    int getIncomingRingCount(  );
    int setEscapeCode(  char escape_char );
    int setEndOfLineCharacter(  char end_of_line_char );
    int setLineFeedCharacter(  char line_feed_char );
    int setBackspaceCharacter(  char backspace_char );
    int setWaitForDialToneTime(  int wait_for_dt_time);
    int setWaitTimeForCarrier(  int wait_for_cd_time );
    int setPauseTimeForComma(  int pause_time_for_comma );
    int setCDResponseTime(  int carrier_detect_response );
    int setCarrierDisconnectTime(  int disc_timer );
    int setTouchToneDuration(  int dial_speed );
    int setEscapeCodeGuardTime(  int escape_guard_time );
    int getUARTStatus(  );
    int getOptionRegister(  );
    int getFlagRegister(  );
    int goOnline(  );
    int dial(  char  *digit_string );
    int repeatLastCommand(  );
    int dialInAnswerMode(  char  *digits );
    int dialAndReturnToCommandMode(  char  *digit_string );
    int answer(  );
    int setCarrier(  int on_off );
    int setEchoMode(  int on_off );
    int setFullDuplexMode(  int full_or_half );
    int setHookSwitch(  int on_or_off_hook );
    int setSpeaker(  int speaker_control );
    int returnNoResultCodes(  int on_or_off );
    int selectExtendedResultCodes(  int extended_codes );
    int setVerboseMode(  int on_of_off );
    int sendString(  const char  *string );
    int sendStringNoWait(  const char  *string, int termination_sequence );
    void setUpEchoRoutine( CHAR_PRINTER character_printer );
    void setUpAbortKey( unsigned int key );
    void waitForOK( long milliseconds, char  *match_string );
    void fixedDelay( long milliseconds );
    long inputLine(  long milliseconds, char  *buffer, int length );
    int writeString(  const char *string, char termination_sequence );


    static bool validModemResponse (PCHAR Response);

};

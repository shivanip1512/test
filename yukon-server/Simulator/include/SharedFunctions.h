#ifndef  __SHAREDFUNCTIONS_H__
#define  __SHAREDFUNCTIONS_H__

#include "ctitime.h"

int uchar_parity( unsigned char x );
unsigned char makeAck(int ccuAddress);

enum WordTypes
{   
    DEFAULT    = 0,
    INPUTTYPE      = 0,
    RESETREQ   = 1,
    RESETACK   = 2,
    GENREQ     = 3,
    GENREP     = 4,
    A_WORD     = 31,
    B_WORD     = 32,
    C_WORD     = 33,
    D_WORD     = 34,
    E_WORD     = 35,
    G_WORD     = 36,
    DTRAN      = 11,
    LGRPQ      = 12,
    RCOLQ      = 13,
    ACTIN      = 14,
    WSETS      = 15,
    XTIME      = 16,
    FEEDEROP   = 21,
    PING       = 22,
    ONEACK     = 23,
    FUNCREAD   = 41,
    READ       = 42,
    WRITE      = 43,
    READREP1   = 44,
    READREP2   = 45,
    READREP3   = 46,
    FUNC_WRITE = 47,
    FUNC_READ  = 48,
    ACKACK     = 51,
    INCOMING   = 0,
    OUTGOING   = 1
};

unsigned int mctGetValue(int mctNumber, CtiTime time);
double mctGetLoadProfile(int mctNumber, CtiTime time);

#endif

#pragma once

#include "dlldefs.h"
#include "cticalls.h"

USHORT  IM_EX_CTIBASE CrcCalc_C(const BYTE* pStr, ULONG cnt);
USHORT  IM_EX_CTIBASE NCrcCalc_C(const BYTE* pStr, ULONG cnt);
USHORT  IM_EX_CTIBASE SCrcCalc_C(BYTE* pStr, ULONG cnt);
USHORT  IM_EX_CTIBASE BCHCalc_C(BYTE* pStr, ULONG bits);
CHAR    IM_EX_CTIBASE Parity_C(char ch);


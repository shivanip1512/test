#ifndef _cti_asmc_h_
#define _cti_asmc_h_

USHORT  IM_EX_CTIBASE CrcCalc_C(BYTE* pStr, ULONG cnt);
USHORT  IM_EX_CTIBASE NCrcCalc_C(BYTE* pStr, ULONG cnt);
USHORT  IM_EX_CTIBASE SCrcCalc_C(BYTE* pStr, ULONG cnt);
USHORT  IM_EX_CTIBASE BCHCalc_C(BYTE* pStr, ULONG bits);
CHAR    IM_EX_CTIBASE Parity_C(char ch);
INT     IM_EX_CTIBASE RPort_C(USHORT port);
INT     IM_EX_CTIBASE WPort_C(USHORT port, INT data);

#endif


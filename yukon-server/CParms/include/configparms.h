#pragma once

#include "dlldefs.h"
#include "cparms.h"

#ifdef __cplusplus
extern "C" {
#endif

IM_EX_C_CPARM BOOL            isConfigOpt(const std::string& key);
IM_EX_C_CPARM void            DumpConfigParms();
IM_EX_C_CPARM BOOL            getConfigValueAsString(const std::string& Key, char *targ, int len);

typedef BOOL (*CPARM_ISCONFIG)(const std::string&);
typedef int  (*CPARM_REFRESH)(const std::string&);
typedef void (*CPARM_DUMPCONFIG)(void);
typedef BOOL (*CPARM_GETCONFIGSTRING)(const std::string&, char*, int);

#ifdef __cplusplus
}
#endif

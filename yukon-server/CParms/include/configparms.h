#ifndef __CONFIGPARMS_H__
#define __CONFIGPARMS_H__

#include <rw\cstring.h>
#include "dlldefs.h"
#include "cparms.h"

#ifdef __cplusplus
extern "C" {
#endif

IM_EX_C_CPARM BOOL            isConfigOpt(RWCString key);
IM_EX_C_CPARM int             RefreshConfigParameters(RWCString FileName = DefaultMasterConfigFileName);
IM_EX_C_CPARM void            DumpConfigParms();
IM_EX_C_CPARM BOOL            getConfigValueAsString(RWCString Key, char *targ, int len);

typedef BOOL (*CPARM_ISCONFIG)(RWCString);
typedef int  (*CPARM_REFRESH)(RWCString);
typedef void (*CPARM_DUMPCONFIG)(void);
typedef BOOL (*CPARM_GETCONFIGSTRING)(RWCString, char*, int);

#ifdef __cplusplus
}
#endif




#endif // #ifndef __CONFIGPARMS_H__



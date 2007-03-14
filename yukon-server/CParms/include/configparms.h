#ifndef __CONFIGPARMS_H__
#define __CONFIGPARMS_H__

#include "dlldefs.h"
#include "cparms.h"

#ifdef __cplusplus
extern "C" {
#endif

IM_EX_C_CPARM BOOL            isConfigOpt(const string& key);
IM_EX_C_CPARM int             RefreshConfigParameters(const string& FileName = DefaultMasterConfigFileName);
IM_EX_C_CPARM void            DumpConfigParms();
IM_EX_C_CPARM BOOL            getConfigValueAsString(const string& Key, char *targ, int len);

typedef BOOL (*CPARM_ISCONFIG)(const string&);
typedef int  (*CPARM_REFRESH)(const string&);
typedef void (*CPARM_DUMPCONFIG)(void);
typedef BOOL (*CPARM_GETCONFIGSTRING)(const string&, char*, int);

#ifdef __cplusplus
}
#endif




#endif // #ifndef __CONFIGPARMS_H__



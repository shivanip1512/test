#include "yukon.h"

#include <rw/thr/condtion.h>
#include <rw/thr/barrier.h>
#include <rw/thr/thrfunc.h>

#include "dlldefs.h"

#include "socketinterface.h"
#include "inetinterface.h"


CtiTestInterface *ti;

#ifdef __cplusplus
extern "C" {
#endif

//  
    
    DLLEXPORT int CreateInterface( void )
{
    ti = new CtiTestInterface;
    return TRUE;
}

DLLEXPORT int InitInterface( void )
{
    return ti->init( );
}

DLLEXPORT void GetSuicideTools( RWCondition *suicideVar, RWBarrier *barrier )
{         
    ti->submitSuicideTools( suicideVar, barrier );
}
                        
DLLEXPORT int RunInterface( void )
{
    RWThreadFunction interfaceFunc = rwMakeThreadFunction( *ti, CtiTestInterface::runInterface );
    interfaceFunc.start( );
    return TRUE;
}

DLLEXPORT int hitman( int *sig )
{
    ti->hitman( sig );
    return TRUE;
}

#ifdef __cplusplus
}
#endif



#include "pointstore.h"

RWDEFINE_NAMED_COLLECTABLE(CtiPointStoreElement,"CtiPointStoreElement")

CtiPointStoreElement *CtiPointStore::insertPointElement( long pointNum, long dependentId, enum PointUpdateType updateType )
{
    CtiPointStoreElement *newElement;
    CtiHashKey *newHashKey;

    if( pointNum == 0 )
        return NULL;

    newElement = new CtiPointStoreElement( pointNum );
    newHashKey = new CtiHashKey( pointNum );

    //  if the insertion wasn't successful, that means this point is already in the pointstore
    if( !(this->insert( newHashKey, newElement )) )
    {
        delete newElement;
        //  may as well use the indexing hashkey before deleting it...
        newElement = (CtiPointStoreElement *)((*this)[newHashKey]);
        delete newHashKey;
    }

    //  in either case, newElement now points to the CtiPointStoreElement of pointID pointNum...

    //  we append the pointID of the calc point that is dependent on it...
    if( ( updateType == allUpdate || updateType == anyUpdate || updateType == periodicPlusUpdate) &&
        dependentId > 0 )// this be for the calc points cause they ain't got no dependents
        newElement->appendDependent( dependentId, updateType );

    return newElement;
}

/* Pointer to the singleton instance of CtiPointStore
   Instantiate lazily by Instance */
CtiPointStore* CtiPointStore::_instance = NULL;

/*---------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of CtiPointStore
---------------------------------------------------------------------------*/
CtiPointStore *CtiPointStore::getInstance()
{
    if ( _instance == NULL )
    {
        _instance = new CtiPointStore();
    }

    return _instance;
}



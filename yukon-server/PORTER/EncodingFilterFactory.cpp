#include "yukon.h"

#include "EncodingFilterFactory.h"
#include "encryption_lantronix.h"
#include "encryption_noop.h"

#include "utility.h"

//These need to change if the database change. Match the client
const string EncodingFilterFactory::NoFilterType = "none";
const string EncodingFilterFactory::LantronrixUdpAES = "AES";

/**
 *  Returns an Encoding Filter for the given port id. This
 *  function has two db hits.
 */
EncodingFilterFactory::EncodingFilterSPtr EncodingFilterFactory::getEncodingFilter(int portId)
{
	/* Db hit*/
	string type = getEncodingTypeForPort(portId);

	if (!LantronrixUdpAES.compare(type))
	{
		/* Db hit*/
		string encode = getEncodingKeyForPort(portId);
		LantronixEncryptionImpl* filter = new LantronixEncryptionImpl();
		filter->setKey(encode);

		return EncodingFilterSPtr(filter);
	}
	else
	{
		//return a no-op filter
		return EncodingFilterSPtr(new NoOpEncryption());
	}

}


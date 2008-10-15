#include "yukon.h"

#include "EncodingFilterFactory.h"
#include "LantronixEncryptionImpl.h"
#include "utility.h"

//These need to change if the database change. Match the client
const string EncodingFilterFactory::NoFilterType = "none";
const string EncodingFilterFactory::LantronrixUdpAES = "AES";

EncodingFilterFactory::EncodingFilterSPtr EncodingFilterFactory::getEncodingFilter(int portId)
{
	/* Db hit*/
	string type = getEncodingTypeForPort(portId);

	if (LantronrixUdpAES.compare(type))
	{
		/* Db hit*/
		string encode = getEncodeStringForPort(portId);
		LantronixEncryptionImpl* filter = new LantronixEncryptionImpl();
		filter->setKey(encode);

		return EncodingFilterSPtr(filter);
	}
	else
	{
		//return a no-op filter
		return EncodingFilterSPtr(new EncodingFilter());
	}

}


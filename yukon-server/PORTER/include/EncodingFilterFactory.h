#ifndef __ENCODINGFILTERFACTORY__
#define __ENCODINGFILTERFACTORY__

#include <string>
#include <boost/shared_ptr.hpp>
#include "EncodingFilter.h"

using std::string;

class EncodingFilterFactory
{
	public:
		typedef boost::shared_ptr<EncodingFilter> EncodingFilterSPtr;

		static EncodingFilterSPtr getEncodingFilter(int portId);

		static const string NoFilterType;
		static const string LantronrixUdpAES;

};

#endif

#pragma once
#include <boost/container/string.hpp>

namespace Cti {

class YukonMetricTracker
{
public:
	YukonMetricTracker();
	YukonMetricTracker(static const std::string pointInfo, int64_t value, long long timeStamp);

	void sendYukonMetricMessage(static const std::string pointInfo, int64_t value, long long timeStamp);
	static const std::string RPH_INSERTS;
	static const std::string RPH_QUEUE_SIZE;
};

}
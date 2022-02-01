#pragma once
#include <string>

namespace Cti {

class YukonMetricTracker
{
public:
	YukonMetricTracker(){}

	void sendYukonMetricMessage(const std::string pointInfo, int64_t value, std::string timeStamp);
	static const std::string RPH_INSERTS;
	static const std::string RPH_QUEUE_SIZE;

};
}
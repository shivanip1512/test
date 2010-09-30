#include "yukon.h"

#include "dev_mct420.h"
#include "config_device.h"

#include "dev_mct420_commands.h"

using namespace Cti::Devices::Commands;

namespace Cti {
namespace Devices {

const Mct420Device::ConfigPartsList  Mct420Device::_config_parts = Mct420Device::initConfigParts();
const Mct420Device::read_key_store_t Mct420Device::_readKeyStore = Mct420Device::initReadKeyStore();

Mct420Device::ConfigPartsList Mct420Device::initConfigParts()
{
    return ConfigPartsList(1, PutConfigPart_display);
}

Mct420Device::read_key_store_t Mct420Device::initReadKeyStore()
{
    //  inherit the MCT-410's key store...  this is a little awkward, but perhaps the best way to do it
    read_key_store_t readKeyStore = Mct410Device::initReadKeyStore();

    const int read1 = Mct420LcdConfigurationCommand::Read_LcdConfiguration1 & 0xff;

    readKeyStore.insert(read_key_info_t(read1,  0, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01));
    readKeyStore.insert(read_key_info_t(read1,  1, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02));
    readKeyStore.insert(read_key_info_t(read1,  2, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03));
    readKeyStore.insert(read_key_info_t(read1,  3, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04));
    readKeyStore.insert(read_key_info_t(read1,  4, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05));
    readKeyStore.insert(read_key_info_t(read1,  5, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06));
    readKeyStore.insert(read_key_info_t(read1,  6, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07));
    readKeyStore.insert(read_key_info_t(read1,  7, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08));
    readKeyStore.insert(read_key_info_t(read1,  8, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09));
    readKeyStore.insert(read_key_info_t(read1,  9, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10));
    readKeyStore.insert(read_key_info_t(read1, 10, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11));
    readKeyStore.insert(read_key_info_t(read1, 11, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12));
    readKeyStore.insert(read_key_info_t(read1, 12, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13));

    const int read2 = Mct420LcdConfigurationCommand::Read_LcdConfiguration2 & 0xff;

    readKeyStore.insert(read_key_info_t(read2,  0, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14));
    readKeyStore.insert(read_key_info_t(read2,  1, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15));
    readKeyStore.insert(read_key_info_t(read2,  2, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16));
    readKeyStore.insert(read_key_info_t(read2,  3, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17));
    readKeyStore.insert(read_key_info_t(read2,  4, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18));
    readKeyStore.insert(read_key_info_t(read2,  5, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19));
    readKeyStore.insert(read_key_info_t(read2,  6, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20));
    readKeyStore.insert(read_key_info_t(read2,  7, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21));
    readKeyStore.insert(read_key_info_t(read2,  8, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22));
    readKeyStore.insert(read_key_info_t(read2,  9, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23));
    readKeyStore.insert(read_key_info_t(read2, 10, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24));
    readKeyStore.insert(read_key_info_t(read2, 11, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25));
    readKeyStore.insert(read_key_info_t(read2, 12, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26));

    return readKeyStore;
}


Mct420Device::ConfigPartsList Mct420Device::getPartsList()
{
    return _config_parts;
}


int Mct420Device::executePutConfigDisplay(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* > &outList, bool readsOnly)
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return NoConfigData;
    }

    vector<unsigned char> display_metrics;

    if( ! readsOnly )
    {
        static const PaoInfoKeys lcd_metric_keys[26] = {

            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26
        };

        int i = 0;

        vector<unsigned char> paoinfo_metrics;

        for each( const PaoInfoKeys pao_key in lcd_metric_keys )
        {
            string config_key = "Display Item " + CtiNumStr(++i);

            long config_value;

            if( !deviceConfig->getLongValue(config_key, config_value) )
            {
                if( getMCTDebugLevel(DebugLevel_Configs) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Device \"" << getName() << "\" - invalid value (" << deviceConfig->getValueFromKey(config_key) << ") for config key \"" << config_key << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                return NoConfigData;
            }

            display_metrics.push_back(config_value);

            long pao_value = 0xff;

            //  will not touch pao_value if it's not found
            getDynamicInfo(pao_key, pao_value);

            paoinfo_metrics.push_back(pao_value);
        }

        if( display_metrics.size() == paoinfo_metrics.size() && std::equal(display_metrics.begin(), display_metrics.end(), paoinfo_metrics.begin()) )
        {
            if( ! parse.isKeyValid("force") )
            {
                return ConfigCurrent;
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
        }
    }

    DlcCommandSPtr lcdConfiguration(new Mct420LcdConfigurationCommand(display_metrics, readsOnly));

    if( ! tryExecuteCommand(*OutMessage, lcdConfiguration) )
    {
        return NoMethod;
    }

    outList.push_back(OutMessage);

    OutMessage = NULL;

    return NoError;
}

}
}


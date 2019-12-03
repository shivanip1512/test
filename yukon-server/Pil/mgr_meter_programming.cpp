#include "precompiled.h"

#include "mgr_meter_programming.h"

#include "mgr_device.h"
#include "mgr_dyn_paoinfo.h"
#include "dev_rfn.h"

#include "database_reader.h"
#include "encryption.h"
#include "std_helper.h"

namespace {
    
typedef void(*func)(void *pData, size_t dataSize);

typedef struct _fileInfo_s
{
    char        *pFile;
    uint16_t    fileSize;
    char        *pPassword;
    uint8_t     pwdLength;
}FILEINFO_t;

int conProcessBlob(const FILEINFO_t *pData)
{
    //  unused, writes to a file.

    return 0;
}
int conProcessBlob(const FILEINFO_t *pData, func callback)
{
    std::vector<char> buf {
        pData->pPassword,
        pData->pPassword + pData->pwdLength };

    //  just concatenate the two for testing purposes
    buf.insert(buf.end(),
        pData->pFile, 
        pData->pFile + pData->fileSize);

    callback(buf.data(), buf.size());

    return 0;
}

std::mutex programMux;
Cti::Pil::MeterProgrammingManager::Bytes globalBuffer;

}

namespace Cti::Pil {

MeterProgrammingManager::MeterProgrammingManager(CtiDeviceManager& deviceManager)
    :   _deviceManager  { deviceManager }
{}
    
auto MeterProgrammingManager::getProgram(const std::string guid) -> Bytes
{
    if( std::lock_guard lg(programMux); 
        auto existingProgram = mapFindRef(_programs, guid) )
    {
        return *existingProgram;
    }

    std::string sql = "select program, password from MeterProgram where guid = ?";

    Database::DatabaseConnection conn;
    Database::DatabaseReader rdr { conn, sql };

    rdr << guid;

    rdr.execute();

    if( ! rdr() )
    {
//        CTILOG_ERROR(dout, "Could not retrieve MeterProgram entry for guid " << guid);
        
//        return {};

        //  Return Lorem Ipsum for the initial E2E Block Transfer integration test
        static const std::string testString =
            R"testString(
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin eget sapien eleifend, viverra purus a, venenatis ex. Etiam massa leo, sollicitudin nec placerat a, ultrices a eros. In tristique nunc id eros placerat, sit amet consectetur risus dignissim. Morbi sit amet malesuada purus. Mauris lobortis ut lectus ac dapibus. Nam cursus vestibulum pharetra. Nullam cursus congue lectus vel efficitur. Phasellus lacus tellus, posuere ac dolor et, maximus rutrum dolor. Praesent a ex non ante tristique ullamcorper. Praesent in ante porta mauris vulputate fermentum eget ut mauris. Mauris tempor sagittis massa ac sagittis.
In fermentum nulla vel nisi lacinia faucibus. Curabitur placerat viverra consequat. Curabitur facilisis pulvinar nibh ut luctus. Fusce ac ex ac lectus laoreet faucibus vel quis tortor. Vestibulum urna nulla, luctus ut quam in, tempus pellentesque nibh. Aenean bibendum metus ut placerat viverra. In sapien elit, vulputate a ornare quis, varius ut purus. Praesent gravida fermentum egestas. Donec sit amet eleifend mi. Nunc rutrum metus nec magna varius porta. Integer pellentesque risus vitae mauris mattis, vel lacinia sem tempor.
Aenean ac eros in lectus mattis ultrices. Vestibulum lobortis accumsan sagittis. Etiam eget pharetra tellus. Proin commodo bibendum sem, in egestas ligula placerat non. Curabitur sit amet dignissim nunc. Morbi viverra vel nunc id rutrum. Suspendisse blandit dolor massa, in cursus nunc suscipit eget. Vestibulum porta massa nulla, sit amet consectetur lacus viverra porta. Nullam non laoreet magna. Pellentesque a elementum nulla. Fusce quis metus fringilla neque efficitur molestie et vel dui. Sed consectetur, turpis eu pharetra consectetur, nisl magna consequat justo, non posuere nulla diam sit amet ante. Aliquam erat volutpat. Suspendisse interdum quam eget felis tempus, id eleifend mi rhoncus. Sed porta lacinia purus in consectetur. Vestibulum rhoncus diam ex, in malesuada dolor faucibus a.
Pellentesque id neque quis ex maximus rutrum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Integer sit amet velit id ligula gravida vulputate et congue ipsum. Sed mollis lorem at odio scelerisque porttitor. Aenean a eleifend nisl. Nullam a erat sodales, ullamcorper erat ac, tristique nunc. Nullam pretium malesuada ultrices. Praesent imperdiet auctor nisi at tincidunt. Sed convallis justo eu purus fringilla mattis. Morbi id commodo tortor. Mauris tincidunt dignissim est et pulvinar. Praesent aliquet, massa eget condimentum interdum, dolor justo rhoncus enim, vel feugiat velit neque non elit. Sed sapien arcu, sagittis vel mauris sit amet, iaculis laoreet lorem. Nam ultricies feugiat mollis.
In tristique vitae lorem porta laoreet. Donec pellentesque facilisis odio eu pulvinar. Sed a dapibus sapien. Etiam neque urna, accumsan et odio id, malesuada commodo quam. Curabitur eu justo luctus, auctor eros vel, vehicula odio. Fusce et finibus mi. Sed quis augue quis tortor condimentum suscipit ornare sit amet lacus. Donec enim ligula, consequat mattis urna a, iaculis pulvinar sapien. Quisque eget libero ut tortor aliquam lacinia rutrum in lectus. Nam erat enim, facilisis eget cursus et, dictum quis lectus. Proin placerat lectus eget leo molestie posuere. Nullam faucibus, ipsum ut finibus elementum, sem massa vestibulum velit, et luctus eros tellus et nisi. Etiam mattis lectus lorem. Donec lacinia semper faucibus.
Praesent elementum volutpat ante eget laoreet. Nullam quis porttitor leo, in porta nisi. Cras varius lorem a arcu euismod vehicula. Suspendisse nec eros non orci faucibus aliquet vel in diam. Aliquam erat volutpat. Donec fermentum auctor arcu, in efficitur lectus faucibus id. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Integer interdum diam quis tellus molestie varius.
In sit amet lorem nunc. Nullam non arcu ac leo lacinia pharetra. Nunc quis pretium tellus. Donec vulputate volutpat sodales. Phasellus ac velit nec velit cursus blandit vitae eget orci. Etiam ultrices a ipsum non scelerisque. Aliquam pellentesque consequat enim. Cras ullamcorper vehicula ullamcorper. Duis nec lectus vitae turpis sodales semper sed id risus. In vitae vehicula dui, nec tempus leo. Mauris fringilla leo in elit iaculis, eu pharetra nulla consectetur. Praesent dapibus nunc nibh, nec egestas nisi laoreet in. Integer egestas mauris vel lacus lacinia bibendum. Nullam rutrum tristique massa, ut interdum ligula dapibus a.
Maecenas quis risus nec odio porttitor commodo. Nulla rhoncus velit dictum, venenatis felis sed, suscipit lacus. Fusce sit amet nunc rhoncus, convallis nunc quis, sollicitudin leo. Sed leo tortor, egestas et purus id, faucibus pellentesque velit. Mauris id accumsan nulla. Phasellus eget erat est. Ut auctor urna nec venenatis bibendum. Quisque euismod sagittis odio ut fringilla. Integer nec urna porta, porta dui et, consectetur massa. Phasellus malesuada orci sit amet sem sollicitudin imperdiet. Phasellus non maximus purus. Nulla ac ullamcorper lorem. In id nulla nec orci volutpat porttitor. Quisque consectetur erat id faucibus fringilla.
Pellentesque sapien ante, iaculis ac tincidunt sit amet, faucibus sit amet mi. Ut aliquam ornare dignissim. Sed scelerisque nulla eget augue aliquam tincidunt. Nulla libero metus, sodales eu ornare sed, mattis non magna. Ut tincidunt, metus a hendrerit efficitur, eros justo ullamcorper libero, ut efficitur arcu mi mollis arcu. Proin auctor maximus lectus, ac dapibus nunc scelerisque eget. Phasellus sodales nec erat id maximus. Vivamus magna diam, finibus eget purus non, faucibus egestas ante. Nunc aliquet tellus non ipsum rutrum, ut venenatis justo commodo.
Maecenas egestas metus nibh, quis rhoncus arcu volutpat at. Integer quis lobortis orci, at sagittis lectus. Interdum et malesuada fames ac ante ipsum primis in faucibus. Suspendisse in mi congue, malesuada sem dapibus, venenatis lectus. Morbi justo eros, faucibus quis nulla id, pretium aliquam diam. Ut ut eleifend justo, vitae pulvinar magna. Maecenas varius sapien sed magna scelerisque finibus. Nulla ut ipsum molestie, mollis metus vitae, faucibus ex.
Praesent condimentum, dolor at tempus pretium, nibh lorem varius mi, eget commodo ligula arcu ut ante. Nulla posuere et urna id semper. Nunc nec aliquet justo. Nam et rhoncus tortor, ac dignissim orci. Maecenas diam augue, vehicula et nibh vel, accumsan iaculis felis. Aenean euismod ipsum sed leo mollis, ut iaculis lorem lacinia. Curabitur non leo pretium, rutrum elit eget, pulvinar magna. In eget mollis felis. Fusce gravida justo non semper condimentum.
Donec iaculis, lorem sit amet imperdiet scelerisque, arcu metus hendrerit odio, sit amet suscipit purus metus at libero. Cras eleifend ac enim in sollicitudin. Suspendisse at vestibulum nisi, eget consectetur eros. Ut at ipsum cursus, fermentum elit eu, aliquam ipsum. Sed non neque sit amet sem malesuada congue sit amet viverra nunc. Praesent nec metus et lacus tristique tempor eu quis lacus. Curabitur diam lorem, tempus id eleifend eget, volutpat sed lorem. Fusce pulvinar posuere finibus. Vivamus rhoncus neque nec ex scelerisque iaculis.
Curabitur eleifend turpis eu convallis iaculis. Nunc in porttitor massa. Proin metus libero, semper at quam in, porttitor ornare dui. Morbi elementum est id enim sollicitudin, nec hendrerit nunc faucibus. In et est in tellus ultrices gravida. In dui velit, aliquet id viverra et, rhoncus non sapien. Pellentesque congue arcu in massa bibendum interdum. Praesent id convallis nulla.
Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nullam at posuere ipsum, ac scelerisque libero. Maecenas eleifend nunc a ante ultricies, et malesuada ex cursus. Cras nunc magna, aliquet ut mi ut, egestas luctus leo. Aenean a odio rhoncus, feugiat est id, tempus ex. Nulla ligula ex, tristique et tincidunt ut, eleifend at tellus. Nam elementum enim risus, vitae vehicula libero tristique quis. Ut rhoncus, ipsum sed ornare luctus, enim odio rutrum enim, vestibulum scelerisque nulla orci eu felis.
Ut imperdiet quam quis orci rhoncus vehicula. Cras tincidunt in ligula ut cursus. Nunc nec sem et mauris lacinia volutpat id id nibh. Nunc aliquet quam odio, eget auctor dui sagittis quis. Vivamus ut tristique lectus. Curabitur ut lacus quis justo sodales aliquam vitae quis enim. In in tincidunt nibh, nec finibus est. Cras ligula ex, pharetra quis sem vel, faucibus vehicula dui. Aenean quis aliquam nibh. Nam porta massa at velit accumsan condimentum. Nunc porttitor nibh ut commodo mollis. Suspendisse nec porttitor nunc, id varius lacus.
Sed eu placerat nisl, vitae vulputate nunc. In ac maximus sapien. Vestibulum id tempus sapien, sit amet tristique risus. Aliquam vitae lectus et leo aliquet rhoncus id in lectus. Praesent efficitur vitae ante eu convallis. Sed metus nulla, auctor eleifend gravida vel, malesuada tincidunt mauris. Aenean imperdiet odio et sapien bibendum lacinia. Vivamus congue sollicitudin sapien, in vulputate mauris ultricies nec.
Aliquam condimentum vel metus non tempus. Curabitur quis metus mi. Sed interdum ac ante in lobortis. Sed blandit libero dui. Suspendisse feugiat, risus placerat mollis ullamcorper, ligula mauris sollicitudin sem, in molestie tortor ipsum id tortor. Ut finibus justo urna, sit amet vehicula magna pretium quis. Quisque nisi sem, gravida ut nisi vel, sollicitudin viverra nibh. Sed massa nisl, scelerisque vel purus in, gravida aliquet turpis. Pellentesque gravida eleifend ex, rutrum porttitor velit eleifend vitae.
Vivamus quis quam sit amet nisi tempus porttitor. Vivamus nec hendrerit dui. Nunc dignissim eros ac varius semper. Donec ac viverra odio. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam erat volutpat. Nullam at mauris non est dignissim rhoncus. Vestibulum pharetra eget tellus vel porttitor.
Nulla facilisi. Vestibulum eget diam eget leo viverra rhoncus eget nec augue. Maecenas ut mollis nulla. Etiam faucibus ornare lorem sit amet hendrerit. Nam rutrum vulputate leo ut eleifend. Cras vitae commodo urna. Nullam ultricies ultrices sem, iaculis malesuada sem pretium eu. Quisque finibus id magna at varius. Proin laoreet massa ac justo blandit, sed gravida velit commodo. Fusce ullamcorper mi a nisi sollicitudin, non bibendum nisl pretium. Donec id lobortis metus. Aliquam nec tellus a odio pellentesque elementum et eget enim. Fusce lorem ex, ultricies ut tempus quis, venenatis sed lacus. Aliquam a ornare neque, id venenatis est.
Etiam viverra tincidunt gravida. Curabitur felis eros, ullamcorper in volutpat a, pretium sit amet elit. Donec eu purus et tortor elementum porttitor eu imperdiet ipsum. Sed nibh diam, vestibulum eget ex a, mattis dapibus elit. Etiam euismod laoreet placerat. Maecenas efficitur sapien vel tempus dapibus. Donec consequat purus quis venenatis iaculis. Ut sit amet enim faucibus, tempus arcu id, ultrices mauris. Proin id tristique dolor. Maecenas id dolor quis mauris commodo fringilla. Aenean laoreet diam erat, sed accumsan massa cursus a. Nunc interdum odio eu eleifend sollicitudin. Vestibulum hendrerit cursus justo id sodales. Praesent augue sapien, efficitur eget ultricies a, aliquam a odio metus.)testString";

        return { testString.begin(), testString.end() };
    }

    auto program = rdr["program"] .as<Bytes>();
    auto encryptedPassword = rdr["password"].as<Bytes>();

    auto password = Cti::Encryption::decrypt(Cti::Encryption::SharedKeyfile, encryptedPassword);

    //  convert to bytes
    std::vector<char> charProgram  { program.begin(),  program.end() };
    std::vector<char> charPassword { password.begin(), password.end() };

    FILEINFO_t fileInfo { charProgram.data(), static_cast<uint16_t>(charProgram.size()), 
                          charPassword.data(), static_cast<uint8_t>(password.size()) };

    Bytes buf;

    {
        std::lock_guard lg(programMux);

        auto captureToBuffer = [](void *buf, size_t len) {
            auto ucBuf = reinterpret_cast<unsigned char*>(buf);
            globalBuffer.assign(ucBuf, ucBuf + len);
        };

        if( auto error = conProcessBlob(&fileInfo, captureToBuffer) )
        {
            CTILOG_ERROR(dout, "Error processing meter program buffer" << FormattedList::of(
                "Error", error,
                "GUID", guid));

            return {};
        }

        buf = globalBuffer;

        _programs[guid] = buf;
    }

    return buf;
}

bool MeterProgrammingManager::isUploading(const RfnIdentifier rfnIdentifier, const std::string guid)
{
    //  TODO - remove after initial E2E block transfer integration test
    return true;

    if( auto rfnDevice = _deviceManager.getDeviceByRfnIdentifier(rfnIdentifier) )
    {
        std::string storedGuid;

        if( rfnDevice->getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigurationId, storedGuid)
                && storedGuid == guid )
        {
            return rfnDevice->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress);
        }
    }
    return false;
}

void MeterProgrammingManager::updateMeterProgrammingStatus(RfnIdentifier rfnIdentifier, std::string guid, size_t size, size_t totalSize)
{
    //  update the programming progress percentage
    if( auto rfnDevice = _deviceManager.getDeviceByRfnIdentifier(rfnIdentifier) )
    {
        std::string storedGuid;

        if( rfnDevice->getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingConfigurationId, storedGuid)
                && storedGuid == guid )
        {
            const double percentage = 100.0 * size / totalSize;

            rfnDevice->setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_MeterProgrammingProgress, percentage);
        }
    }

    //  send a Cti::Messaging::Porter::MeterProgramArchiveStatusRequestMsg
}

}
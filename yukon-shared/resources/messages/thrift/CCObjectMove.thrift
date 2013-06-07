include "CCMessage.thrift"

namespace cpp Cti.Messaging.Serialization.Thrift
namespace java com.cannontech.messaging.serialization.thrift.generated

struct CCObjectMove {
    1: required     CCMessage.CCMessage             _baseMessage;
    2: required     bool                            _permanentFlag; 
    3: required     i32                             _oldParentId;   
    4: required     i32                             _objectId;      
    5: required     i32                             _newParentId;   
    6: required     double                          _switchingOrder;
    7: required     double                          _closeOrder;
    8: required     double                          _tripOrder;
}

package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.CCState;
import com.cannontech.database.db.state.State;
import com.cannontech.database.db.state.StateGroupUtils;

public class StateSerializer extends ThriftSerializer<State, CCState> {

    protected StateSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<State> getTargetMessageClass() {
        return State.class;
    }

    @Override
    protected State createMessageInstance() {
        return new State();
    }

    @Override
    protected CCState createThrifEntityInstance() {
        return new CCState();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCState entity, State msg) {
        msg.setText(entity.get_text());
        msg.setForegroundColor(entity.get_foregroundColor());
        msg.setBackgroundColor(entity.get_backgroundColor());

        // set the stateGroupId to that of all CapBank states
        msg.setStateGroupId(StateGroupUtils.STATEGROUPID_CAPBANK);
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, State msg, CCState entity) {
        entity.set_text(msg.getText());
        entity.set_foregroundColor(msg.getForegroundColor());
        entity.set_backgroundColor(msg.getBackgroundColor());
    }

}

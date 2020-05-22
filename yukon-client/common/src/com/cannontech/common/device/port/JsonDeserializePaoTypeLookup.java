package com.cannontech.common.device.port;

import java.io.IOException;

import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.exception.TypeNotSupportedExcpetion;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class JsonDeserializePaoTypeLookup extends StdDeserializer<YukonPao> {

    protected JsonDeserializePaoTypeLookup() {
        super(YukonPao.class);
    }

    private IDatabaseCache serverDatabaseCache = YukonSpringHook.getBean(IDatabaseCache.class);

    @Override
    public YukonPao deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        TreeNode node = parser.readValueAsTree();
        // Catch the update case here.
        String id = ServletUtils.getPathVariable("id");
        PaoType paoType;

        if (id == null) {
            // Create Case
            paoType = getPaoTypeFromJson(node);
        } else {
            // Update case
            paoType = getPaoTypeFromCache(id);
        }
        return (YukonPao) parser.getCodec().treeToValue(node, getYukonPaoFromModelFactory(paoType).getClass());
    }

    /**
     * Retrieves PaoType from type field provided JSON.
     */
    private PaoType getPaoTypeFromJson(TreeNode node) {
        TreeNode type = node.get("type");
        if (type != null) {
            try {
                return PaoType.valueOf(type.toString().replace("\"", ""));
            } catch (IllegalArgumentException e) {
                // throw exception for invalid paoType
                throw new TypeNotSupportedExcpetion("type is not valid.");
            }
        } else {
            throw new NotFoundException("type is not found in the request.");
        }

    }

    /**
     * Retrieves PaoType from Cache based on id provided.
     */
    private PaoType getPaoTypeFromCache(String id) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(id));
        if (pao == null) {
            throw new NotFoundException("id not found.");
        }
        return pao.getPaoType();

    }

    /**
     * Retrieves valid Yukon pao from model factory.
     */
    private YukonPao getYukonPaoFromModelFactory(PaoType paoType) {
        YukonPao pao = PaoModelFactory.getModel(paoType);
        if (pao != null) {
            return pao;
        } else {
            // throw exception for not supported paoType
            throw new TypeNotSupportedExcpetion("type is not valid.");
           
        }
    }
}
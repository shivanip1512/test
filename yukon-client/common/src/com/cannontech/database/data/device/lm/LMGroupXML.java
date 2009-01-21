package com.cannontech.database.data.device.lm;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.LmGroupXmlParameterDao;
import com.cannontech.spring.YukonSpringHook;

public class LMGroupXML extends LMGroupExpressCom {
    
    public enum XmlType {
        OTHER(0),
        ZIGBEE(1);

        private final int pos;
        
        XmlType(int pos) {
            this.pos = pos;
        }
        
        public int getValue() {
            return pos;
        }
    }
    
    //Needs to be transient because this object gets serialized.
    transient private LmGroupXmlParameterDao xmlLMGroupParameterDao = null;
    
    private List<LmXmlParameter> parameterList;
	private XmlType xmlType = XmlType.ZIGBEE;
	
    public LMGroupXML() {
		super();
		parameterList = new ArrayList<LmXmlParameter>();
		
		getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_XML_GROUP[0] );
	}
	
    public void add() throws java.sql.SQLException 
    {
        super.add();
        
        //Remove them all first. Then add them back in. 
        getDao().removeAllByGroupId(getGroupId());
        
        for(LmXmlParameter param : parameterList) {
        	param.setLmGroupId(getGroupId());
            getDao().add(param);
        }
    }
    
    public void delete() throws java.sql.SQLException 
    {
        super.delete();
        getDao().removeAllByGroupId(getGroupId());
    }
    
    public void update() throws java.sql.SQLException 
    {
        super.update();
        
        getDao().removeAllByGroupId(getGroupId());
        
        for(LmXmlParameter param : parameterList) {  
        	getDao().add(param);
        }
    }
    
    public void retrieve() throws java.sql.SQLException 
    {
        super.retrieve();        
     
        List<LmXmlParameter> list = getDao().getParametersForGroup(getGroupId(),XmlType.ZIGBEE);
        
        parameterList.clear();
        parameterList.addAll(list);
    }
 
    public Integer getGroupId() {
        return super.getPAObjectID();
    }
    
    public List<LmXmlParameter> getParameterList() {
    	return parameterList;
    }

    public void setParameterList(List<LmXmlParameter> parameterList) {
        this.parameterList = parameterList;
    }

    /**
     * Returns the xmlDao. Use this instead of accessing the dao variable. This prevents a null.
     * 
     * @return
     */
    private LmGroupXmlParameterDao getDao() {
        if (xmlLMGroupParameterDao == null) {
            xmlLMGroupParameterDao = YukonSpringHook.getBean("xmlLMGroupParameterDao",LmGroupXmlParameterDao.class);
        }
        return xmlLMGroupParameterDao;
    }

    public XmlType getXmlType() {
        return xmlType;
    }

    public void setXmlType(XmlType xmlType) {
        this.xmlType = xmlType;
    }
    
}

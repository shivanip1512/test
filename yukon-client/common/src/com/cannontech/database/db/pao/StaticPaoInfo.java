package com.cannontech.database.db.pao;

import com.cannontech.common.pao.PaoInfo;
import com.cannontech.core.dao.NotFoundException;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

public class StaticPaoInfo extends DBPersistent
{
	private Integer staticPaoInfoId = null;
	private Integer paobjectId = null;
	private String infoKey = null;
	private String value = null;

	public static final String SETTER_COLUMNS[] = { 
		"PaobjectId", "InfoKey", "Value"
	};

	public static final String UNIQUE_SETTER_COLUMNS[] = { 
        "StaticPaoInfoId", "Value"
    };
	
	public static final String CONSTRAINT_COLUMNS[] = { "StaticPaoInfoId" };
	public static final String UNIQUE_COLUMNS[] = { "PaobjectId", "InfoKey" };
	

	public static final String TABLE_NAME = "StaticPaoInfo";

	public StaticPaoInfo(PaoInfo paoInfo) {
        super();
        this.infoKey = paoInfo.name();
    }
	 
	public StaticPaoInfo(Integer staticPaoInfoId, Integer paobjectId,
            PaoInfo paoInfo, String value) {
        super();
        this.staticPaoInfoId = staticPaoInfoId;
        this.paobjectId = paobjectId;
        this.infoKey = paoInfo.name();
        this.value = value;
    }

	public void add() throws java.sql.SQLException {
	    // always get a new primary key value on "add".
		setStaticPaoInfoId( getNextStaticPaoInfoId());

		Object addValues[] = {
			getStaticPaoInfoId(), getPaobjectId(),
			getInfoKey(), getValue()
		};
	
		add( TABLE_NAME, addValues );
	}
	
	public void delete() throws java.sql.SQLException {
	    Object values[] = { getStaticPaoInfoId() };
	    delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}

	public final static Integer getNextStaticPaoInfoId( ) {
	    NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("StaticPaoInfo");
	}
	
	/**
	 * This retrieve works a little differently, it doesn't load by the primary key.
	 * Instead, it loads by the unique key of PaobjectId, InfoKey
	 */
	public void retrieve() throws java.sql.SQLException {
		Object uniqueValues[] = { getPaobjectId(), getInfoKey()};
		Object results[] = retrieve( UNIQUE_SETTER_COLUMNS, TABLE_NAME, UNIQUE_COLUMNS, uniqueValues );
	
		if( results.length == UNIQUE_SETTER_COLUMNS.length ) {
			setStaticPaoInfoId((Integer) results[0] );
			setValue( (String) results[1] );
		} else {
		    throw new NotFoundException("Incorrect Number of results retrieved");
		}
	}
	
	public void update() throws java.sql.SQLException {
		Object setValues[] = {
			getPaobjectId(), getInfoKey(), getValue()
		};
						
		Object constraintValues[] = { 
		    getStaticPaoInfoId()
		};
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}


    public Integer getStaticPaoInfoId() {
        return staticPaoInfoId;
    }


    public void setInfoKey(String infoKey) {
        this.infoKey = infoKey;
    }

    public void setStaticPaoInfoId(Integer staticPaoInfoId) {
        this.staticPaoInfoId = staticPaoInfoId;
    }


    public Integer getPaobjectId() {
        return paobjectId;
    }


    public void setPaobjectId(Integer paobjectId) {
        this.paobjectId = paobjectId;
    }

    /**
     * This is the String representation of PaoInfo enum, used to write to the database.
     * @return
     */
    private String getInfoKey() {
        return infoKey;
    }
    
    public PaoInfo getPaoInfo() {
        return PaoInfo.valueOf(getInfoKey());
    }
    
    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

}
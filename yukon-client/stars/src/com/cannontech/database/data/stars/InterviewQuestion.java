package com.cannontech.database.data.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InterviewQuestion extends DBPersistent {
	
	private com.cannontech.database.db.stars.InterviewQuestion interviewQuestion = null;
	private Integer energyCompanyID = null;
	
	public void setQuestionID(Integer questionID) {
		getInterviewQuestion().setQuestionID( questionID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getInterviewQuestion().setDbConnection( conn );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    	
    	getInterviewQuestion().add();
        
    	// Add to mapping table
    	Object[] addValues = {
    		getEnergyCompanyID(),
    		getInterviewQuestion().getQuestionID(),
    		com.cannontech.database.db.stars.InterviewQuestion.TABLE_NAME
    	};
    	add("ECToGenericMapping", addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
    	// delete from mapping table
    	String[] constraintColumns = {
    		"ItemID", "MappingCategory"
    	};
    	Object[] constraintValues = {
    		getInterviewQuestion().getQuestionID(),
    		com.cannontech.database.db.stars.InterviewQuestion.TABLE_NAME
    	};
    	delete("ECToGenericMapping", constraintColumns, constraintValues);
    	
    	getInterviewQuestion().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getInterviewQuestion().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getInterviewQuestion().update();
	}

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * Returns the interviewQuestion.
	 * @return com.cannontech.database.db.stars.InterviewQuestion
	 */
	public com.cannontech.database.db.stars.InterviewQuestion getInterviewQuestion() {
		if (interviewQuestion == null)
			interviewQuestion = new com.cannontech.database.db.stars.InterviewQuestion();
		return interviewQuestion;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/**
	 * Sets the interviewQuestion.
	 * @param interviewQuestion The interviewQuestion to set
	 */
	public void setInterviewQuestion(com.cannontech.database.db.stars.InterviewQuestion interviewQuestion) {
		this.interviewQuestion = interviewQuestion;
	}

}

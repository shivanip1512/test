package com.cannontech.database.db.stars;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CustomerFAQ extends DBPersistent {
	
	private Integer questionID = null;
	private Integer subjectID = new Integer(com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID);
	private String question = "";
	private String answer = "";
	
	public static final String[] SETTER_COLUMNS = {
		"SubjectID", "Question", "Answer"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "QuestionID" };
	
	public static final String TABLE_NAME = "CustomerFAQ";
	
	public CustomerFAQ() {
		super();
	}
	
	public final Integer getNextQuestionID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	@Override
    public void add() throws SQLException {
		if (getQuestionID() == null)
			setQuestionID( getNextQuestionID() );
		
		Object[] addValues = {
			getQuestionID(), getSubjectID(), getQuestion(), getAnswer()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	@Override
    public void delete() throws SQLException {
		Object[] constraintValues = { getQuestionID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	@Override
    public void retrieve() throws SQLException {
		Object[] constraintValues = { getQuestionID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setSubjectID( (Integer) results[0] );
			setQuestion( (String) results[1] );
			setAnswer( (String) results[2] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	@Override
    public void update() throws SQLException {
		Object[] setValues = {
			getSubjectID(), getQuestion(), getAnswer()
		};
		Object[] constraintValues = { getQuestionID() };
		
		update( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues, SETTER_COLUMNS, setValues );
	}
	
	public static CustomerFAQ[] getAllCustomerFAQs(int faqGrpListID) {
		String sql = "SELECT QuestionID, SubjectID, Question, Answer" +
			" FROM " + TABLE_NAME + ", YukonListEntry" +
			" WHERE SubjectID = EntryID AND ListID = " + faqGrpListID +
			" ORDER BY SubjectID, QuestionID";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			CustomerFAQ[] faqs = new CustomerFAQ[ stmt.getRowCount() ];
			
			for (int i = 0; i < faqs.length; i++) {
				Object[] row = stmt.getRow(i);
				faqs[i] = new CustomerFAQ();
				
				faqs[i].setQuestionID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				faqs[i].setSubjectID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				faqs[i].setQuestion( (String) row[2] );
				faqs[i].setAnswer( (String) row[3] );
			}
			
			return faqs;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public static CustomerFAQ[] getCustomerFAQs(int subjectID) {
		String sql = "SELECT QuestionID, SubjectID, Question, Answer" +
				" FROM " + TABLE_NAME + " WHERE SubjectID = " + subjectID +
				" ORDER BY QuestionID";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			CustomerFAQ[] faqs = new CustomerFAQ[ stmt.getRowCount() ];
			
			for (int i = 0; i < faqs.length; i++) {
				Object[] row = stmt.getRow(i);
				faqs[i] = new CustomerFAQ();
				faqs[i].setQuestionID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				faqs[i].setSubjectID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				faqs[i].setQuestion( (String) row[2] );
				faqs[i].setAnswer( (String) row[3] );
			}
			
			return faqs;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public static void deleteCustomerFAQs(int subjectID) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE SubjectID = " + subjectID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	public static void deleteAllCustomerFAQs(int faqGrpListID) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE SubjectID IN (" +
			"SELECT EntryID FROM YukonListEntry WHERE ListID = " + faqGrpListID + ")";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}

	/**
	 * Returns the answer.
	 * @return String
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * Returns the question.
	 * @return String
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * Returns the questionID.
	 * @return Integer
	 */
	public Integer getQuestionID() {
		return questionID;
	}

	/**
	 * Returns the subjectID.
	 * @return Integer
	 */
	public Integer getSubjectID() {
		return subjectID;
	}

	/**
	 * Sets the answer.
	 * @param answer The answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * Sets the question.
	 * @param question The question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * Sets the questionID.
	 * @param questionID The questionID to set
	 */
	public void setQuestionID(Integer questionID) {
		this.questionID = questionID;
	}

	/**
	 * Sets the subjectID.
	 * @param subjectID The subjectID to set
	 */
	public void setSubjectID(Integer subjectID) {
		this.subjectID = subjectID;
	}

}

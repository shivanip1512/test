package com.cannontech.database.db.stars;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
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
	
	private Integer questionID = null;
	private Integer questionType = new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	private String question = "";
	private String mandatory = "N";
	private Integer displayOrder = new Integer(0);
	private Integer answerType = new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	private Integer expectedAnswer = new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	
	public static String[] SETTER_COLUMNS = {
		"QuestionType", "Question", "Mandatory", "DisplayOrder",
		"AnswerType", "ExpectedAnswer"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "QuestionID" };
	
	public static final String TABLE_NAME = "InterviewQuestion";
	
	private static final String GET_NEXT_QUESTION_ID_SQL =
			"SELECT MAX(QuestionID) FROM " + TABLE_NAME;

	public InterviewQuestion() {
		super();
	}
	
	public final Integer getNextQuestionID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextQuestionID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_QUESTION_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextQuestionID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {}
        }

        return new Integer( nextQuestionID );
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getQuestionID() == null)
			setQuestionID( getNextQuestionID() );
			
		Object[] addValues = {
			getQuestionID(), getQuestionType(), getQuestion(), getMandatory(),
			getDisplayOrder(), getAnswerType(), getExpectedAnswer()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getQuestionID() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getQuestionID() };
		
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setQuestionType( (Integer) results[0] );
			setQuestion( (String) results[1] );
			setMandatory( (String) results[2] );
			setDisplayOrder( (Integer) results[3] );
			setAnswerType( (Integer) results[4] );
			setExpectedAnswer( (Integer) results[5] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getQuestionType(), getQuestion(), getMandatory(),
			getDisplayOrder(), getAnswerType(), getExpectedAnswer()
		};
		Object[] constraintValues = { getQuestionID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static InterviewQuestion[] getAllInterviewQuestions(Integer energyCompanyID) {
		ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems(energyCompanyID, TABLE_NAME);
		if (items == null) return null;
		if (items.length == 0) return new InterviewQuestion[0];
		
		StringBuffer sql = new StringBuffer();
		sql.append( "SELECT QuestionID, QuestionType, Question, Mandatory, DisplayOrder, AnswerType, ExpectedAnswer " )
		   .append( "FROM " ).append( TABLE_NAME )
		   .append( " WHERE QUESTIONID = " ).append( items[0].getItemID() );
		for (int i = 1; i < items.length; i++)
			sql.append( " OR QUESTIONID = " ).append( items[i].getItemID() );
		sql.append( " ORDER BY DISPLAYORDER" );
		
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql.toString(), com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			InterviewQuestion[] questions = new InterviewQuestion[ stmt.getRowCount() ];
			
			for (int i = 0; i < questions.length; i++) {
				Object[] row = stmt.getRow(i);
				questions[i] = new InterviewQuestion();
				
				questions[i].setQuestionID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				questions[i].setQuestionType( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				questions[i].setQuestion( (String) row[2] );
				questions[i].setMandatory( (String) row[3] );
				questions[i].setDisplayOrder( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				questions[i].setAnswerType( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
				questions[i].setExpectedAnswer( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
			}
			
			return questions;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

	/**
	 * Returns the answerType.
	 * @return Integer
	 */
	public Integer getAnswerType() {
		return answerType;
	}

	/**
	 * Returns the displayOrder.
	 * @return Integer
	 */
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * Returns the expectedAnswer.
	 * @return Integer
	 */
	public Integer getExpectedAnswer() {
		return expectedAnswer;
	}

	/**
	 * Returns the mandatory.
	 * @return String
	 */
	public String getMandatory() {
		return mandatory;
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
	 * Returns the questionType.
	 * @return Integer
	 */
	public Integer getQuestionType() {
		return questionType;
	}

	/**
	 * Sets the answerType.
	 * @param answerType The answerType to set
	 */
	public void setAnswerType(Integer answerType) {
		this.answerType = answerType;
	}

	/**
	 * Sets the displayOrder.
	 * @param displayOrder The displayOrder to set
	 */
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	/**
	 * Sets the expectedAnswer.
	 * @param expectedAnswer The expectedAnswer to set
	 */
	public void setExpectedAnswer(Integer expectedAnswer) {
		this.expectedAnswer = expectedAnswer;
	}

	/**
	 * Sets the mandatory.
	 * @param mandatory The mandatory to set
	 */
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
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
	 * Sets the questionType.
	 * @param questionType The questionType to set
	 */
	public void setQuestionType(Integer questionType) {
		this.questionType = questionType;
	}

}

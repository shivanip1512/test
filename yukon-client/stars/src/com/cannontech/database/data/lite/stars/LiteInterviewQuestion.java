package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteInterviewQuestion extends LiteBase {
	
	private int questionType = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private String question = null;
	private String mandatory = null;
	private int displayOrder = 0;
	private int answerType = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private int expectedAnswer = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	
	public LiteInterviewQuestion() {
		super();
		setLiteType( LiteTypes.STARS_INTERVIEW_QUESTION );
	}
	
	public LiteInterviewQuestion(int questionID) {
		super();
		setQuestionID( questionID );
		setLiteType( LiteTypes.STARS_INTERVIEW_QUESTION );
	}
	
	public int getQuestionID() {
		return getLiteID();
	}
	
	public void setQuestionID(int questionID) {
		setLiteID( questionID );
	}

	/**
	 * Returns the answerType.
	 * @return int
	 */
	public int getAnswerType() {
		return answerType;
	}

	/**
	 * Returns the expectedAnswer.
	 * @return int
	 */
	public int getExpectedAnswer() {
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
	 * Returns the questionType.
	 * @return int
	 */
	public int getQuestionType() {
		return questionType;
	}

	/**
	 * Sets the answerType.
	 * @param answerType The answerType to set
	 */
	public void setAnswerType(int answerType) {
		this.answerType = answerType;
	}

	/**
	 * Sets the expectedAnswer.
	 * @param expectedAnswer The expectedAnswer to set
	 */
	public void setExpectedAnswer(int expectedAnswer) {
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
	 * Sets the questionType.
	 * @param questionType The questionType to set
	 */
	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}

	/**
	 * Returns the displayOrder.
	 * @return int
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * Sets the displayOrder.
	 * @param displayOrder The displayOrder to set
	 */
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

}

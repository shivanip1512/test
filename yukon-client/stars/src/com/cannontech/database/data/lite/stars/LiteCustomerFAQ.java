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
public class LiteCustomerFAQ extends LiteBase {
	
	private int subjectID = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private String question = null;
	private String answer = null;
	
	public LiteCustomerFAQ() {
		setLiteType( LiteTypes.STARS_CUSTOMER_FAQ );
	}
	
	public LiteCustomerFAQ(int questionID) {
		setLiteType( LiteTypes.STARS_CUSTOMER_FAQ );
		setQuestionID( questionID );
	}
	
	public int getQuestionID() {
		return getLiteID();
	}
	
	public void setQuestionID(int questionID) {
		setLiteID( questionID );
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
	 * Returns the subjectID.
	 * @return int
	 */
	public int getSubjectID() {
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
	 * Sets the subjectID.
	 * @param subjectID The subjectID to set
	 */
	public void setSubjectID(int subjectID) {
		this.subjectID = subjectID;
	}

}

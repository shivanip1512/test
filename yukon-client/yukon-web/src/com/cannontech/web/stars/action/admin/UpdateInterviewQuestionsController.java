package com.cannontech.web.stars.action.admin;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AnswerType;
import com.cannontech.stars.xml.serialize.QuestionType;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateInterviewQuestionsController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {

        try {
            StarsEnergyCompanySettings ecSettings =
                (StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
            StarsExitInterviewQuestions starsExitQuestions = ecSettings.getStarsExitInterviewQuestions();

            String type = request.getParameter("type");
            int qType = CtiUtilities.NONE_ZERO_ID;
            if (type.equalsIgnoreCase("Exit"))
                qType = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT ).getEntryID();

            String[] questions = request.getParameterValues("Questions");
            String[] answerTypes = request.getParameterValues("AnswerTypes");

            List<LiteInterviewQuestion> liteQuestions = energyCompany.getAllInterviewQuestions();

            synchronized (liteQuestions) {
                Iterator it = liteQuestions.iterator();
                while (it.hasNext()) {
                    LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) it.next();
                    if (liteQuestion.getQuestionType() == qType) {
                        com.cannontech.database.data.stars.InterviewQuestion question =
                            new com.cannontech.database.data.stars.InterviewQuestion();
                        question.setQuestionID( new Integer(liteQuestion.getQuestionID()) );
                        Transaction.createTransaction(Transaction.DELETE, question).execute();
                        it.remove();
                    }
                }
            }

            if (type.equalsIgnoreCase("Exit"))
                starsExitQuestions.removeAllStarsExitInterviewQuestion();

            if (questions != null) {
                for (int i = 0; i < questions.length; i++) {
                    com.cannontech.database.data.stars.InterviewQuestion question =
                        new com.cannontech.database.data.stars.InterviewQuestion();
                    com.cannontech.database.db.stars.InterviewQuestion questionDB = question.getInterviewQuestion();
                    questionDB.setQuestionType( new Integer(qType) );
                    questionDB.setQuestion( questions[i] );
                    questionDB.setMandatory( "N" );
                    questionDB.setDisplayOrder( new Integer(i+1) );
                    questionDB.setAnswerType( Integer.valueOf(answerTypes[i]) );
                    questionDB.setExpectedAnswer( new Integer(CtiUtilities.NONE_ZERO_ID) );
                    question.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
                    question = (com.cannontech.database.data.stars.InterviewQuestion)
                    Transaction.createTransaction(Transaction.INSERT, question).execute();

                    LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) StarsLiteFactory.createLite( question.getInterviewQuestion() );
                    synchronized (liteQuestions) { liteQuestions.add(liteQuestion); }

                    if (type.equalsIgnoreCase("Exit")) {
                        StarsExitInterviewQuestion starsQuestion = new StarsExitInterviewQuestion();
                        starsQuestion.setQuestionID( liteQuestion.getQuestionID() );
                        starsQuestion.setQuestion( liteQuestion.getQuestion() );
                        starsQuestion.setQuestionType( (QuestionType)StarsFactory.newStarsCustListEntry(
                                                                                                        DaoFactory.getYukonListDao().getYukonListEntry(liteQuestion.getQuestionType()), QuestionType.class) );
                        starsQuestion.setAnswerType( (AnswerType)StarsFactory.newStarsCustListEntry(
                                                                                                    DaoFactory.getYukonListDao().getYukonListEntry(liteQuestion.getAnswerType()), AnswerType.class) );
                        starsExitQuestions.addStarsExitInterviewQuestion( starsQuestion );
                    }
                }
            }

            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Interview questions updated successfully");
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update interview questions");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }        
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}

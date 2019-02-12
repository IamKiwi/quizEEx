package com.kiwi.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.kiwi.dao.QuestionDAO;
import com.kiwi.entities.Answer;
import com.kiwi.entities.Question;
import com.kiwi.entities.Quiz;

@ManagedBean
@ViewScoped
public class QuestionBB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_QUESTIONS = "qna?faces-redirect=true";
	
	private ResourceBundle messages;
	
	@EJB
	QuestionDAO questionDAO;
	private LazyDataModel<Question> questions;
	
	@PostConstruct
    public void initialization() {
		this.questions = new LazyDataModel<Question>() {
            private static final long serialVersionUID = 1L;
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            Quiz x = (Quiz) session.getAttribute("quiz");
            
            @Override
            public List<Question> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
               setRowCount(questionDAO.getRowsCount(x.getId()));
               return questionDAO.retrieveQuestionsForQuiz(x.getId(), first, pageSize);
            }
        };
        FacesContext context = FacesContext.getCurrentInstance();
		this.messages = ResourceBundle.getBundle("resources.adminbean", context.getViewRoot().getLocale());
	}
	
	public String questions(Quiz q) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute("quiz", q);
		return PAGE_QUESTIONS;
	}
	
	private Question question = new Question();
	
	public String saveData() {
		try {
			Map<String,String> params = 
	                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			question.setIdQuiz(Integer.parseInt(params.get("qid")));
			questionDAO.create(question);

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("saveError"), null));
			return PAGE_STAY_AT_THE_SAME;
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("questionToDBsuccess"), null));
		return PAGE_STAY_AT_THE_SAME;

	}
	
	public void onRowEdit(RowEditEvent event) {
        Question question = (Question) event.getObject();
            	
        try {
			questionDAO.merge(question);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("saveError"), null));
		}
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("questionUpdated"), null));
    }
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("questionEditCanceled"), null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	
	public String deleteQuestion(Question question){
		questionDAO.remove(question);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("questionWithAnswersDeleted"), null));
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public List<Question> getFullQuestionList(){
		return questionDAO.getFullList();
	}
	
//	public List<Question> getQuestionsListForQuiz(int id){
//		return questionDAO.getQuestionsForQuiz(id);
//	}
	
	public Question getQuestion() {
		return question;
	}

	public LazyDataModel<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(LazyDataModel<Question> questions) {
		this.questions = questions;
	}

}

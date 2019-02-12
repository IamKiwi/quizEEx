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

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.kiwi.dao.QuizDAO;
import com.kiwi.entities.Quiz;

@ManagedBean
@ViewScoped
public class QuizBB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_STAY_AT_THE_SAME = null;

	public String quizAdd() {
		return "quizedit";
	}
	
	private ResourceBundle messages;

	@EJB
	QuizDAO quizDAO;
	
	private LazyDataModel<Quiz> quizes;
	
	@PostConstruct
    public void initialization() {
		this.quizes = new LazyDataModel<Quiz>() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public List<Quiz> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
               setRowCount(quizDAO.getRowsCount());
               return quizDAO.retrieveQuizes(first, pageSize);
            }
        };
        
        FacesContext context = FacesContext.getCurrentInstance();
		this.messages = ResourceBundle.getBundle("resources.adminbean", context.getViewRoot().getLocale());
	}
	
	private Quiz quiz = new Quiz();

	public String saveData() {
		try {
			if(!quizDAO.isQuizUnique(quiz.getName())) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("quizUnique"), null));
				return PAGE_STAY_AT_THE_SAME;
			}
			
			quiz.setActive(false);
			quizDAO.create(quiz);

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
        Quiz quiz = (Quiz) event.getObject();
        
        try {
			quizDAO.merge(quiz);

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("saveError"), null));
		}
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("quizUpdated"), null));
    }
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("quizEditCanceled"), null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	public String deactivateQuiz(Quiz quiz) {
		quizDAO.deactivateQuiz(quiz);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("quizDisabled"), null));
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public String activateQuiz(Quiz quiz) {
		quizDAO.activateQuiz(quiz);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("quizEnabled"), null));
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public String deleteQuiz(Quiz quiz) {
		quizDAO.remove(quiz);
		FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("quizDeleted"), null));
		return PAGE_STAY_AT_THE_SAME;
	}
	
	public List<Quiz> getFullQuizList(){
		return quizDAO.getFullList();
	}
	
	public Quiz getQuiz() {
		return quiz;
	}

	public LazyDataModel<Quiz> getQuizes() {
		return quizes;
	}

	public void setQuizes(LazyDataModel<Quiz> quizes) {
		this.quizes = quizes;
	}
}

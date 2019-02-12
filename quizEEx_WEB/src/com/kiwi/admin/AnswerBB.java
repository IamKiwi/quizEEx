package com.kiwi.admin;

import java.io.Serializable;
import java.util.ArrayList;
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

import com.kiwi.dao.AnswerDAO;
import com.kiwi.entities.Answer;
import com.kiwi.entities.Question;
import com.kiwi.entities.Quiz;

@ManagedBean
@ViewScoped
public class AnswerBB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_STAY_AT_THE_SAME = null;
	private static final String PAGE_ANSWERS = "ans?faces-redirect=true";
	
	private ResourceBundle messages;

	@EJB
	AnswerDAO answerDAO;

	public String answers(Question q) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute("question", q);
		return PAGE_ANSWERS;
	}

	private LazyDataModel<Answer> answers;

	@PostConstruct
	public void initialization() {
		this.setAnswers(new LazyDataModel<Answer>() {
			private static final long serialVersionUID = 1L;
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			Question x = (Question) session.getAttribute("question");

			@Override
			public List<Answer> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(answerDAO.getRowsCount(x.getId()));
				return answerDAO.retrieveAnswersForQuestion(x.getId(), first, pageSize);
			}
		});
		
		FacesContext context = FacesContext.getCurrentInstance();
		this.messages = ResourceBundle.getBundle("resources.adminbean", context.getViewRoot().getLocale());
	}
	

	private ArrayList<Answer> answer = new ArrayList<Answer>();
	private int correct = 0;

	public AnswerBB() {
		for (int i = 0; i < 4; i++)
			answer.add(new Answer());
	}

	public String saveData() {
		try {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();

			for (int i = 0; i < 4; i++)
				if (i == correct)
					answer.get(i).setIsCorrect(true);

			for (Answer ans : answer)
				ans.setIdQuestion(Integer.parseInt(params.get("qid")));

			for (Answer ans : answer)
				answerDAO.create(ans);

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("saveError"), null));
			return PAGE_STAY_AT_THE_SAME;
		}

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("answersToDBsuccess"), null));
		return PAGE_STAY_AT_THE_SAME;
	}

	private String edited = null;

	public void onRowEdit(RowEditEvent event) {
		Answer answer = (Answer) event.getObject();

		if (edited.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("noChanges"), null));
			return;
		}

		answer.setAnswer(edited);

		try {
			answerDAO.merge(answer);

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("saveError"), null));
		}

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("answerUpdated"), null));
	}

	public void onRowCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("answerEditCanceled"), null);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public List<Answer> getFullAnswerList() {
		return answerDAO.getFullList();
	}

	public List<Answer> getAnswersListForQuestion(int id) {
		return answerDAO.getAnswersForQuestion(id);
	}

	public List<Answer> getAnswer() {
		return answer;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public String deleteAllAnswers(int id) {
		List<Answer> toDelete = getAnswersListForQuestion(id);
		if (toDelete.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("nothingToDelete"), null));
			return PAGE_STAY_AT_THE_SAME;
		} else {
			for (Answer ans : toDelete)
				answerDAO.remove(ans);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					messages.getString("deletedAll"), null));
			return PAGE_STAY_AT_THE_SAME;
		}
	}

	public String deleteAnswer(Answer answer) {
		answerDAO.remove(answer);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				messages.getString("answerDeleted"), null));
		return PAGE_STAY_AT_THE_SAME;
	}

	public String changeToCorrect(Answer answer) {
		List<Answer> answersForQuestion = getAnswersListForQuestion(answer.getIdQuestion());
		for (Answer ans : answersForQuestion)
			answerDAO.changeToIncorrect(ans);

		answerDAO.changeToCorrect(answer);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("markedAsCorrect"), null));
		return PAGE_STAY_AT_THE_SAME;
	}

	public String changeToIncorrect(Answer answer) {
		answerDAO.changeToIncorrect(answer);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, messages.getString("markedAsIncorrect"), null));
		return PAGE_STAY_AT_THE_SAME;
	}

	public String getEdited() {
		return edited;
	}

	public void setEdited(String edited) {
		this.edited = edited;
	}

	public LazyDataModel<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(LazyDataModel<Answer> answers) {
		this.answers = answers;
	}

}

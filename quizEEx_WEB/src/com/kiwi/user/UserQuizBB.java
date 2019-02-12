package com.kiwi.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.simplesecurity.RemoteClient;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.kiwi.dao.AnswerDAO;
import com.kiwi.dao.QuestionDAO;
import com.kiwi.dao.QuizDAO;
import com.kiwi.dao.UserDAO;
import com.kiwi.entities.Answer;
import com.kiwi.entities.Question;
import com.kiwi.entities.Quiz;
import com.kiwi.entities.User;

@Named
@ViewScoped
public class UserQuizBB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_CATSELECT = "catselect?faces-redirect=true";
	private static final String PAGE_QUIZSELECT = "quizselect?faces-redirect=true";
	private static final String PAGE_DOQUIZ = "doquiz?faces-redirect=true";
	private static final String PAGE_FINISHED = "finished?faces-redirect=true";
	
	@EJB
	QuizDAO quizDAO;
	@EJB
	QuestionDAO questionDAO;
	@EJB
	AnswerDAO answerDAO;
	@EJB
	UserDAO userDAO;
	
	private List<String> categories = null;
	private List<Quiz> quizes = null;
	private List<String> useranswers = null;
	private List<Question> questions = null;
	private List<Answer> answers = null;
	private int index = 0;
	private int numberOfQuestions = 0;
	private String currentAnswer = null;
	private int points = 0;
	
	public UserQuizBB() {
		this.useranswers = new ArrayList<String>();
	}

	public String quizes() {
		this.categories = quizDAO.getCategories();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute("cat", this.categories);
		return PAGE_CATSELECT;
	}
	
	public String getQuizesByCategory(String value) {
		this.quizes = quizDAO.getQuizByCategory(value);
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute("quiz", this.quizes);
		return PAGE_QUIZSELECT;
	}
	
	public String tryAgain() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		Quiz q = (Quiz)session.getAttribute("quizName");
		int id = quizDAO.getQuizIdByName(q.getName());		
		this.questions = questionDAO.getQuestionsForQuiz(id);
		Collections.shuffle(this.questions);
		int qid = this.questions.get(this.index).getId();
		this.setAnswers(answerDAO.getAnswersForQuestion(qid));
		Collections.shuffle(this.answers);
		this.numberOfQuestions = this.questions.size();
		session.setAttribute("quizName", quizDAO.getQuiz(id));
		session.setAttribute("questions", this.questions);
		session.setAttribute("answers", this.answers);
		session.setAttribute("questionIndex", 0);
		session.setAttribute("noq", this.numberOfQuestions);
		
		return PAGE_DOQUIZ;
	}
	
	public String getQuiz(String quizname) {
		int id = quizDAO.getQuizIdByName(quizname);		
		this.questions = questionDAO.getQuestionsForQuiz(id);
		Collections.shuffle(this.questions);
		int qid = this.questions.get(this.index).getId();
		this.setAnswers(answerDAO.getAnswersForQuestion(qid));
		Collections.shuffle(this.answers);
		this.numberOfQuestions = this.questions.size();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.setAttribute("quizName", quizDAO.getQuiz(id));
		session.setAttribute("questions", this.questions);
		session.setAttribute("answers", this.answers);
		session.setAttribute("questionIndex", 0);
		session.setAttribute("noq", this.numberOfQuestions);
		
		return PAGE_DOQUIZ;
	}
	
	public void nextQuestion() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		this.questions = (List<Question>)session.getAttribute("questions");
		this.answers = (List<Answer>)session.getAttribute("answers");
		this.numberOfQuestions = this.questions.size();
		
		processUserAnswer();
		
		this.index++;
		
		int qid = this.questions.get(this.index).getId();
		this.setAnswers(answerDAO.getAnswersForQuestion(qid));
		Collections.shuffle(this.answers);
		
		session.removeAttribute("answers");
		session.setAttribute("answers", this.answers);
	}
	
	public void prevQuestion() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		this.questions = (List<Question>)session.getAttribute("questions");
		this.answers = (List<Answer>)session.getAttribute("answers");
		this.numberOfQuestions = this.questions.size();
		
		processUserAnswer();
		
		this.index--;
		
		int qid = this.questions.get(this.index).getId();
		this.setAnswers(answerDAO.getAnswersForQuestion(qid));
		Collections.shuffle(this.answers);
		
		session.removeAttribute("answers");
		session.setAttribute("answers", this.answers);
	}
	
	public String finish() {
		processUserAnswer();
		
		this.points = 0;
		
		List<Answer> correctAnswers = new ArrayList<Answer>();
		for(int i = 0; i < this.questions.size(); i++)
			correctAnswers.add(answerDAO.getCorrectAnswersForQuestion(this.questions.get(i).getId()));
		
		for(int i = 0; i < useranswers.size(); i++)
			if (useranswers.get(i).equals(""))
				useranswers.set(i, "Brak odpowiedzi");

		for (Answer c : correctAnswers)
			for (String u : useranswers)
				if (c.getAnswer().contains(u))
					this.points++;
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		
		session.setAttribute("ua", this.useranswers);
		session.setAttribute("ca", correctAnswers);
		session.setAttribute("yourPoints", this.points);
		session.setAttribute("maxPoints", this.numberOfQuestions);
		
		int uid = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("uid"));
		
		userDAO.addPoints(this.points, uid);
		
		return PAGE_FINISHED;
	}
	
	private void processUserAnswer() {
		if(this.index >= this.useranswers.size() && this.currentAnswer == null)
			this.useranswers.add(this.index, "");
		else if(this.index >= this.useranswers.size() && this.currentAnswer != null)
			this.useranswers.add(this.index, this.currentAnswer);
		else if(this.currentAnswer != null)
			this.useranswers.set(this.index, this.currentAnswer);
	}
	
	
	/****************************** Getters and setters ******************************/
	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public List<Quiz> getQuizes() {
		return quizes;
	}

	public void setQuizes(List<Quiz> quizes) {
		this.quizes = quizes;
	}
	
	public List<String> getUseranswers() {
		return useranswers;
	}

	public void setUseranswers(List<String> useranswers) {
		this.useranswers = useranswers;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(int numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	public String getCurrentAnswer() {
		return currentAnswer;
	}

	public void setCurrentAnswer(String currentAnswer) {
		this.currentAnswer = currentAnswer;
	}
}

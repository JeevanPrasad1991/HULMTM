package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class QuestionGetterSetter {
	
	ArrayList<String> question_id = new ArrayList<String>();
	ArrayList<String> question = new ArrayList<String>();
	ArrayList<String> display_id = new ArrayList<String>();
	
	
	public ArrayList<String> getDisplay_id() {
		return display_id;
	}
	public void setDisplay_id(String display_id) {
		this.display_id.add(display_id);
	}
	String meta_data;
	
	public String getMeta_data() {
		return meta_data;
	}
	public void setMeta_data(String meta_data) {
		this.meta_data = meta_data;
	}
	public ArrayList<String> getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(String question_id) {
		this.question_id.add(question_id);
	}
	public ArrayList<String> getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question.add(question);
	}


}

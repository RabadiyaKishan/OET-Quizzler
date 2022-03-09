package com.oet.quiz.Model;

public class ModelQuestion {
    String ID;
    String Question;
    String OptionOne;
    String OptionTwo;
    String OptionThree;
    String OptionFour;
    String AnswerNumber;
    String TID;
    String SelectedAnswer;

    public String getSelectedAnswer() {
        return SelectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        SelectedAnswer = selectedAnswer;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getOptionOne() {
        return OptionOne;
    }

    public void setOptionOne(String optionOne) {
        OptionOne = optionOne;
    }

    public String getOptionTwo() {
        return OptionTwo;
    }

    public void setOptionTwo(String optionTwo) {
        OptionTwo = optionTwo;
    }

    public String getOptionThree() {
        return OptionThree;
    }

    public void setOptionThree(String optionThree) {
        OptionThree = optionThree;
    }

    public String getOptionFour() {
        return OptionFour;
    }

    public void setOptionFour(String optionFour) {
        OptionFour = optionFour;
    }

    public String getAnswerNumber() {
        return AnswerNumber;
    }

    public void setAnswerNumber(String answerNumber) {
        AnswerNumber = answerNumber;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }
}

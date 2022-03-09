package com.oet.quiz.Model;

public class ModelResult {
    String ID;
    String WrongAnswer;
    String RightAnswer;
    String TotalQuestion;
    String AttemptTime;
    String Duration;
    String ExamName;
    String ImageURL;

    public String getExamName() {
        return ExamName;
    }

    public void setExamName(String examName) {
        ExamName = examName;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getWrongAnswer() {
        return WrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        WrongAnswer = wrongAnswer;
    }

    public String getRightAnswer() {
        return RightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        RightAnswer = rightAnswer;
    }

    public String getTotalQuestion() {
        return TotalQuestion;
    }

    public void setTotalQuestion(String totalQuestion) {
        TotalQuestion = totalQuestion;
    }

    public String getAttemptTime() {
        return AttemptTime;
    }

    public void setAttemptTime(String attemptTime) {
        AttemptTime = attemptTime;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

}

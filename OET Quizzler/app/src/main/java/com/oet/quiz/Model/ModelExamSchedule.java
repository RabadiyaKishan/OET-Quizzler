package com.oet.quiz.Model;

public class ModelExamSchedule {
    String ID;
    String Name;
    String Images;
    String Duration;
    String IsActive;
    String QuestionLimit;
    String DateTime;
    String MarkingSystem;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getQuestionLimit() {
        return QuestionLimit;
    }

    public void setQuestionLimit(String questionLimit) {
        QuestionLimit = questionLimit;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getMarkingSystem() {
        return MarkingSystem;
    }

    public void setMarkingSystem(String markingSystem) {
        MarkingSystem = markingSystem;
    }
}

package com.oet.quiz.Model;

public class ModelExamList {

    String ID;
    String Name;
    String Images;
    String Duration;
    String IsActive;
    String QuestionLimit;
    String MarkingSystem;
    String StartTime;
    String EndTime;
    String SelectedDate;
    boolean IsAttemptable;
    String TimeDiff;

    public String getTimeDiff() {
        return TimeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        TimeDiff = timeDiff;
    }

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

    public String getMarkingSystem() {
        return MarkingSystem;
    }

    public void setMarkingSystem(String markingSystem) {
        MarkingSystem = markingSystem;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getSelectedDate() {
        return SelectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        SelectedDate = selectedDate;
    }

    public boolean getIsAttemptable() {
        return IsAttemptable;
    }

    public void setIsAttemptable(boolean isAttemptable) {
        IsAttemptable = isAttemptable;
    }
}

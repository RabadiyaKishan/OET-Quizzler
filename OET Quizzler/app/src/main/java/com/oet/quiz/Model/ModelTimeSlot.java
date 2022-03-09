package com.oet.quiz.Model;

public class ModelTimeSlot {
    String ID;
    String SelectedDates;
    String TimeSlotID;
    String StartTime;
    String EndTime;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSelectedDates() {
        return SelectedDates;
    }

    public void setSelectedDates(String selectedDates) {
        SelectedDates = selectedDates;
    }

    public String getTimeSlotID() {
        return TimeSlotID;
    }

    public void setTimeSlotID(String timeSlotID) {
        TimeSlotID = timeSlotID;
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
}

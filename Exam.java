package com.project;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Exam {
    private String courseName;
    private boolean isSecondExam;//the second test is true
    private String preferredSecondDate;
    @JsonIgnore
    private Date preferredSecondDateObject;
    private int testDurationHours;
    private int requiredDaysToStudy;
    private int daysToStudy;
    private String scheduledDate;
    private String scheduledTime;
    @JsonIgnore
    private Date scheduledDateObject;

    public Exam() {

    }

    public Exam(Course course, boolean isSecondExam) {
        courseName = course.getName();
        this.isSecondExam = isSecondExam;
        testDurationHours = course.getTestDurationHours();
        requiredDaysToStudy = course.getDaysToStudy();
    }

    @JsonIgnore
    public boolean isRequiredDaysAccomplished() {
        return daysToStudy >= requiredDaysToStudy;
    }
    @JsonIgnore
    public boolean isSecondExamPreferredDateAccomplished() {
        return preferredSecondDateObject.getTime() <= scheduledDateObject.getTime();
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public boolean isSecondExam() {
        return isSecondExam;
    }

    public void setSecondExam(boolean secondExam) {
        isSecondExam = secondExam;
    }

    public int getTestDurationHours() {
        return testDurationHours;
    }

    public void setTestDurationHours(int testDurationHours) {
        this.testDurationHours = testDurationHours;
    }

    public int getRequiredDaysToStudy() {
        return requiredDaysToStudy;
    }

    public void setRequiredDaysToStudy(int requiredDaysToStudy) {
        this.requiredDaysToStudy = requiredDaysToStudy;
    }

    public int getDaysToStudy() {
        return daysToStudy;
    }

    public void setDaysToStudy(int daysToStudy) {
        this.daysToStudy = daysToStudy;
    }

    public String getPreferredSecondDate() {
        return preferredSecondDate;
    }

    public void setPreferredSecondDate(String preferredSecondDate) {
        this.preferredSecondDate = preferredSecondDate;
    }
    @JsonIgnore
    public Date getPreferredSecondDateObject() {
        return preferredSecondDateObject;
    }
    @JsonIgnore
    public void setPreferredSecondDateObject(Date preferredSecondDateObject) {
        this.preferredSecondDateObject = preferredSecondDateObject;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @JsonIgnore
    public Date getScheduledDateObject() {
        return scheduledDateObject;
    }
    @JsonIgnore
    public void setScheduledDateObject(Date scheduledDateObject) {
        this.scheduledDateObject = scheduledDateObject;
    }
}

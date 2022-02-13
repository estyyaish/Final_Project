package com.project;

import java.util.Date;

public class Course {
    private String name;
    private boolean isMorningCourse;
    private int daysToStudy;
    private int testDurationHours;

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", isMorningCourse=" + isMorningCourse +
                ", daysToStudy=" + daysToStudy +
                ", testDurationHours=" + testDurationHours +
                '}';
    }

    public int getTestDurationHours() {
        return testDurationHours;
    }

    public void setTestDurationHours(int testDurationHours) {
        this.testDurationHours = testDurationHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMorningCourse() {
        return isMorningCourse;
    }

    public void setMorningCourse(Boolean morningCourse) {
        this.isMorningCourse = morningCourse;
    }

    public boolean isMorningCourse() {
        return isMorningCourse;
    }

    public void setMorningCourse(boolean morningCourse) {
        isMorningCourse = morningCourse;
    }

    public int getDaysToStudy() {
        return daysToStudy;
    }

    public void setDaysToStudy(int daysToStudy) {
        this.daysToStudy = daysToStudy;
    }
}

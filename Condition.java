package com.project;

public class Condition {

    private String conditionName;
    private int daysBetweenExams;
    private int gapFirstAndSecondExam;
    private int minMorningHour;
    private int minAfternoonHour;
    private String examsPeriodStartDate;
    private String examsPeriodEndDate;
    private boolean forSoldiers;

    @Override
    public String toString() {
        return "Condition{" +
                "conditionName='" + conditionName + '\'' +
                ", daysBetweenExams=" + daysBetweenExams +
                ", gapFirstAndSecondExam=" + gapFirstAndSecondExam +
                ", minMorningHour=" + minMorningHour +
                ", minAfternoonHour=" + minAfternoonHour +
                ", examsPeriodStartDate='" + examsPeriodStartDate + '\'' +
                ", examsPeriodEndDate='" + examsPeriodEndDate + '\'' +
                '}';
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }
    public int getDaysBetweenExams() {
        return daysBetweenExams;
    }

    public void setDaysBetweenExams(int daysBetweenExams) {
        this.daysBetweenExams = daysBetweenExams;
    }

    public int getGapFirstAndSecondExam() {
        return gapFirstAndSecondExam;
    }

    public void setGapFirstAndSecondExam(int gapFirstAndSecondExam) {
        this.gapFirstAndSecondExam = gapFirstAndSecondExam;
    }
    public int getMinMorningHour() {
        return minMorningHour;
    }

    public void setMinMorningHour(int minMorningHour) {
        this.minMorningHour = minMorningHour;
    }

    public int getMinAfternoonHour() {
        return minAfternoonHour;
    }

    public void setMinAfternoonHour(int minAfternoonHour) {
        this.minAfternoonHour = minAfternoonHour;
    }

    public String getExamsPeriodStartDate() {
        return examsPeriodStartDate;
    }

    public void setExamsPeriodStartDate(String examsPeriodStartDate) {
        this.examsPeriodStartDate = examsPeriodStartDate;
    }

    public String getExamsPeriodEndDate() {
        return examsPeriodEndDate;
    }

    public void setExamsPeriodEndDate(String examsPeriodEndDate) {
        this.examsPeriodEndDate = examsPeriodEndDate;
    }

    public boolean isForSoldiers() {
        return forSoldiers;
    }

    public void setForSoldiers(boolean forSoldiers) {
        this.forSoldiers = forSoldiers;
    }
}

package com.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.Exam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamsSchedule {

    private List<Exam> exams;
    private List<String> reports;

    public ExamsSchedule() {
        exams = new ArrayList<>();
        reports = new ArrayList<>();
    }

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    public List<String> getReports() {
        return reports;
    }

    public void setReports(List<String> reports) {
        this.reports = reports;
    }
    @JsonIgnore
    public void generateReports() {
        for (Exam exam : exams) {
            if (!exam.isRequiredDaysAccomplished()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Examination in the course of ").append(exam.getCourseName());
                sb.append(exam.isSecondExam() ? " (second exam)":"").append(" got ").append(exam.getDaysToStudy()).append(" days to study instead of ");
                sb.append(exam.getRequiredDaysToStudy()).append(" days");
                reports.add(sb.toString());
            }
            if (exam.isSecondExam() && ! exam.isSecondExamPreferredDateAccomplished()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Retest in the course of ").append(exam.getCourseName());
                sb.append(" (second exam) is scheduled to ").append(exam.getScheduledDate());
                sb.append(" but the preferred schedule date is not earlier than ").append(exam.getPreferredSecondDate());
                reports.add(sb.toString());
            }
        }
    }
}

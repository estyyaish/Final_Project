package com.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.db.MySqlDBClient;
import com.project.xsl.XslFileReader;
import com.project.xsl.XslFileWriter;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

public class ExamScheduleCalculator {
    private static int MAX_EXAM_DURATION = 5;
    private static int MIN_MORNING_START_HOUR = 9;
    private static int MAX_MORNING_END_HOUR = 15;
    private static int MIN_EVENING_START_HOUR = 14;
    private static int MAX_EVENING_END_HOUR = 20;

    // 87.68.225.43:4200

    private static final Condition theCondition = new Condition();
    static {
        theCondition.setConditionName("Schedule_1");
        theCondition.setDaysBetweenExams(4);
        theCondition.setExamsPeriodStartDate("2022-01-06");
        theCondition.setExamsPeriodEndDate("2022-07-01");
        theCondition.setGapFirstAndSecondExam(30);
        theCondition.setMinAfternoonHour(14);
        theCondition.setMinMorningHour(9);
    }



    private static boolean isLoaded = false;

    private static ExamScheduleCalculator theInstance = new ExamScheduleCalculator();

    public static ExamScheduleCalculator getTheInstance() {
        return theInstance;
    }

    protected HashMap<String, Condition> conditionMap = new HashMap<>();
    protected Condition condition;
    protected HashMap<String, Course> courseHashMap = new HashMap<>();
    private HashMap<String, Exam> firstExamHashMap = new HashMap<>();
    private HashMap<String, Exam> secondExamHashMap = new HashMap<>();
    protected List<Course> allCourses;
    private List<Exam> allFirstExams = new ArrayList<>();
    private List<Exam> allSecondExams = new ArrayList<>();
    private int totalDaysToStudy;

    private HashMap<String, List<Exam>> sameNameFirstExams = new HashMap<>();
    private HashMap<String, List<Exam>> sameNameSecondExams = new HashMap<>();

    protected void loadConditionFromDB(String conditionName) {
        condition = MySqlDBClient.getCondition(conditionName);
        conditionMap.put(condition.getConditionName(), condition);
    }

    protected void loadAllCoursesFromXsl() {
        courseHashMap.clear();
        allCourses = XslFileReader.getInstance().getCourses();
        for (Course course : allCourses) {
            courseHashMap.put(course.getName(), course);
        }
    }

    private int getExamStartHour(int minStart, int maxEnd, int examDuration) {
        int toAdd = (int) ((maxEnd - minStart - 2) * Math.random());
        int latestHour = maxEnd - examDuration;
        int hour = Math.min(minStart + toAdd, latestHour);
        return hour;
    }

    private void initExams(Condition condition) {
        totalDaysToStudy = 0;
        firstExamHashMap.clear();
        secondExamHashMap.clear();
        allFirstExams.clear();
        allSecondExams.clear();

        for (Course course : allCourses) {
            //  add to <totalDaysToStudy> days for first exam and for second exam
            Exam exam1 = new Exam(course, false);
            Exam exam2 = new Exam(course, true);
            exam1.setRequiredDaysToStudy(course.getDaysToStudy());
            exam2.setRequiredDaysToStudy(course.getDaysToStudy());
            int startTime;
            if (course.isMorningCourse()) {
                startTime = getExamStartHour(MIN_MORNING_START_HOUR, MAX_MORNING_END_HOUR, course.getTestDurationHours());
            } else {
                startTime = getExamStartHour(MIN_EVENING_START_HOUR, MAX_EVENING_END_HOUR, course.getTestDurationHours());
            }
            String scheduleTime = startTime + ":00";
            exam1.setScheduledTime(scheduleTime);
            exam2.setScheduledTime(scheduleTime);
            if (firstExamHashMap.containsKey(course.getName())) {
                List<Exam> sameName1List = sameNameFirstExams.get(course.getName());
                if (sameName1List == null) {
                    sameName1List = new ArrayList<>();
                    sameNameFirstExams.put(course.getName(), sameName1List);
                }
                sameName1List.add(exam1);
                List<Exam> sameName2List = sameNameSecondExams.get(course.getName());
                if (sameName2List == null) {
                    sameName2List = new ArrayList<>();
                    sameNameSecondExams.put(course.getName(), sameName2List);
                }
                sameName2List.add(exam2);

                continue;
                //throw new ApplicationException("The course " + course.getName() + " appears more then once in courses list");
            }
            totalDaysToStudy += course.getDaysToStudy() * 2;
            firstExamHashMap.put(course.getName(), exam1);
            secondExamHashMap.put(course.getName(), exam2);
            allFirstExams.add(exam1);
            allSecondExams.add(exam2);
        }
    }

    public void load() {
        // TODO integration patch
        if (!isLoaded) {
            List<Condition> conditions = MySqlDBClient.getAllConditions();
            for (Condition condition : conditions) {
                conditionMap.put(condition.getConditionName(), condition);
            }
            conditionMap.put(theCondition.getConditionName(), theCondition);
            condition = theCondition;
            isLoaded = true;
        }
    }

    public List<Condition> getAllConditions() {
        return new ArrayList<>(conditionMap.values());
    }

    public Condition getCondition(String name) {
        return conditionMap.get(name);
    }

    public String getCurrentConditionName() {
        return condition.getConditionName();
    }

    public void handleConditionDB(Condition condition) {
        // TODO integration patch
        if (conditionMap.containsKey(condition.getConditionName())) {
            System.out.println("Schedule " + condition.getConditionName() + " updated: " + condition);
            MySqlDBClient.updateCondition(condition);
        } else {
            System.out.println("Schedule " + condition.getConditionName() + " added: " + condition);
            MySqlDBClient.insertCondition(condition);
        }
        conditionMap.put(condition.getConditionName(), condition);
    }

//    public ExamsSchedule calculateSchedule(String conditionName) {
//        return calculateSchedule(conditionMap.get(conditionName));
//    }

    public ExamsSchedule calculateSchedule(Condition condition) {
        System.out.println("Start calculating schedule " + condition.getConditionName());
        this.condition = condition;
        ArrayList<Exam> examsResult = new ArrayList<>();
        // Initialization:
        loadAllCoursesFromXsl();
        if (allCourses.isEmpty()) {
            throw new ApplicationException("No courses file found or current file is empty");
        }

        System.out.println("Courses list loaded to local map from excel loader");
        initExams(condition);
        Date examsPeriodStart = ExamCalendar.parse(condition.getExamsPeriodStartDate());
        Date examsPeriodEnd = ExamCalendar.parse(condition.getExamsPeriodEndDate());
        if (examsPeriodStart.getTime() >= examsPeriodEnd.getTime()) {
            throw new ApplicationException("Schedule end date is not later than schedule start date");
        }
        // TODO maybe we have to subtract the number of first and second exams from
        // TODO <totalPeriodDays>, so the day of the exam-date is not considered as a day that you are able to study,
        // TODO like this: totalPeriodDays -= (totalPeriodDays - allFirstExams.size() - allSecondExams.size())
        int totalPeriodDays = ExamCalendar.calculateNumberOfActualDays(examsPeriodStart, examsPeriodEnd, true);
        totalPeriodDays = (totalPeriodDays - allFirstExams.size() - allSecondExams.size());
        if (totalPeriodDays <= 0) {
            throw new ApplicationException("Selected period has not enough days to place the exams");
        }

        System.out.println("Total exams period days " + totalPeriodDays);
        // START CALCULATING
        // =================
        // Assign to each exam its relative portion from <totalPeriodDays>:
        TreeSet<Exam> examsOrderedByRequiredStudyDays = new TreeSet<>(new Comparator<Exam>() {
            @Override
            public int compare(Exam o1, Exam o2) {
                int requiredDaysCompare = o1.getRequiredDaysToStudy() - o2.getRequiredDaysToStudy();
                if (requiredDaysCompare != 0) {
                    return requiredDaysCompare;
                }
                int nameCompare = o1.getCourseName().compareTo(o2.getCourseName());
                if (nameCompare != 0 || o1.isSecondExam() == o2.isSecondExam()) {
                    return nameCompare;
                }

                return o1.isSecondExam() ? 1 : -1;
            }
        });
        int totalAssignedDays = 0;
        for (Exam exam : allFirstExams) {
            float relativePortion = ((float) totalPeriodDays / (float) totalDaysToStudy) * (float) exam.getRequiredDaysToStudy();
            exam.setDaysToStudy((int) relativePortion);
            // handle same name exams
            if (sameNameFirstExams.containsKey(exam.getCourseName())) {
                for (Exam firstExam : sameNameFirstExams.get(exam.getCourseName())) {
                    firstExam.setDaysToStudy(exam.getDaysToStudy());
                }
            }
            totalAssignedDays += exam.getDaysToStudy();
            examsOrderedByRequiredStudyDays.add(exam);
        }
        for (Exam exam : allSecondExams) {
            float relativePortion = ((float) totalPeriodDays / (float) totalDaysToStudy) * (float) exam.getRequiredDaysToStudy();
            exam.setDaysToStudy((int) relativePortion);
            // handle same name exams
            if (sameNameSecondExams.containsKey(exam.getCourseName())) {
                for (Exam secondExam : sameNameSecondExams.get(exam.getCourseName())) {
                    secondExam.setDaysToStudy(exam.getDaysToStudy());
                }
            }
            totalAssignedDays += exam.getDaysToStudy();
            examsOrderedByRequiredStudyDays.add(exam);
        }
        // Take remaining days (residue of the casting from float to int) and divide them between exams:
        int remainingDays = totalPeriodDays - totalAssignedDays;
        Iterator<Exam> iterator = examsOrderedByRequiredStudyDays.iterator();
        while (remainingDays > 0) {
            if (!iterator.hasNext()) {
                iterator = examsOrderedByRequiredStudyDays.iterator();
            }
            Exam exam = iterator.next();
            if (!exam.isRequiredDaysAccomplished()) {
                exam.setDaysToStudy(exam.getDaysToStudy() + 1);
                if (! exam.isSecondExam()) {
                    // handle same name exams
                    if (sameNameFirstExams.containsKey(exam.getCourseName())) {
                        for (Exam firstExam : sameNameFirstExams.get(exam.getCourseName())) {
                            firstExam.setDaysToStudy(exam.getDaysToStudy());
                        }
                    }
                } else {
                    // handle same name exams
                    if (sameNameSecondExams.containsKey(exam.getCourseName())) {
                        for (Exam secondExam : sameNameSecondExams.get(exam.getCourseName())) {
                            secondExam.setDaysToStudy(exam.getDaysToStudy());
                        }
                    }
                }
            }
            remainingDays --;
        }
        // Assign first exams to calendar:
        Date lastExamDate = ExamCalendar.getActualDateFrom(examsPeriodStart, -1, false);
        for (Exam exam : allFirstExams) {
            // set a schedule to first exams
            int daysToExamDate = exam.getDaysToStudy() + 1;
            lastExamDate = ExamCalendar.getActualDateFrom(lastExamDate, daysToExamDate, true);
            exam.setScheduledDateObject(lastExamDate);
            exam.setScheduledDate(ExamCalendar.format(lastExamDate));
            examsResult.add(exam);
            // handle same name exams
            if (sameNameFirstExams.containsKey(exam.getCourseName())) {
                for (Exam firstExam : sameNameFirstExams.get(exam.getCourseName())) {
                    firstExam.setScheduledDateObject(lastExamDate);
                    firstExam.setScheduledDate(ExamCalendar.format(lastExamDate));
                    examsResult.add(firstExam);
                }
            }

            // set the preferred date to second exam by the condition's <gapFirstAndSecondExam>:
            Exam exam2 = secondExamHashMap.get(exam.getCourseName());
            Date preferredSecondDate = ExamCalendar.getValidDateFrom(lastExamDate, condition.getGapFirstAndSecondExam(), true);
            exam2.setPreferredSecondDateObject(preferredSecondDate);
            exam2.setPreferredSecondDate(ExamCalendar.format(preferredSecondDate));
        }

        // Assign second exams to calendar:
        Date examsPeriodEndDate = ExamCalendar.parse(condition.getExamsPeriodEndDate());
        Date secondExamDate = ExamCalendar.getValidDateFrom(examsPeriodEndDate, 0, false);
        int indexToInsert = examsResult.size();
        // here we assign the dates from the last and backwards, that is why we find the exam date by the previous
        // exam's <daysToStudy>, also we insert the second-exams in a way that they will be in a chronological order:
        for (int i = allSecondExams.size(); i -- > 0;) {
            Exam exam2 = allSecondExams.get(i);
            exam2.setScheduledDateObject(secondExamDate);
            exam2.setScheduledDate(ExamCalendar.format(secondExamDate));
            examsResult.add(indexToInsert, exam2);
            // handle same name exams
            if (sameNameSecondExams.containsKey(exam2.getCourseName())) {
                for (Exam secondExam : sameNameFirstExams.get(exam2.getCourseName())) {
                    secondExam.setScheduledDateObject(lastExamDate);
                    secondExam.setScheduledDate(ExamCalendar.format(lastExamDate));
                    examsResult.add(secondExam);
                }
            }
            // here we put (exam2.getDaysToStudy() + 1) because the exam day is not a study day so we add 1 for the exam day
            secondExamDate = ExamCalendar.getActualDateFrom(secondExamDate, (exam2.getDaysToStudy() + 1) * -1, false);
        }

        ExamsSchedule examsSchedule = new ExamsSchedule();
        examsSchedule.setExams(examsResult);
        examsSchedule.generateReports();
        XslFileWriter.getTheInstance().generateFile(condition, examsSchedule);
        return examsSchedule;
    }

    public static void main(String[] args) {
        XslFileReader.getInstance().loadFile("xls_file" + File.separator + "many courses.xlsx");
        List<Course> loadedCourses = XslFileReader.getInstance().getCourses();

        int NUMBER_OF_COURSES = 7;
        String COURSE_NAME_PREFIX = "Course";
        ArrayList<Course> courses = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_COURSES; i ++) {
            Course course = new Course();
            course.setName(COURSE_NAME_PREFIX + i);
            course.setDaysToStudy((int) (Math.random() * 6) + 1);
            courses.add(course);
        }
        ExamScheduleCalculator calculator = new ExamScheduleCalculator() {
//            @Override
//            protected void loadConditionFromDB(String conditionName) {
//                this.condition = theCondition;
//            }

            @Override
            protected void loadAllCoursesFromXsl() {
                conditionMap.put(theCondition.getConditionName(), theCondition);
                courseHashMap.clear();
//                allCourses = courses;
                allCourses = loadedCourses;
                for (Course course : allCourses) {
                    courseHashMap.put(course.getName(), course);
                }
            }
        };

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            System.out.println(objectMapper.writeValueAsString(theCondition));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ExamsSchedule examsSchedule = calculator.calculateSchedule(theCondition);
        for (Exam exam : examsSchedule.getExams()) {
            System.out.println("Exam " + exam.getCourseName() + (exam.isSecondExam() ? " (second): " : ": ") + exam.getScheduledDate() +
                    " ( " + exam.getDaysToStudy() + " / " + exam.getRequiredDaysToStudy() +" )");
        }
        System.out.println("REPORTS:");
        for (String report : examsSchedule.getReports()) {
            System.out.println(report);
        }
//        try {
//            String resultString = objectMapper.writeValueAsString(examsSchedule);
//            JSONObject jsonObject = new JSONObject(resultString);
//
//            System.out.println(jsonObject.toString(4));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        for (Course course : calculator.allCourses) {
            int startTime;
            if (course.isMorningCourse()) {
                startTime = calculator.getExamStartHour(MIN_MORNING_START_HOUR, MAX_MORNING_END_HOUR, course.getTestDurationHours());
            } else {
                startTime = calculator.getExamStartHour(MIN_EVENING_START_HOUR, MAX_EVENING_END_HOUR, course.getTestDurationHours());
            }
            String scheduleTime = startTime + ":00";
            System.out.println((course.isMorningCourse() ? "MORNING" : "EVENING") + " " + scheduleTime);
        }


    }
}

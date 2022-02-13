package com.project.xsl;

import com.project.Course;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class XslFileReader {

    public static final String COURSE_NAME_COLUMN = "COURSE_NAME";
    public static final String MORNING_EVENING_COLUMN = "MORNING/EVENING";
    public static final String DAYS_TO_STUDY_COLUMN = "DAYS_TO_STUDY";
    public static final String TEST_DURATION_COLUMN = "TEST_DURATION_HOURS";

    public static final String MORNING_VALUE = "MORNING";
    public static final String EVENING_VALUE = "EVENING";

    private int courseNameColumn = -1;
    private int morningEveningColumn = -1;
    private int daysToStudyColumn = -1;
    private int testDurationColumn = -1;

    private boolean courseNameValueAssigned = false;
    private boolean morningEveningValueAssigned = false;
    private boolean daysToStudyValueAssigned = false;
    private boolean testDurationValueAssigned = false;

    private Course currentBeingReadCourse;
    private List<Course> courses;

    private static final XslFileReader theInstance = new XslFileReader();
    private XslFileReader() {

    }
    public static XslFileReader getInstance() {
        return theInstance;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void loadFile(String path) {
        System.out.println("Excel file reader loading file at " + path);
        startReadingNewFile();
        startReadingNewCourse();
        courses = new ArrayList<>();
        try {
            FileInputStream fis =new FileInputStream(path);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            System.out.println("Excel workbook loaded");
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0; // No of columns
            int tmp = 0;

            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for(int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if(row != null) {
                    tmp = row.getPhysicalNumberOfCells();
                    if(tmp > cols) cols = tmp;
                }
            }



            // first we reveal the indices of the columns
            for(int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                if(row != null) {
                    for(int c = 0; c < cols; c++) {
                        cell = row.getCell((short)c);
                        if(cell != null) {
                            // Your code here
                            if (!isAllColumnsFound()) {
                                String value = cell.getStringCellValue();
                                if (value != null && value.trim().length() > 0) {
                                    switch (value) {
                                        case COURSE_NAME_COLUMN:
                                            System.out.println("Excel file: column found " + COURSE_NAME_COLUMN);
                                            courseNameColumn = c;
                                            break;
                                        case MORNING_EVENING_COLUMN:
                                            System.out.println("Excel file: column found " + MORNING_EVENING_COLUMN);
                                            morningEveningColumn = c;
                                            break;
                                        case TEST_DURATION_COLUMN:
                                            System.out.println("Excel file: column found " + TEST_DURATION_COLUMN);
                                            testDurationColumn = c;
                                            break;
                                        case DAYS_TO_STUDY_COLUMN:
                                            System.out.println("Excel file: column found " + DAYS_TO_STUDY_COLUMN);
                                            daysToStudyColumn = c;
                                            break;
                                    }
                                }
                            } else {
                                if (currentBeingReadCourse == null) {
                                    // we are starting to read a new course
                                    currentBeingReadCourse = new Course();
                                }
                                if (courseNameColumn == c) {
                                    String value = getStringValue(cell);
                                    currentBeingReadCourse.setName(value);
                                    courseNameValueAssigned = true;
                                } else if (morningEveningColumn == c) {
                                    String value = getStringValue(cell);
                                    switch (value) {
                                        case MORNING_VALUE:
                                            currentBeingReadCourse.setMorningCourse(true);
                                            morningEveningValueAssigned = true;
                                            break;
                                        case EVENING_VALUE:
                                            currentBeingReadCourse.setMorningCourse(false);
                                            morningEveningValueAssigned = true;
                                            break;
                                    }
                                } else if (daysToStudyColumn == c) {
                                    double value = getNumericValue(cell);
                                    currentBeingReadCourse.setDaysToStudy((int)value);
                                    daysToStudyValueAssigned = true;
                                } else if (testDurationColumn == c) {
                                    double value = getNumericValue(cell);
                                    currentBeingReadCourse.setTestDurationHours((int)value);
                                    testDurationValueAssigned = true;
                                }
                                if (isCourseObjectFinishRead()) {
                                    courses.add(currentBeingReadCourse);
                                    System.out.println("Excel file: Course read from excel - " + currentBeingReadCourse);
                                    startReadingNewCourse();
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private double getNumericValue(XSSFCell cell) {
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return Double.valueOf(cell.getStringCellValue());
        }
    }

    private String getStringValue(XSSFCell cell) {
        try {
            return cell.getStringCellValue();
        } catch (Exception e) {
            return String.valueOf(cell.getNumericCellValue());
        }
    }

    private boolean isAllColumnsFound() {
        return courseNameColumn >= 0 && testDurationColumn >= 0 && morningEveningColumn >= 0 && daysToStudyColumn >= 0;
    }

    private boolean isCourseObjectFinishRead() {
        return courseNameValueAssigned && morningEveningValueAssigned && daysToStudyValueAssigned && testDurationValueAssigned;
    }

    private void startReadingNewCourse() {
        courseNameValueAssigned = false;
        morningEveningValueAssigned = false;
        daysToStudyValueAssigned = false;
        testDurationValueAssigned = false;
        currentBeingReadCourse = null;
    }

    private void startReadingNewFile() {
        courseNameColumn = -1;
        morningEveningColumn = -1;
        daysToStudyColumn = -1;
        testDurationColumn = -1;
    }
}

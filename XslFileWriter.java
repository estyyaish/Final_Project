package com.project.xsl;

import com.project.Condition;
import com.project.Exam;
import com.project.ExamsSchedule;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class XslFileWriter {

    public static final String SCHEDULE_FILE_NAME = "xls_file" + File.separator + "schedule.xlsx";

    private static final String COURSE_NAME_COL = "שם הקורס";
    private static final String DATE_COL = "תאריך";
    private static final String TIME_COL = "שעה";
    private static final String DURATION_COL = "משך המבחן";
    private static final String TRIAL_COL = "מועד";
    private static final List<String> COLUMNS = new ArrayList<>();
    static {
        COLUMNS.add(COURSE_NAME_COL);
        COLUMNS.add(DATE_COL);
        COLUMNS.add(TIME_COL);
        COLUMNS.add(DURATION_COL);
        COLUMNS.add(TRIAL_COL);
    }


    private static final XslFileWriter theInstance = new XslFileWriter();

    public static XslFileWriter getTheInstance() {
        return theInstance;
    }

    private XslFileWriter(){}

    public void generateFile(Condition scheduleParams, ExamsSchedule schedule) {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(scheduleParams.getConditionName() + " " + "לוח מבחנים");

        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"ID", "NAME", "LASTNAME"});
        data.put("2", new Object[] {1, "Amit", "Shukla"});
        data.put("3", new Object[] {2, "Lokesh", "Gupta"});
        data.put("4", new Object[] {3, "John", "Adwards"});
        data.put("5", new Object[] {4, "Brian", "Schultz"});

        int rowIndex = 0;

        // Write the schedule
        // create table header
        Row headerRow = sheet.createRow(rowIndex ++);
        CellStyle styleCenter = workbook.createCellStyle();
        styleCenter.setAlignment(HorizontalAlignment.CENTER);

        CellStyle styleHeader = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setFontHeight((short) (headerFont.getFontHeight() + 2));
        headerFont.setBold(true);
        styleHeader.setFont(headerFont);
        styleHeader.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        styleHeader.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < COLUMNS.size(); i ++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMNS.get(i));
            cell.setCellStyle(styleHeader);
        }
        // fill table with exams
        for (Exam exam : schedule.getExams()) {
            // course name
            Row dataRow = sheet.createRow(rowIndex ++);
            Cell nameCell = createCell(dataRow, COLUMNS.indexOf(COURSE_NAME_COL), styleCenter);
            nameCell.setCellValue(exam.getCourseName());
            // date
            Cell dateCell = createCell(dataRow, COLUMNS.indexOf(DATE_COL), styleCenter);
            dateCell.setCellValue(exam.getScheduledDate());
            // time
            Cell timeCell = createCell(dataRow, COLUMNS.indexOf(TIME_COL), styleCenter);
            if (scheduleParams.isForSoldiers()) {
                int hour = scheduleParams.getMinAfternoonHour();
                timeCell.setCellValue(hour + ":00");
            } else {
                int hour = scheduleParams.getMinMorningHour();
                timeCell.setCellValue(hour + ":00");
            }

            // duration
            Cell durationCell = createCell(dataRow, COLUMNS.indexOf(DURATION_COL), styleCenter);
            durationCell.setCellValue(exam.getTestDurationHours());
            // trial
            Cell trialCell = createCell(dataRow, COLUMNS.indexOf(TRIAL_COL), styleCenter);
            if (exam.isSecondExam()) {
                trialCell.setCellValue("ב'");
            } else {
                trialCell.setCellValue("א'");
            }

        }

        if (!schedule.getReports().isEmpty()) {
            Row titleRow = sheet.createRow(rowIndex ++);
            Cell titleCell = createCell(titleRow, 0, styleHeader);
            titleCell.setCellValue("הודעות");
            for (String report : schedule.getReports()) {
                Cell reportCell = sheet.createRow(rowIndex ++).createCell(0);
                reportCell.setCellValue(report);
            }
        }

        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(SCHEDULE_FILE_NAME));
            workbook.write(out);
            out.close();
            System.out.println(SCHEDULE_FILE_NAME + " written successfully on disk.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private Cell createCell(Row row, int cellIndex, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);
        return cell;
    }
}

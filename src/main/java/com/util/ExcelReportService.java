package com.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ExcelReportService {

    @Value("${db.record.list}")
    private List<String> dbRecordList;

    public List<ReportBean> processExel(String filePath) throws IOException {
        ReportBean reportBean;
        List<ReportBean> data = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream("C:\\Users\\Admin\\Downloads\\export (7).xlsx"))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                reportBean = new ReportBean();

                for (Cell cell : row) {
                    int cellNumber = cell.getColumnIndex();

                    if (cellNumber == 6 && cell.getCellType() == CellType.STRING) {
                        reportBean.setOutcome(cell.getStringCellValue().trim());
                    }
                    if (cellNumber == 12 && cell.getCellType() == CellType.NUMERIC) {
                        reportBean.setEtrmId(NumberToTextConverter.toText(cell.getNumericCellValue()));
                    }
                }
                data.add(reportBean);
            }
        }
        return data;
    }

    public void process(String filePath) throws IOException {
        List<ReportBean> data = processExel(filePath);
        Set<Integer> fail = new HashSet<>();
        Set<Integer> process = new HashSet<>();
        Set<Integer> filter = new HashSet<>();
        Set<Integer> notFound = new HashSet<>();

        if (data != null && !data.isEmpty()) {
            for (ReportBean reportBean : data) {
                if (dbRecordList.contains(reportBean.getEtrmId())) {
                    if ("Failed".equalsIgnoreCase(reportBean.getOutcome())) {
                        fail.add(Integer.parseInt(reportBean.getEtrmId()));
                    } else if ("Processed".equalsIgnoreCase(reportBean.getOutcome())) {
                        process.add(Integer.parseInt(reportBean.getEtrmId()));
                    } else if ("Filtered".equalsIgnoreCase(reportBean.getOutcome())) {
                        filter.add(Integer.parseInt(reportBean.getEtrmId()));
                    }
                }
            }
        }

        filter.removeAll(process);

        for (String id : dbRecordList) {
            boolean found = false;
            for (ReportBean bean : data) {
                if (bean.getEtrmId() != null && bean.getEtrmId().equals(id)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                notFound.add(Integer.valueOf(id));
            }
        }

        System.out.println("Failed: " + fail);
        System.out.println("Processed: " + process);
        System.out.println("Filtered: " + filter);
        System.out.println("Not Found: " + notFound);
    }

    public static List<Map<String, String>> readExcel(MultipartFile file) throws IOException {
        List<Map<String, String>> excelData = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String cellValue = "";

                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            cellValue = NumberToTextConverter.toText(cell.getNumericCellValue());
                            break;
                        default:
                            cellValue = "";
                    }
                }
                rowData.put(headers.get(j), cellValue);
            }
            excelData.add(rowData);
        }

        workbook.close();
        return excelData;
    }
}
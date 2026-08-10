package com.birthdayposter.service;

import com.birthdayposter.dto.PersonRequest;
import com.birthdayposter.exception.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType()) || file.getOriginalFilename().endsWith(".xlsx");
    }

    public List<PersonRequest> parseExcelFile(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<PersonRequest> personRequests = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header row
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                PersonRequest person = new PersonRequest();

                int cellIdx = 0;
                boolean hasContent = false;

                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    cellIdx = currentCell.getColumnIndex();

                    switch (cellIdx) {
                        case 0: // Name
                            String name = getCellValueAsString(currentCell);
                            if (name != null && !name.trim().isEmpty()) {
                                person.setName(name.trim());
                                hasContent = true;
                            }
                            break;

                        case 1: // Email
                            String email = getCellValueAsString(currentCell);
                            if (email != null && !email.trim().isEmpty()) {
                                person.setEmail(email.trim());
                            }
                            break;

                        case 2: // DOB
                            LocalDate dob = parseDateCell(currentCell);
                            if (dob != null) {
                                person.setDob(dob);
                            }
                            break;

                        case 3: // Phone
                            person.setPhone(getCellValueAsString(currentCell));
                            break;

                        case 4: // Department
                            person.setDepartment(getCellValueAsString(currentCell));
                            break;

                        case 5: // Designation
                            person.setDesignation(getCellValueAsString(currentCell));
                            break;

                        case 6: // Relationship
                            person.setRelationship(getCellValueAsString(currentCell));
                            break;

                        default:
                            break;
                    }
                }

                if (hasContent && person.getName() != null && person.getEmail() != null && person.getDob() != null) {
                    personRequests.add(person);
                }

                rowNumber++;
            }

            workbook.close();
            return personRequests;

        } catch (Exception e) {
            throw new BadRequestException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private LocalDate parseDateCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            String dateStr = cell.getStringCellValue().trim();
            try {
                return LocalDate.parse(dateStr); // YYYY-MM-DD
            } catch (Exception e1) {
                try {
                    return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e2) {
                    try {
                        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    } catch (Exception e3) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}

package com.birthdayposter.service;

import com.birthdayposter.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceTest {

    private final ExcelService excelService = new ExcelService();

    @Test
    void hasExcelFormat_ValidFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "test data".getBytes()
        );

        assertTrue(excelService.hasExcelFormat(file));
    }

    @Test
    void hasExcelFormat_InvalidFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test data".getBytes()
        );

        assertFalse(excelService.hasExcelFormat(file));
    }

    @Test
    void parseExcelFile_InvalidDataThrowsException() {
        InputStream invalidInputStream = new ByteArrayInputStream("invalid data".getBytes());
        
        assertThrows(BadRequestException.class, () -> excelService.parseExcelFile(invalidInputStream));
    }
}

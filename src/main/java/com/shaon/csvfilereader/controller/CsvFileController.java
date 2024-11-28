package com.shaon.csvfilereader.controller;

import com.shaon.csvfilereader.service.CsvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class CsvFileController {

    public CsvFileController(CsvService csvService) {
        this.csvService = csvService;
    }

    private final CsvService csvService;



    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload", ".csv");
        file.transferTo(tempFile);

        csvService.processCsv(tempFile);

        return ResponseEntity.ok("File processing started!");
    }
}

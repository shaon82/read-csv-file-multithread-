package com.shaon.csvfilereader.controller;

import com.shaon.csvfilereader.service.CsvService;
import com.shaon.csvfilereader.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class CsvFileController {

    private final UserService userService;

    public CsvFileController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        userService.createUserByCsvFile(multipartFile);

        return ResponseEntity.ok("File processing started!");
    }
}

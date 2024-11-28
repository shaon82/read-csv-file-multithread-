package com.shaon.csvfilereader.service;


import com.shaon.csvfilereader.model.User;
import com.shaon.csvfilereader.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    private final UserRepository userRepository;

    public CsvService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Async
    public void processCsv(File file) {
        System.out.println(Thread.currentThread().getName());
        List<User> users = new ArrayList<>();
        try (Reader reader = new BufferedReader(new FileReader(file))){

            String firstLine = ((BufferedReader) reader).readLine();
            if (firstLine != null && firstLine.startsWith("\uFEFF")) {
                firstLine = firstLine.substring(1); // Remove BOM
            }
            CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT.withHeader(firstLine.split(",")));
            for (CSVRecord record : csvParser) {
                User user = new User();
                user.setName(record.get("name"));
                user.setEmail(record.get("email"));
                user.setPhone(record.get("phone"));
                users.add(user);

                // Batch save every 1000 records
                if (users.size() == 1000) {
                    userRepository.saveAll(users);
                    users.clear();
                }
            }
            // Save remaining records
            if (!users.isEmpty()) {
                userRepository.saveAll(users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

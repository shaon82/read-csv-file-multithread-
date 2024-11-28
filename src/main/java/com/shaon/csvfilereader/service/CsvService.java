package com.shaon.csvfilereader.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CsvService<T> {

    private static final Logger log = LoggerFactory.getLogger(CsvService.class);
    private final Class<T> type;

    public CsvService(Class<T> type) {
        this.type = type;
    }

    @Async
    public List<T> processCsv(MultipartFile file) {

        Field[] fields = type.getDeclaredFields();


        List<T> resultList = new ArrayList<>();

        try {
            File tempFile = File.createTempFile("upload", ".csv");
            file.transferTo(tempFile);
            Reader reader = new BufferedReader(new FileReader(tempFile));

            String firstLine = ((BufferedReader) reader).readLine();
            if (firstLine != null && firstLine.startsWith("\uFEFF")) {
                firstLine = firstLine.substring(1); // Remove BOM
            }
            CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT.withHeader(firstLine.split(",")));

            if (fields.length != csvParser.getHeaderMap().size()){
                throw new RuntimeException("Object column and csv column not match!");
            }

            resultList = csvParser.stream().map(record ->{
                try {
                    T dto = type.getDeclaredConstructor().newInstance();

                    for (int i =0; i< fields.length; i++){
                        Field field = fields[i];

                        String csvValue = record.get(i);

                        field.setAccessible(true);

                        try {
                            if (field.getType().equals(String.class)){
                                field.set(dto,csvValue);
                            }else if (field.getType().equals(int.class)){
                                field.set(dto, Integer.parseInt(csvValue));
                            }else if (field.getType().equals(long.class)){
                                field.set(dto, Long.parseLong(csvValue));
                            }else if (field.getType().equals(double.class)){
                                field.set(dto, Double.parseDouble(csvValue));
                            }
                        }catch (Exception e){
                            log.error(e.getMessage());
                            log.error(csvValue+" unparsable value and unable to set into "+field.getName());
                            throw new RuntimeException(csvValue+" unable to set into "+field.getName());
                        }
                        field.setAccessible(false);
                    }

                    return dto;

                }catch (Exception e){
                    log.error(e.getMessage());
                    throw new RuntimeException("Error while mapping CSV to DTO");
                }

            }).collect(Collectors.toList());


        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return resultList;
    }
}

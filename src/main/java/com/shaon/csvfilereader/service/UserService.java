package com.shaon.csvfilereader.service;


import com.shaon.csvfilereader.model.dto.UserDTO;
import com.shaon.csvfilereader.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void createUserByCsvFile(MultipartFile multipartFile) {

        CsvService<UserDTO> csvService = new CsvService<>(UserDTO.class);
        List<UserDTO> dtos = csvService.processCsv(multipartFile);


//        convert dto to entity and save in database
        
    }
}

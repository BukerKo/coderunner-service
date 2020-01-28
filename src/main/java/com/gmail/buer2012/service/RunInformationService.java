package com.gmail.buer2012.service;

import com.gmail.buer2012.entity.RunInformation;
import com.gmail.buer2012.entity.User;
import com.gmail.buer2012.repository.RunInformationRepository;
import com.gmail.buer2012.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RunInformationService {
    RunInformationRepository runInformationRepository;
    UserRepository userRepository;
    
    public void saveOrUpdate(File fileWithSourceCode, User user) {
        Optional<RunInformation> runInformationFromDb = runInformationRepository.findByUserId(user.getId());
        if (runInformationFromDb.isPresent()) {
            RunInformation runInformation = runInformationFromDb.get();
            runInformation.setNumberOfTries(runInformation.getNumberOfTries() + 1);
            runInformation.setPathToLastAttempt(fileWithSourceCode.getAbsolutePath());
            runInformationRepository.save(runInformation);
        } else {
            runInformationRepository.save(new RunInformation(fileWithSourceCode.getAbsolutePath(), 0, user));
        }
    }
    
    public Optional<RunInformation> getByUser(User user) {
        return runInformationRepository.findByUserId(user.getId());
    }
}
package com.example.studyhyuck.study;

import com.example.studyhyuck.domain.Account;
import com.example.studyhyuck.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {

    @Autowired StudyService studyService;
    @Autowired StudyRepository studyRepository;

    public Study createStudy(String path, Account manager) {
        Study study = new Study();
        study.setPath(path);
        studyService.createNewStudy(study, manager);
        return study;
    }

}

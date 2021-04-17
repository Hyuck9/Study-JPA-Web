package com.example.studyhyuck.event;

import com.example.studyhyuck.domain.Account;
import com.example.studyhyuck.domain.Event;
import com.example.studyhyuck.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreateBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

}

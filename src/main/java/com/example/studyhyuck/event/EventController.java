package com.example.studyhyuck.event;

import com.example.studyhyuck.account.CurrentAccount;
import com.example.studyhyuck.domain.Account;
import com.example.studyhyuck.domain.Event;
import com.example.studyhyuck.domain.Study;
import com.example.studyhyuck.event.form.EventForm;
import com.example.studyhyuck.event.validator.EventValidator;
import com.example.studyhyuck.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }
}

package com.obu.ems.service;

import com.obu.ems.dto.TermResponse;
import com.obu.ems.model.Term;
import com.obu.ems.repository.TermRepository;
import com.obu.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final UserRepository userRepository;

    public TermResponse mapToTermResponse (Term term) {
        return TermResponse.builder()
                .termId(term.getTermId())
                .name(term.getName())
                .year(term.getYear())
                .semester(term.getSemester().toString())
                .isActive(term.getIsActive()).build();
    }

    //     list all terms
    public List<TermResponse> getAll()
    {
        // custom ordering by year ,semester
        List<Term> termResponseList = termRepository.findAll();
        return  termResponseList.stream()
                .map(this::mapToTermResponse)
                .toList();
    }

    //    get the currently active terms
    public TermResponse getActiveTerm(Boolean isActive) {
        Term term = termRepository.findActiveTerm()
                .orElseThrow(() -> new RuntimeException("Active term not found"));

        return mapToTermResponse(term);
    }


}

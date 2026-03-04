package com.obu.ems.mapper;

import com.obu.ems.dto.TermResponse;
import com.obu.ems.model.Term;
import org.springframework.stereotype.Component;

@Component
public class TermMapper {
    public TermResponse mapToTermResponse(Term term) {
        if (term == null) {
            return null;
        }
        return TermResponse.builder()
                .termId(term.getTermId())
                .name(term.getName())
                .year(term.getYear())
                .semester(term.getSemester() != null ? term.getSemester().name() : null)
                .isActive(term.getIsActive())
                .build();
    }
}

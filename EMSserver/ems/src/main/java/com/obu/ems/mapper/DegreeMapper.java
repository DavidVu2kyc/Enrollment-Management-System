package com.obu.ems.mapper;

import com.obu.ems.dto.DegreeResponse;
import com.obu.ems.model.Degree;
import org.springframework.stereotype.Component;

@Component
public class DegreeMapper {
    public DegreeResponse mapToDegreeResponse(Degree degree) {
        if (degree == null) {
            return null;
        }
        return DegreeResponse.builder()
                .degreeId(degree.getDegreeId())
                .name(degree.getName())
                .description(degree.getDescription())
                .build();
    }
}

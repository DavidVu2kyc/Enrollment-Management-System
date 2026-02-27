package com.obu.ems.service;

import com.obu.ems.dto.DegreeResponse;
import com.obu.ems.repository.CourseRepository;
import com.obu.ems.repository.DegreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DegreeService {

    private final DegreeRepository degreeRepository;
    private final CourseRepository courseRepository ;
    //  list all degrees
//    public DegreeResponse listalldegrees( )
//    {
//
//    }

    //  list courses for a degree
}

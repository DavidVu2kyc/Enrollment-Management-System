package com.obu.ems.service;

import com.obu.ems.dto.CourseResponse;
import com.obu.ems.dto.DegreeResponse;
import com.obu.ems.exception.ResourceNotFoundException;
import com.obu.ems.model.Course;
import com.obu.ems.model.Degree;
import com.obu.ems.repository.CourseRepository;
import com.obu.ems.repository.DegreeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DegreeService {

    private final DegreeRepository degreeRepository;
    private final CourseRepository courseRepository ;

    //degree -> degreeResponse mapper ( private )
    private DegreeResponse mapToResponse(Degree degree) {
        return DegreeResponse.builder()
                .degreeId(degree.getDegreeId())
                .name(degree.getName())
                .description(degree.getDescription())
                .build();
    }

    public CourseResponse maptoCourseResponse(Degree degree) {
        List<CourseResponse> courses = courseRepository.findByDegree_DegreeId(degree.getDegreeId())
                .stream()
                .map(course -> CourseResponse.builder()
                        .courseId(course.getCourseId())
                        .title(course.getTitle())
                        .build())
                .collect(Collectors.toList());

        return CourseResponse.builder()
                .courseId(degree.getDegreeId()) // Assuming you want to set the degree ID here
                .title(degree.getName()) // Assuming you want to set the degree name here
                .build();
    }

    //  list all degrees
   public List<DegreeResponse> getAll()
   {
        List<Degree> degreesResponses = degreeRepository.findAll();
      
        return degreesResponses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

   }

    private CourseResponse mapToCourseResponse(Course course) {
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .units(course.getUnits())
                .build();
    }

    //  list courses for a degree
    public List<CourseResponse> getCoursesByDegree( Long degreeId) 
        {
            // validate degree exists 
            Degree degree = degreeRepository.findByDegreeId(degreeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Degree not found with id: " + degreeId));

            // Check if the degree is still active ( optional )
            // fetch courses for the given degree
            List<Course> courses = courseRepository.findByDegree_DegreeId(degreeId);

            // Map to Response
            return courses.stream().map(this::mapToCourseResponse).toList();
        }

}

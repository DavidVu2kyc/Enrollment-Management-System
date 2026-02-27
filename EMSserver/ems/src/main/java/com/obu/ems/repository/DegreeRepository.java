package com.obu.ems.repository;

import com.obu.ems.model.Degree;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DegreeRepository extends JpaRepository<Degree, Long> {

    List<Degree>  findByDegreeId(Long degreeId);
}

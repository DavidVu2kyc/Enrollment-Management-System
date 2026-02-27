package com.obu.ems.repository;

import com.obu.ems.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByIsActiveTrue();
}

package com.obu.ems.repository;

import com.obu.ems.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

    // JPQL way
    @Query("SELECT t FROM Term t WHERE t.isActive = true")
    Optional<Term> findActiveTerm() ;


}

package com.myswcompany.demo.repositories;

import com.myswcompany.demo.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}

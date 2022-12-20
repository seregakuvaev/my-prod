package com.boots.repository;

import com.boots.entity.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;

@Transactional
public interface LogsRepository extends JpaRepository<Logs, Long> {
}


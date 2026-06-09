package com.cdas.repository;

import com.cdas.entity.DelayData;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DelayRepository
        extends JpaRepository<DelayData, Long> {
}
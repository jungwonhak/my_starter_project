package com.gmail.andreas.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gmail.andreas.backend.data.entity.HistoryItem;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
}

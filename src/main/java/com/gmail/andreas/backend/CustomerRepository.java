package com.gmail.andreas.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gmail.andreas.backend.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

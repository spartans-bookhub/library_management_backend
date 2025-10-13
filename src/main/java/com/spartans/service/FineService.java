package com.spartans.service;

import com.spartans.model.Fine;
import com.spartans.model.Transaction;

import java.util.List;

public interface FineService {
        Fine calculateFine(Transaction transaction);
        List<Fine> getOverdueFines(Long studentId);
}

package com.spartans.dto;

import java.util.List;
import java.util.Map;

public class BorrowBooksResponse {
  private List<BorrowedBookDTO> success;
  private Map<Long, String> failed; // bookId -> error message

  public BorrowBooksResponse(List<BorrowedBookDTO> success, Map<Long, String> failed) {
    this.success = success;
    this.failed = failed;
  }

  public List<BorrowedBookDTO> getSuccess() {
    return success;
  }

  public void setSuccess(List<BorrowedBookDTO> success) {
    this.success = success;
  }

  public Map<Long, String> getFailed() {
    return failed;
  }

  public void setFailed(Map<Long, String> failed) {
    this.failed = failed;
  }
}

package com.spartans.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class BorrowBooksRequest {

  @NotEmpty(message = "Book list cannot be empty.")
  @Size(max = 5, message = "You can borrow up to 5 books at a time.")
  private List<Long> bookIds;

  public List<Long> getBookIds() {
    return bookIds;
  }

  public void setBookIds(List<Long> bookIds) {
    this.bookIds = bookIds;
  }
}

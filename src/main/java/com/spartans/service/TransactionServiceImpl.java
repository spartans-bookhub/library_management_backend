package com.spartans.service;

import com.spartans.config.AdminReportConfig;
import com.spartans.config.LibraryConfig;
import com.spartans.config.TransactionStatusConfig;
import com.spartans.config.UserRoleConfig;
import com.spartans.dto.BorrowBooksResponse;
import com.spartans.dto.BorrowedBookDTO;
import com.spartans.exception.*;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import com.spartans.repository.BookRepository;
import com.spartans.repository.TransactionRepository;
import com.spartans.repository.UserRepository;
import com.spartans.util.UserContext;
import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired private BookRepository bookRepository;

  @Autowired private TransactionRepository transactionRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private NotificationService notificationService;

  @Autowired private LibraryConfig libraryConfig;

  @Autowired private UserRoleConfig userRoleConfig;

  @Autowired private TransactionStatusConfig transactionStatusConfig;

  @Autowired private AdminReportConfig config;

  @Override
  public Transaction borrowBook(Long userId, Long bookId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    if (!userRoleConfig.getStudent().equals(user.getUserAuth().getRole())) {
      throw new InvalidOperationException("Only students can borrow books");
    }

    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

    if (!isBookAvailable(bookId)) {
      throw new BookNotAvailableException("Book is not available for borrowing");
    }

    if (transactionRepository
        .findByUserAndBookAndTransactionStatus(user, book, transactionStatusConfig.getBorrowed())
        .isPresent()) {
      throw new BookAlreadyBorrowedException("You have already borrowed this book");
    }

    if (!canBorrowMoreBooks(userId)) {
      throw new BorrowLimitExceededException(
          "You have reached the maximum borrowing limit of "
              + libraryConfig.getMaxBorrowLimit()
              + " books");
    }

    Transaction transaction = new Transaction();
    transaction.setUser(user);
    transaction.setBook(book);
    transaction.setBorrowDate(LocalDate.now());
    transaction.setDueDate(LocalDate.now().plusDays(libraryConfig.getBorrowPeriodDays()));
    transaction.setTransactionStatus(transactionStatusConfig.getBorrowed());
    transaction.setFineAmount(0.0);

    // Update available copies
    book.setAvailableCopies(book.getAvailableCopies() - 1);
    bookRepository.save(book);

    Transaction savedTransaction = transactionRepository.save(transaction);

    notificationService.sendBookBorrowedNotification(user, book);

    return savedTransaction;
  }

    @Override
    public BorrowBooksResponse borrowMultipleBooks(Long userId, List<Long> bookIds) {
        List<BorrowedBookDTO> successList = new ArrayList<>();
        Map<Long, String> failedList = new HashMap<>();

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        long currentBorrowed = transactionRepository.countByUserAndTransactionStatus(
                user, transactionStatusConfig.getBorrowed());

        int maxLimit = libraryConfig.getMaxBorrowLimit();
        int requested = bookIds.size();

        if (currentBorrowed + requested > maxLimit) {
            throw new BorrowLimitExceededException(
                    "You are trying to borrow " + requested + " books, but you already have "
                            + currentBorrowed + " borrowed. Maximum allowed is " + maxLimit + ".");
        }

        for (Long bookId : bookIds) {
            try {
                Transaction transaction = borrowBook(userId, bookId);
                successList.add(toDTO(transaction));
            } catch (BookNotAvailableException e) {
                failedList.put(bookId, "Book is currently unavailable");
            } catch (BookAlreadyBorrowedException e) {
                failedList.put(bookId, "You have already borrowed this book");
            } catch (BorrowLimitExceededException e) {
                failedList.put(bookId, "User has reached max borrow limit");
            } catch (Exception e) {
                failedList.put(bookId, "Unexpected error: " + e.getMessage());
            }
        }

        return new BorrowBooksResponse(successList, failedList);
    }

  @Override
  public BorrowedBookDTO returnBook(Long userId, Long bookId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

    Transaction transaction =
        transactionRepository
            .findByUserAndBookAndTransactionStatus(
                user, book, transactionStatusConfig.getBorrowed())
            .orElseThrow(
                () -> new ResourceNotFoundException("No active borrowing found for this book"));

    LocalDate returnDate = LocalDate.now();
    transaction.setReturnDate(returnDate);
    transaction.setTransactionStatus(transactionStatusConfig.getReturned());

    if (returnDate.isAfter(transaction.getDueDate())) {
      long daysLate = returnDate.toEpochDay() - transaction.getDueDate().toEpochDay();
      double fine = daysLate * libraryConfig.getDailyFineRate();
      transaction.setFineAmount(fine);
      transaction.setPenaltyReason("Late return - " + daysLate + " days overdue");
      notificationService.sendLateReturnNotification(user, book, daysLate, fine);
    }

    book.setAvailableCopies(book.getAvailableCopies() + 1);
    bookRepository.save(book);

    Transaction savedTransaction = transactionRepository.save(transaction);

    notificationService.sendBookReturnedNotification(user, book);

    return toDTO(savedTransaction);
  }

    @Override
    public List<Transaction> getBorrowedBooks(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<String> statuses = List.of(
                transactionStatusConfig.getBorrowed(),
                transactionStatusConfig.getDue()
        );

        return transactionRepository.findByUserAndTransactionStatusIn(user, statuses);
    }

  @Override
  public List<Transaction> getOverdueBooks(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    LocalDate today = LocalDate.now();
    return transactionRepository
        .findByUserAndTransactionStatus(user, transactionStatusConfig.getBorrowed())
        .stream()
        .filter(transaction -> today.isAfter(transaction.getDueDate()))
        .toList();
  }

  @Override
  public List<Transaction> getBorrowingHistory(Long userId) {
    String role = UserContext.getRole();

    if ("ADMIN".equalsIgnoreCase(role)) {
      return transactionRepository.findAll();
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    return transactionRepository.findByUser(user);
  }

  @Override
  public List<Transaction> getAllBorrowingHistory() {
    return transactionRepository.findAll();
  }

  @Override
  public List<Transaction> getBorrowingHistoryByUserId(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    return transactionRepository.findByUser(user);
  }

  @Override
  public boolean canBorrowMoreBooks(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    long currentBorrowedCount =
        transactionRepository.countByUserAndTransactionStatus(
            user, transactionStatusConfig.getBorrowed());
    return currentBorrowedCount < libraryConfig.getMaxBorrowLimit();
  }

  @Override
  public List<Book> getAvailableBooks() {
    return bookRepository.findByAvailableCopiesGreaterThan(0);
  }

  @Override
  public boolean isBookAvailable(Long bookId) {
    return bookRepository.findById(bookId).map(book -> book.getAvailableCopies() > 0).orElse(false);
  }

  // Admin methods
  @Override
  public Book updateBookInventory(Long bookId, Integer quantityChange) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

    int newTotalCopies = book.getTotalCopies() + quantityChange;
    book.setTotalCopies(newTotalCopies);

    if (quantityChange > 0) {
      book.setAvailableCopies(book.getAvailableCopies() + quantityChange);
    }

    return bookRepository.save(book);
  }

  @Override
  public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  @Override
  public List<Transaction> getTransactionsByStatus(String status) {
    return transactionRepository.findByTransactionStatus(status);
  }

  @Override
  public List<Transaction> getOverdueTransactions() {
    LocalDate today = LocalDate.now();
    return transactionRepository
        .findByTransactionStatus(transactionStatusConfig.getBorrowed())
        .stream()
        .filter(transaction -> today.isAfter(transaction.getDueDate()))
        .toList();
  }

  @Override
  public Book updateBookAvailability(Long bookId, String availabilityStatus) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

    if ("YES".equals(availabilityStatus)) {
      book.setAvailableCopies(book.getTotalCopies());
    } else if ("NO".equals(availabilityStatus)) {
      book.setAvailableCopies(0);
    }

    return bookRepository.save(book);
  }

  @Override
  public List<Book> getBooksWithLowStock(Integer threshold) {
    int actualThreshold = threshold != null ? threshold : libraryConfig.getLowStockThreshold();
    return bookRepository.findByAvailableCopiesLessThanEqual(actualThreshold);
  }

  public void setLibraryConfig(LibraryConfig libraryConfig) {
    this.libraryConfig = libraryConfig;
  }

  public void setUserRoleConfig(UserRoleConfig userRoleConfig) {
    this.userRoleConfig = userRoleConfig;
  }

  public void setTransactionStatusConfig(TransactionStatusConfig transactionStatusConfig) {
    this.transactionStatusConfig = transactionStatusConfig;
  }

  public List<Map<String, Object>> getHighFineUsers(double threshold) {
    List<Object[]> results =
        transactionRepository.findUsersWithHighFines(config.getFineThreshold());
    return results.stream()
        .map(
            row ->
                Map.of(
                    "userId", row[0],
                    "userName", row[1],
                    "contactNumber", row[2],
                    "totalFine", row[3]))
        .toList();
  }

  public List<Map<String, Object>> getRepeatedLateUsers(long threshold) {
    List<Object[]> results =
        transactionRepository.findUsersWithRepeatedLateReturns(config.getLateThreshold());
    return results.stream()
        .map(
            row ->
                Map.of(
                    "userId", row[0],
                    "userName", row[1],
                    "contactNumber", row[2],
                    "lateCount", row[3]))
        .toList();
  }

  private BorrowedBookDTO toDTO(Transaction t) {
    return new BorrowedBookDTO(
        t.getTransactionId(),
        t.getBook().getBookId(),
        t.getBook().getBookTitle(),
        t.getUser().getUserId(),
        t.getBorrowDate(),
        t.getDueDate(),
        t.getReturnDate(),
        t.getFineAmount(),
            t.getTransactionStatus());
  }
}

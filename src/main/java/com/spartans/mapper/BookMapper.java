package com.spartans.mapper;

import com.spartans.dto.BookDTO;
import com.spartans.dto.CartDTO;
import com.spartans.dto.TransactionDTO;
import com.spartans.model.Book;
import com.spartans.model.Cart;
import com.spartans.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

  Book toBookEntity(BookDTO dto);

  BookDTO toBookDto(Book book);

  @Mapping(target = "bookId", source = "book.bookId")
  @Mapping(target = "bookTitle", source = "book.bookTitle")
  @Mapping(target = "bookAuthor", source = "book.bookAuthor")
  @Mapping(target = "category", source = "book.category")
  @Mapping(target = "isbn", source = "book.isbn")
  @Mapping(target = "imageUrl", source = "book.imageUrl")
  CartDTO toCartDto(Cart cart);

@Mapping(target = "bookTitle", source = "book.bookTitle")
@Mapping(target = "bookId", source = "book.bookId")
@Mapping(target = "userName", source = "user.userName")
  TransactionDTO toTransactionDto(Transaction transaction);
}

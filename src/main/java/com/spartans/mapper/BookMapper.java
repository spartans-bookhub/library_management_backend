package com.spartans.mapper;

import com.spartans.dto.BookDTO;
import com.spartans.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

  Book toBookEntity(BookDTO dto);

  BookDTO toBookDto(Book book);
}

package com.spartans.dto;

public record UserRequestDTO(
    long userId, String userName, String contactNumber, String address, String email) {}

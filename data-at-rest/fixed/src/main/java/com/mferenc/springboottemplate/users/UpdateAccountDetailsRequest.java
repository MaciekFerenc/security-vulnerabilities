package com.mferenc.springboottemplate.users;

public record UpdateAccountDetailsRequest(String firstName, String lastName, String pesel) {}
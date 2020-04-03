package com.gmail.buer2012.constants;

public interface MailConstants {
  String CONFIRM_EMAIL_MESSAGE = "Hello, %s!%n%n"
      + "We have received an application to register your account with CodeRunner. You need to confirm your mail - follow the link: %s%n%n"
      + "If you have not registered an account with CodeRunner, simply ignore this message.%n%n"
      + "Have a nice day!";
  String RESTORE_PASSWORD_MESSAGE = "Hello, %s!%n%n"
      + "We received a request to recover a password from your CodeRunner account. If you really need to reset your password, follow the link: %s%n%n"
      + "If you did not request a password recovery from your account, just ignore this message.%n%n"
      + "Have a nice day!";
  String CODE_TOPIC = "Your code from CodeRunner";
  String CONFIRM_EMAIL_TOPIC = "Confirm your e-mail on CodeRunner";
  String RESTORE_PASSWORD_TOPIC = "Password recovery from CodeRunner";
}

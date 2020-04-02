package com.gmail.buer2012.constants;

public interface MailConstants {
  String CONFIRM_EMAIL_MESSAGE = "Здравствуйте, %s!%n%n"
      + "Мы получили заявку на регистрацию Вашего аккаунта в Coderunner. Вам нужно подтвердить Вашу почту — пройдите по ссылке: %s%n%n"
      + "Если вы не регистрировали аккаунт на Coderunner – просто проигнорируйте это сообщение.%n%n"
      + "Хорошего дня!";
  String RESTORE_PASSWORD_MESSAGE = "Здравствуйте, %s!%n%n"
      + "Мы получили заявку на восстановление пароля от вашего аккаунта в Coderunner. Если Вам действительно нужно восстановить пароль — пройдите по ссылке: %s%n%n"
      + "Если вы не запрашивали восстановление пароля от своего аккаунта – просто проигнорируйте это сообщение.%n%n"
      + "Хорошего дня!";
  String CODE_TOPIC = "Ваш код с CodeRunner";
  String CONFIRM_EMAIL_TOPIC = "Подтвердите e-mail на CodeRunner";
  String RESTORE_PASSWORD_TOPIC = "Восстановление пароля от CodeRunner";
}

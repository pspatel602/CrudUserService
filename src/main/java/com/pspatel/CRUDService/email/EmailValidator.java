package com.pspatel.CRUDService.email;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class EmailValidator implements Predicate<String> {
  private String regex =
      "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@"
          + "(?:[a-zA-Z0-9-]+\\"
          + ".)+[a-zA-Z]{2,6}$";
  Pattern pattern = Pattern.compile(regex);

  @Override
  public boolean test(String email) {
    Matcher matcher = pattern.matcher(email);
    System.out.println(email + " : " + matcher.matches());
    boolean result = matcher.matches();
    return result;
  }
}

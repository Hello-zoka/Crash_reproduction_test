package org.apache.commons.lang3.time;

import org.junit.Test;
import java.util.Locale;
import java.util.TimeZone;

public class FastDateFormatTest {
  @Test
  public void test_crash() {
    Locale customLocale = new Locale("de");
    // Setting locale to use Japanese calendar
    customLocale = new Locale.Builder().setLocale(customLocale).setUnicodeLocaleKeyword("ca", "japanese").build();
  
    String pattern = "GGGG yyyy-MM-dd"; // Use a valid date pattern, which uses eras
  
    FastDateFormat.getInstance(pattern, TimeZone.getDefault(), customLocale);
  }
}

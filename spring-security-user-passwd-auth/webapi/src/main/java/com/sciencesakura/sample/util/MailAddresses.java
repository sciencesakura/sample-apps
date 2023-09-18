package com.sciencesakura.sample.util;

import jakarta.annotation.Nonnull;
import java.util.EnumSet;
import org.apache.commons.lang3.StringUtils;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressParser;

/**
 * Provides static utility methods for mail addresses.
 */
public final class MailAddresses {

  private static final EnumSet<EmailAddressCriteria> criteria = EmailAddressCriteria.RECOMMENDED;

  private MailAddresses() {
  }

  /**
   * Returns the local part of the given mail address.
   *
   * @param mailAddress the mail address
   * @return the local part of the given mail address
   */
  @Nonnull
  public static String getLocalPart(@Nonnull String mailAddress) {
    var part = EmailAddressParser.getLocalPart(mailAddress, criteria, false);
    if (StringUtils.isEmpty(part)) {
      throw new IllegalArgumentException("'%s' is not well-formed".formatted(mailAddress));
    }
    return part;
  }

  /**
   * Returns the domain part of the given mail address.
   *
   * @param mailAddress the mail address
   * @return the domain part of the given mail address
   */
  @Nonnull
  public static String getDomainPart(@Nonnull String mailAddress) {
    var part = EmailAddressParser.getDomain(mailAddress, criteria, false);
    if (StringUtils.isEmpty(part)) {
      throw new IllegalArgumentException("'%s' is not well-formed".formatted(mailAddress));
    }
    return part;
  }
}

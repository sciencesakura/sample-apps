package com.sciencesakura.sample.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Provides static utility methods for accessing messages from the message source.
 *
 * <p>
 * For implementation reasons, this class is declared as a Spring IoC's bean, but you must not
 * create or use its instance.
 * </p>
 */
@Component
public class Messages {

  private static MessageSource messageSource;

  /**
   * Don't use.
   */
  @Deprecated
  @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  Messages(MessageSource messageSource) {
    Messages.messageSource = messageSource;
  }

  /**
   * Get a message for the given code.
   *
   * @param code the message code to look up
   * @param args arguments that will be filled in for params within the message
   * @return the resolved message
   */
  @Nonnull
  public static String get(@Nonnull String code, Object... args) {
    return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
  }

  /**
   * Get a message for given {@code MessageSourceResolvable}.
   *
   * @param resolvable the value object storing attributes required to resolve a message
   * @return the resolved message
   */
  @Nonnull
  public static String get(@Nonnull MessageSourceResolvable resolvable) {
    return messageSource.getMessage(resolvable, LocaleContextHolder.getLocale());
  }
}

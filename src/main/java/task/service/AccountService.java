package task.service;

import task.exception.LimitExceededException;
import task.exception.NoSuchAccountException;
import task.model.Account;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Main application service that provides key account functionality and is exposed as REST HTTP service.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public interface AccountService {

    /**
     * Create new account with given {@code amount}.
     *
     * @param amount the money to create new account with
     * @return new account
     */
    Account create(@Nullable BigDecimal amount);

    /**
     * Get account with given {@code accountId}.
     *
     * @param accountId the money to create new account with
     * @return account with given {@code accountId}
     * @throws NoSuchAccountException if account with given {@code accountId} doesn't exist
     */
    Account getAccount(long accountId);

    /**
     * Transfer money from account with id: {@code fromAccountId} to account with id: {@code toAccountId}.
     *
     * @param fromAccountId account id to transfer money from
     * @param toAccountId   account id to transfer money to
     * @param amount        money amount to transfer
     * @throws NullPointerException     when {@code amount} is null
     * @throws IllegalArgumentException when {@code fromAccountId == toAccountId} or amount is not positive
     * @throws NoSuchAccountException   when from- or to-account doesn't exist
     * @throws LimitExceededException   when {@code amount} is greater that amount on account with id: {@code fromAccountId}
     */
    void transfer(long fromAccountId, long toAccountId, BigDecimal amount);
}

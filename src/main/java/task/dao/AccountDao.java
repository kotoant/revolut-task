package task.dao;

import org.apache.ibatis.annotations.Param;
import task.model.Account;

/**
 * This DAO provides key methods for saving and retrieving accounts.
 * The implementation is generated by mybatis framework.
 *
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public interface AccountDao {

    /**
     * Select account by id.
     *
     * @param accountId account id
     * @return {@link Account} if account with provided {@code accountId} exists, {@code null} - otherwise.
     */
    Account select(@Param("accountId") long accountId);

    /**
     * Create new account with given amount. The method sets new generated account id to the {@code account} object.
     *
     * @param account account
     */
    void insert(@Param("account") Account account);

    /**
     * Update existing account with new amount value.
     *
     * @param account account
     * @return number of updated rows: 1 when account exists, 0 otherwise
     */
    int update(@Param("account") Account account);

    /**
     * Health-check.
     *
     * @return 1 when the service is healthy, return any other value or throw exception otherwise
     */
    int ping();
}

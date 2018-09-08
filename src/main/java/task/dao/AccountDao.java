package task.dao;

import org.apache.ibatis.annotations.Param;
import task.model.Account;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public interface AccountDao {

    Account select(@Param("accountId") long accountId);

    void insert(@Param("account") Account account);

    void update(@Param("account") Account account);
}

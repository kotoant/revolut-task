package task.exception;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class NoSuchAccountException extends RuntimeException {

    private final long accountId;

    public NoSuchAccountException(long accountId) {
        super("No such account: " + accountId);
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }
}

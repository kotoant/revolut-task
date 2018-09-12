package task.dao;

import org.junit.rules.ExternalResource;

import java.util.List;

/**
 * @author Anton Kotov (kotov-anton@yandex.ru)
 */
public class TemporaryDatabase extends ExternalResource {

    private final List<String> initScripts;




    public TemporaryDatabase(List<String> initScripts) {
        this.initScripts = initScripts;
    }
}

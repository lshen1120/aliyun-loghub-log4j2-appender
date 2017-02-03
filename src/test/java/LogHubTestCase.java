import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

/**
 * Created by SL on 2017/2/3.
 */
@Test
public class LogHubTestCase {

    public void test()
    {
        Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
        logger.info("test");
    }
}

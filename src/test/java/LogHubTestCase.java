import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.time.Instant;

/**
 * Created by SL on 2017/2/3.
 */
@Test
public class LogHubTestCase {

    public void test() throws InterruptedException {
        Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
        logger.info("test:"+ Instant.now().toString());
        //等待发送线程结束
        Thread.sleep(1000*5);
    }

}

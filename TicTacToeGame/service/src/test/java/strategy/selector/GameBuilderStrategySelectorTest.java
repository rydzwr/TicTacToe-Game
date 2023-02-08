package strategy.selector;

import com.rydzwr.tictactoe.service.game.strategy.selector.GameBuilderStrategySelector;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GameBuilderStrategySelectorTest {
    @Autowired
    private GameBuilderStrategySelector selector;

    @Test
    public void test() {
        Assertions.assertNotNull(selector);
    }
}



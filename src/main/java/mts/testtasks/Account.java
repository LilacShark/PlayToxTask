package mts.testtasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Account {

    private static final Logger logger = LogManager.getLogger(Account.class);

    private String id;
    private volatile int money;
//    private AtomicInteger money;

    public Account(String id, int money) {
        this.id = id;
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public synchronized boolean writeOffAmount(int payment) {
        if (money < payment) {
            logger.info("Нехватает денег на счете {} для проводки докумнта на сумму {}", this.id, payment);
            return false;
        }
        money = money - payment;
        logger.info("Со счета: {} списано: {}. Остаток: {}", this.id, payment, this.money);
        return true;
    }

    public synchronized boolean transferAmount(int payment) {
        money = money + payment;
        logger.info("На счет {} зачисленно {}", this.id, payment);
        return true;
    }
}

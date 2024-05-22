package mts.testtasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public class Program {

    private static final Logger logger = LogManager.getLogger(Program.class);
    private static final List<Account> accounts = new ArrayList<>();
//    private static ExecutorService executorService;

    public static void main(String[] args) throws InterruptedException {

        int latchCounter = 30;
        CountDownLatch latch = new CountDownLatch(latchCounter);

        int threadCounter = ThreadLocalRandom.current().nextInt(4) + 4;

        int moneyStart = 0;
        int moneyEnd = 0;

        logger.info("Инициализация..");

        for (int i = 0; i < 10; i++) {
            Account account = new Account(String.valueOf(ThreadLocalRandom.current().nextInt(10_000)), 10000);
            accounts.add(account);
            logger.info("Счет: {} , Сумма: {}", account.getId(), account.getMoney());
            moneyStart = moneyStart + account.getMoney();
        }

        logger.info("Начальная сумма.. {}", moneyStart);


        logger.info("Исполнение..");

        Runnable payDocument = () -> {

            while (latch.getCount() > 0) {
                latch.countDown();

                int payment = ThreadLocalRandom.current().nextInt(100);
                int numberDt = ThreadLocalRandom.current().nextInt(accounts.size());
                int numberCt = ThreadLocalRandom.current().nextInt(accounts.size());

                Account accountDt = accounts.get(numberDt);
                Account accountCt = accounts.get(numberCt);

                logger.info("Проводка начата Счет Дт: {}, Счет Кт: {}, Сумма: {}",
                        accountDt.getId(), accountCt.getId(), payment);

                boolean result = accountDt.writeOffAmount(payment);
                if (!result) {
                    logger.info("Проводка завершена  с ошибкой Счет Дт: {}, Счет Кт: {}, Сумма: {}",
                            accountDt.getId(), accountCt.getId(), payment);
                    return;
                }
                accountCt.transferAmount(payment);

                logger.info("Проводка завершена успешно Счет Дт: {}, Счет Кт: {}, Сумма: {}",
                        accountDt.getId(), accountCt.getId(), payment);

                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000) + 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        for (int i = 0; i < threadCounter; i++) {
            new Thread(payDocument).start();
        }

        latch.await();

        for (Account account : accounts) {
            logger.info("Счет: {} , Сумма: {}", account.getId(), account.getMoney());
            moneyEnd = moneyEnd + account.getMoney();
        }

        logger.info("MoneyStart {}   MoneyEnd {}", moneyStart, moneyEnd);

    }
}
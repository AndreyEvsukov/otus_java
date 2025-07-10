package ru.otus.numbers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersPrinter {
    private static final Logger logger = LoggerFactory.getLogger(NumbersPrinter.class);

    private static final int ID_FOR_FIRST_THREAD = 1;
    private static final int ID_FOR_SECOND_THREAD = 2;
    public static final int HIGH_BOUNDARY = 10;
    public static final int LOW_BOUNDARY = 1;
    private static final int COUNTER_CHANGER = ID_FOR_SECOND_THREAD;

    private int last = ID_FOR_SECOND_THREAD;
    private int counter = 1;
    private boolean forwardDirection = true;

    private synchronized void action(int currentThreadId) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // spurious wakeup https://en.wikipedia.org/wiki/Spurious_wakeup
                // поэтому не if
                while (currentThreadId == last) {
                    this.wait();
                }

                logger.info("{}", counter);

                if (currentThreadId == COUNTER_CHANGER) {
                    if (counter == HIGH_BOUNDARY) {
                        forwardDirection = false;
                    } else if (counter == LOW_BOUNDARY) {
                        forwardDirection = true;
                    }
                    if (forwardDirection) {
                        counter++;
                    } else {
                        counter--;
                    }
                }
                last = currentThreadId;
                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        NumbersPrinter numbersPrinter = new NumbersPrinter();
        var tr1 = new Thread(() -> numbersPrinter.action(ID_FOR_FIRST_THREAD));
        tr1.setName("Поток1");
        tr1.start();
        var tr2 = new Thread(() -> numbersPrinter.action(ID_FOR_SECOND_THREAD));
        tr2.setName("Поток2");
        tr2.start();
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

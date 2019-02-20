package examples;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


public class CallableEx implements Callable<String> {

    private int counter = 0;
    private int getCounterAndIncrementHim() {
        return counter++;
    }


    @Override
    public String call() throws Exception {
        Thread.sleep(500);
//         возвращает имя потока, который выполняет callable таск
//         return Thread.currentThread().getName();
        return String.valueOf(getCounterAndIncrementHim());
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10); //Получаем ExecutorService утилитного класса Executors с размером пула потоков равному 10
        List<Future<String>> list = new ArrayList<>();                 //создаем список с Future, которые ассоциированы с Callable
        Callable<String> callable = new CallableEx();
        for (int i = 0; i < 100; i++) {
            //сабмитим Callable таски, которые будут
            //выполнены пулом потоков
            Future<String> future = executor.submit(callable);
            //добавляя Future в список,
            //мы сможем получить результат выполнения
            list.add(future);
        }
        for (Future<String> fut : list) {
            try {
                // печатаем в консоль возвращенное значение Future
                // будет задержка в 1 секунду, потому что Future.get()
                // ждет пока таск закончит выполнение
                System.out.println(new Date() + "::" + fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
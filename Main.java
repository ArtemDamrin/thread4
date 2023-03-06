import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> symbolA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> symbolB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> symbolC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread textGenerator = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                String text = generateText("abc", 100000);
                try {
                    symbolA.put(text);
                    symbolB.put(text);
                    symbolC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGenerator.start();

        Thread threadForA = new Thread(() -> {
            char letter = 'a';
            int maxA = findMaxCharCount(symbolA, letter);
            System.out.println("Максимальное кол-во символов " + letter + " в наборе: " + maxA);
        });
        threadForA.start();

        Thread threadForB = new Thread(() -> {
            char letter = 'b';
            int maxB = findMaxCharCount(symbolB, letter);
            System.out.println("Максимальное кол-во символов " + letter + " в наборе: " + maxB);
        });
        threadForB.start();

        Thread threadForC = new Thread(() -> {
            char letter = 'c';
            int maxC = findMaxCharCount(symbolC, letter);
            System.out.println("Максимальное кол-во символов " + letter + " в наборе: " + maxC);
        });
        threadForC.start();

        threadForA.join();
        threadForB.join();
        threadForC.join();
    }

    private static int findMaxCharCount(BlockingQueue<String> symbol, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10000; i++) {
                text = symbol.take();

                for (char c : text.toCharArray()) {
                    if (c == letter) {
                        count++;
                    }
                }

                if (count > max) {
                    max = count;
                }
                count = 0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted");
            return -1;
        }
        return max;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
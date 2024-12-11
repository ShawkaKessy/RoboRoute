import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 1000;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int countR = (int) route.chars().filter(ch -> ch == 'R').count();

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(countR, 1, Integer::sum);
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        analyzeAndPrintResults();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void analyzeAndPrintResults() {
        int maxFrequency = sizeToFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);

        int maxCount = sizeToFreq.getOrDefault(maxFrequency, 0);

        System.out.println("Самое частое количество повторений " + maxFrequency + " (встретилось " + maxCount + " раз)");
        System.out.println("Другие размеры:");

        sizeToFreq.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByKey())
                .forEach(entry -> {
                    if (entry.getKey() != maxFrequency) {
                        System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
                    }
                });
    }
}

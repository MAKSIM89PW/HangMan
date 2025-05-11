package org.example;

import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final int MAX_ERRORS = 6;

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        ArrayList<String> lib = loadWords("src/main/resources/words.txt");

        while (true) {
            String word = chooseRandomWord(lib);
            char[] masked = maskWord(word);
            playGame(word, masked, input);

            System.out.print("Сыграть ещё раз? (да/нет): ");
            String answer = input.nextLine().trim().toLowerCase();
            if (answer.equals("нет") || answer.equals("стоп")) {
                System.out.println("До свидания!");
                break;
            }
        }
    }

    private static ArrayList<String> loadWords(String filePath) throws Exception {
        ArrayList<String> words = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new FileReader(filePath))) {
            while (fileScanner.hasNextLine()) {
                words.add(fileScanner.nextLine().toUpperCase());
            }
        }
        return words;
    }

    private static String chooseRandomWord(ArrayList<String> words) {
        int randomIndex = (int) (Math.random() * words.size());
        return words.get(randomIndex);
    }

    private static char[] maskWord(String word) {
        return "*".repeat(word.length()).toCharArray();
    }

    private static void playGame(String word, char[] masked, Scanner input) {
        Set<Character> guessedLetters = new HashSet<>();
        Set<Character> wrongLetters = new HashSet<>();
        int wrongAttempts = 0;

        System.out.println("Угадайте слово (на русском) "
                                   + word // выведение слова для отладки
        );
        System.out.println();

        while (true) {
            printMasked(masked);
            System.out.println("Ошибки " + wrongAttempts + "/" + MAX_ERRORS);
            System.out.println();
            printHangman(wrongAttempts);
            System.out.println("Введенные буквы: " + guessedLetters.stream()
                                                                   .sorted().map(String::valueOf)
                                                                   .collect(Collectors.joining(", ")));
            System.out.println("Ошибочные буквы: " + wrongLetters.stream()
                                                                 .sorted().map(String::valueOf)
                                                                 .collect(Collectors.joining(", ")));
            System.out.println();

            String userInput = readGuess(input);

            // Попытка угадать всё слово
            if (userInput.length() > 1) {
                if (userInput.equals(word)) {
                    System.out.println("Поздравляем! Вы угадали слово: " + word);
                    break;
                } else {
                    System.out.println("Неверное слово.");
                    wrongAttempts++;
                    if (wrongAttempts >= MAX_ERRORS) {
                        printHangman(wrongAttempts);
                        System.out.println("Вы проиграли. Слово было: " + word);
                        break;
                    }
                    continue;
                }
            }

            // Введена одна буква
            char guess = userInput.charAt(0);

            if (guessedLetters.contains(guess)) {
                System.out.println("Вы уже вводили эту букву.");
                continue;
            }

            guessedLetters.add(guess);
            boolean updated = updateMasked(word, masked, guess);

            if (!updated) {
                if (wrongLetters.contains(guess)) {
                    System.out.println("Вы уже ошибались с этой буквой.");
                } else {
                    System.out.println("Такой буквы нет.");
                    System.out.println();
                    wrongLetters.add(guess);
                    wrongAttempts++;
                }

                if (wrongAttempts >= MAX_ERRORS) {
                    printHangman(wrongAttempts);
                    System.out.println("Вы проиграли. Слово было: " + word);
                    break;
                }
            }

            if (isWordGuessed(word, masked)) {
                System.out.println("Поздравляем! Вы угадали слово: " + word);
                break;
            }
        }
    }

    private static boolean updateMasked(String word, char[] masked, char guess) {
        boolean found = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess && masked[i] == '*') {
                masked[i] = guess;
                found = true;
            }
        }
        return found;
    }

    private static String readGuess(Scanner input) {
        System.out.println("Введите букву или всё слово (на русском): ");
        String line = input.nextLine().toUpperCase();
        while (line.isEmpty() || !line.matches("[А-ЯЁ]+")) {
            System.out.println("Ошибка. Введите только русские буквы: ");
            line = input.nextLine().trim().toUpperCase();
        }
        return line;
    }

    private static boolean isWordGuessed(String word, char[] masked) {
        return word.equals(String.valueOf(masked));
    }

    private static void printMasked(char[] masked) {
        System.out.println(new String(masked).replace("", " ").trim());
    }

    private static void printHangman(int wrongAttempts) {
        if (wrongAttempts > 0 && wrongAttempts < HANGMAN_STAGES.length) {
            System.out.println(HANGMAN_STAGES[wrongAttempts]);
        } else if (wrongAttempts >= HANGMAN_STAGES.length) {
            System.out.println(HANGMAN_STAGES[HANGMAN_STAGES.length - 1]);
        }
    }

    private static final String[] HANGMAN_STAGES = {
            """
          +---+
              |
              |
              |
             ===
        """,
            """
          +---+
          O   |
              |
              |
             ===
        """,
            """
          +---+
          O   |
          |   |
              |
             ===
        """,
            """
          +---+
          O   |
         /|   |
              |
             ===
        """,
            """
          +---+
          O   |
         /|\\  |
              |
             ===
        """,
            """
          +---+
          O   |
         /|\\  |
         /    |
             ===
        """,
            """
          +---+
          O   |
         /|\\  |
         / \\  |
             ===
        """
    };
}

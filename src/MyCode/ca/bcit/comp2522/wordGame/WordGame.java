package ca.bcit.comp2522.wordGame;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * Geography trivia game:
 * - 10 questions per game
 * - Randomly picks one of three question types:
 * (a) Print a capital → ask for its country
 * (b) Print a country → ask for its capital
 * (c) Print a fact    → ask for its country
 * - Two attempts per question; track per-game and cumulative totals
 * - When player stops, append cumulative totals to score.txt
 *
 * @author Arshia Adamian
 * @version 1.0
 */
public final class WordGame
{

    // ---------------- constants (no magic numbers) ----------------
    private static final int QUESTIONS_PER_GAME = 10;
    private static final int FACTS_PER_COUNTRY = 3;
    private static final int MAX_ATTEMPTS = 2;

    // Question type identifiers
    private static final int TYPE_CAPITAL_TO_COUNTRY = 1; // (a)
    private static final int TYPE_COUNTRY_TO_CAPITAL = 2; // (b)
    private static final int TYPE_FACT_TO_COUNTRY = 3; // (c)

    // Attempt indices
    private static final int FIRST_ATTEMPT = 1;
    private static final int SECOND_ATTEMPT = 2;

    // ---------------- data & utilities ----------------
    private final World world;
    private final Map<String, Country> countries;
    private final Country[] countryArray;
    private final Scanner scanner;
    private final Random random;
    private final Path scorePath;

    // ---------------- cumulative totals across all games -----------
    private int totalGamesPlayed;
    private int totalCorrectFirst;
    private int totalCorrectSecond;
    private int totalIncorrectBoth;

    /**
     * Constructs the game, loads country data from myResources/countries.
     *
     * @throws IOException if data files cannot be read
     */
    public WordGame(final Scanner sharedScanner)
        throws IOException
    {
        Path dataDir;
        dataDir = Paths.get("src/myResources/countries");

        world        = new World(dataDir);
        countries    = world.getCountries();
        countryArray = toArray(countries);

        scanner = sharedScanner;
        random  = new Random();

        scorePath = Paths.get("src/myCode/score.txt");

        totalGamesPlayed   = 0;
        totalCorrectFirst  = 0;
        totalCorrectSecond = 0;
        totalIncorrectBoth = 0;
    }

    /**
     * Runs 10-question games until user says No. Then appends totals to score.txt.
     *
     * @throws IOException if writing the score file fails
     */
    public void run() throws IOException
    {
        boolean keepPlaying;
        keepPlaying = true;

        while (keepPlaying)
        {
            int correctFirst;
            int correctSecond;
            int incorrectBoth;

            correctFirst  = 0;
            correctSecond = 0;
            incorrectBoth = 0;

            for (int i = 0; i < QUESTIONS_PER_GAME; i++)
            {
                final int result;
                result = askOneQuestion();
                if (result == 2)
                {
                    // correct on first attempt
                    correctFirst++;
                }
                else if (result == 1)
                {
                    // correct on second attempt
                    correctSecond++;
                }
                else
                {
                    // incorrect after two attempts
                    incorrectBoth++;
                }
                System.out.println();
            }

            // Per-game summary (exact wording/format from the spec)
            System.out.println("-- Game Summary --");
            System.out.println("1 word game played");
            System.out.println(correctFirst + " correct answers on the first attempt");
            System.out.println(correctSecond + " correct answers on the second attempt");
            System.out.println(incorrectBoth + " incorrect answers on two attempts each");
            System.out.println();

            // Update cumulative totals (once per game)
            totalGamesPlayed++;
            totalCorrectFirst += correctFirst;
            totalCorrectSecond += correctSecond;
            totalIncorrectBoth += incorrectBoth;

            keepPlaying = askPlayAgain();
            System.out.println();
        }

        appendTotalsToScoreFile(scorePath);
        System.out.println("Thanks for playing! Totals were saved to score.txt");
    }

    /**
     * Asks one random question. Return codes:
     * 2 = correct on first attempt
     * 1 = correct on second attempt
     * 0 = incorrect after two attempts
     */
    private int askOneQuestion()
    {
        final Country c;
        c = randomCountry();

        // Pick type in {1,2,3}
        final int qType;
        qType = random.nextInt(3) + 1;

        final String prompt;
        final String answer;

        if (qType == TYPE_CAPITAL_TO_COUNTRY)
        {
            // (a) show capital, ask for country
            prompt = "Which country has the capital \"" + c.getCapitalCityName() + "\"?";
            answer = c.getName();
        }
        else if (qType == TYPE_COUNTRY_TO_CAPITAL)
        {
            // (b) show country, ask for capital
            prompt = "What is the capital city of \"" + c.getName() + "\"?";
            answer = c.getCapitalCityName();
        }
        else
        {
            // (c) show one fact, ask for country
            final int factIndex;
            factIndex = random.nextInt(FACTS_PER_COUNTRY); // 0..2

            final String fact;
            fact = c.getFacts()[factIndex];

            prompt = "Which country matches this fact?\n" + fact;
            answer = c.getName();
        }

        System.out.println(prompt);

        // Two attempts: read full line (trim) so multi-word answers work
        String guess;
        int attempt;
        attempt = FIRST_ATTEMPT;

        while (attempt <= MAX_ATTEMPTS)
        {
            if (attempt == FIRST_ATTEMPT)
            {
                System.out.print("Your answer: ");
            }
            else
            {
                System.out.print("Your second answer: ");
            }

            guess = readLineTrimmed();

            if (guess.equalsIgnoreCase(answer))
            {
                System.out.println("CORRECT");

                if (attempt == FIRST_ATTEMPT)
                {
                    return 2;
                }
                else
                {
                    return 1;
                }
            }

            if (attempt == FIRST_ATTEMPT)
            {
                System.out.println("INCORRECT. Try once more.");
            }

            attempt++;
        }

        System.out.println("INCORRECT.");
        System.out.println("The correct answer was " + answer);
        return 0;
    }

    // ------------------ play-again & I/O helpers ------------------

    private boolean askPlayAgain()
    {
        while (true)
        {
            System.out.print("Play again? (Yes/No): ");
            final String line;
            line = readLineTrimmed();

            if (line.equalsIgnoreCase("yes"))
            {
                return true;
            }
            else if (line.equalsIgnoreCase("no"))
            {
                return false;
            }
            else
            {
                System.out.println("Please enter Yes or No.");
            }
        }
    }

    private void appendTotalsToScoreFile(Path scorePath)
        throws IOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(
            scorePath,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND))
        {
            // Write number of games, with correct singular/plural form
            writer.write("- " + totalGamesPlayed + " ");
            if (totalGamesPlayed == 1)
            {
                writer.write("word game played");
            }
            else
            {
                writer.write("word games played");
            }
            writer.newLine();

            writer.write("- " + totalCorrectFirst + " correct answers on the first attempt");
            writer.newLine();
            writer.write("- " + totalCorrectSecond + " correct answers on the second attempt");
            writer.newLine();
            writer.write("- " + totalIncorrectBoth + " incorrect answers on two attempts each");
            writer.newLine();
            writer.newLine();
        }
    }


    // ------------------ small utilities ------------------

    private String readLineTrimmed()
    {
        final String line;
        line = scanner.nextLine();

        if (line == null)
        {
            return "";
        }
        else
        {
            return line.trim();
        }
    }


    private Country randomCountry()
    {
        final int idx;
        final int countryArrayLength;

        countryArrayLength = countryArray.length;
        idx                = random.nextInt(countryArrayLength); // 0..length-1
        return countryArray[idx];
    }

    private static Country[] toArray(final Map<String, Country> map)
    {
        final Country[] array;
        array = new Country[map.size()];

        final Set<String> keys;
        keys = map.keySet();

        int i;
        i = 0;

        for (final String key : keys)
        {
            final Country country;
            country  = map.get(key);
            array[i] = country;
            i++;
        }

        return array;
    }

}

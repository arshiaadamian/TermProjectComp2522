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
 * Runs the geography Word Game.
 * The game asks the user questions about countries, capital cities,
 * and facts. Each game consists of questions.
 * The WordGame keeps track of per-game results and cumulative totals
 * across all games in a session. When the player stops, the totals
 * are appended to a score file.
 *
 * @author Arshia Adamian
 * @version 1.0
 */
public final class WordGame
{

    private static final int QUESTIONS_PER_GAME = 10;
    private static final int FACTS_PER_COUNTRY = 3;
    private static final int MAX_ATTEMPTS = 2;
    private static final int FIRST_TRY_POINTS = 2;
    private static final int SECOND_TRY_POINTS = 1;
    private static final int MISSED_ANSWER_POINTS = 0;
    private static final int RANDOM_OFFSET = 1;

    private static final int TYPE_CAPITAL_TO_COUNTRY = 1; // (a)
    private static final int TYPE_COUNTRY_TO_CAPITAL = 2; // (b)
    private static final int TYPE_FACT_TO_COUNTRY = 3; // (c)

    private static final int FIRST_ATTEMPT = 1;
    private static final int SECOND_ATTEMPT = 2;

    private final World world;
    private final Map<String, Country> countries;
    private final Country[] countryArray;
    private final Scanner scanner;
    private final Random random;
    private final Path scorePath;

    private int totalGamesPlayed;
    private int totalCorrectFirst;
    private int totalCorrectSecond;
    private int totalIncorrectBoth;

    /**
     * Creates a new WordGame and loads all country data from the
     * source files.
     * Uses the provided Scanner for all user input so that the same
     * Scanner can be shared with the Main menu.
     *
     * @param sharedScanner the Scanner used to read user input
     *
     * @throws IOException if any of the country data files cannot be read
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
     * Runs the main Word Game loop.
     * Each game consists of questions. After each game,
     * a summary is printed and the user is asked if they want
     * to play again. If the user chooses not to play again,
     * the cumulative totals for the session are written to the
     * score file.
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
                if (result == FIRST_TRY_POINTS)
                {
                    // correct on first attempt
                    correctFirst++;
                }
                else if (result == SECOND_TRY_POINTS)
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
     * Asks one random question about a country.
     * The question type is chosen at random from:
     * 1) capital to country, 2) country to capital, or 3) fact to country.
     *
     * The player has up to two attempts to answer correctly.
     *
     * @return 2 if the answer was correct on the first attempt,
     *         1 if the answer was correct on the second attempt,
     *         0 if both attempts were incorrect
     */
    private int askOneQuestion()
    {
        final Country c;
        c = randomCountry();

        // Pick type in {1,2,3}
        final int qType;
        qType = random.nextInt(FACTS_PER_COUNTRY) + RANDOM_OFFSET;

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
                    return FIRST_TRY_POINTS;
                }
                else
                {
                    return SECOND_TRY_POINTS;
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
        return MISSED_ANSWER_POINTS;
    }

    /**
     * Asks the user if they want to play another game.
     * Repeats the prompt until the user enters "Yes" or "No"
     * in any letter case.
     *
     * @return true if the user answers "Yes", false if the user answers "No", ignoring letter case
     */
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

    /**
     * Appends the cumulative totals for this session to the score file.
     * Writes the number of games played and how many answers were correct
     * on the first attempt, correct on the second attempt, and incorrect
     * after two attempts.
     *
     * If the file does not exist, it is created.
     *
     * @param scorePath path to the score file to write to
     *
     * @throws IOException if there is an error writing to the file
     */
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


    /**
     * Reads a line of input from the shared Scanner and trims
     * leading and trailing whitespace.
     *
     * If the line is null, an empty string is returned.
     *
     * @return the trimmed line of user input, or an empty string if null
     */
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


    /**
     * Returns a random Country from the internal country array.
     *
     * @return a randomly selected Country
     */
    private Country randomCountry()
    {
        final int idx;
        final int countryArrayLength;

        countryArrayLength = countryArray.length;
        idx                = random.nextInt(countryArrayLength); // 0..length-1
        return countryArray[idx];
    }

    /**
     * Converts the map of country names and Country objects into
     * an array of Country references. The order of countries in the
     * resulting array depends on the iteration order of the map's keys.
     *
     * @param map the map of country names to Country objects
     *
     * @return an array containing all Country objects from the map
     */
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

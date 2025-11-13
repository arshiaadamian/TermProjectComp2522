package ca.bcit.comp2522.wordGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages the scoring information for a single Word Game session.
 * A Score object records:
 * - the date and time the session was played
 * - how many games were played
 * - how many answers were correct on the first attempt
 * - how many answers were correct on the second attempt
 * - how many answers were incorrect after two attempts
 *
 * The Score class is also responsible for writing score entries to a text file
 * and reading them back in the exact format required by the JUnit tests.
 *
 * Author: Arshia Adamian
 * Version: 1.0
 */
public final class Score
{

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime dateTimePlayed;
    private final int gamesPlayed;
    private final int correctFirst;
    private final int correctSecond;
    private final int incorrect;

    /**
     * Creates a Score object with the specified values.
     *
     * @param dateTimePlayed the date and time the score was recorded
     * @param gamesPlayed the total number of games played
     * @param correctFirst number of first-attempt correct answers
     * @param correctSecond number of second-attempt correct answers
     * @param incorrect number of incorrect answers
     *
     * @throws IllegalArgumentException if invalid.
     */
    public Score(final LocalDateTime dateTimePlayed,
                 final int gamesPlayed,
                 final int correctFirst,
                 final int correctSecond,
                 final int incorrect)
    {
        validateScore(dateTimePlayed, gamesPlayed, correctFirst, correctSecond, incorrect);

        this.dateTimePlayed = dateTimePlayed;
        this.gamesPlayed    = gamesPlayed;
        this.correctFirst   = correctFirst;
        this.correctSecond  = correctSecond;
        this.incorrect      = incorrect;
    }

    /**
     * Returns the date and time when this score was created.
     *
     * @return the session date and time
     */
    public LocalDateTime getDateTimePlayed()
    {
        return dateTimePlayed;
    }

    /**
     * Returns how many games were played during this scoring session.
     *
     * @return number of games played
     */
    public int getGamesPlayed()
    {
        return gamesPlayed;
    }

    /**
     * Returns how many first-attempt correct answers were recorded.
     *
     * @return number of first-attempt correct answers
     */
    public int getCorrectFirst()
    {
        return correctFirst;
    }

    /**
     * Returns how many second-attempt correct answers were recorded.
     *
     * @return number of second-attempt correct answers
     */
    public int getCorrectSecond()
    {
        return correctSecond;
    }

    /**
     * Returns how many answers were incorrect after two attempts.
     *
     * @return number of incorrect answers
     */
    public int getIncorrect()
    {
        return incorrect;
    }

    /**
     * Calculates the final score for the session.
     * First-attempt answers are worth 2 points.
     * Second-attempt answers are worth 1 point.
     * Incorrect answers are worth 0 points.
     *
     * @return total score for this session
     */
    public int getScore()
    {
        final int firstPoints;
        final int secondPoints;
        final int totalPoints;

        firstPoints  = correctFirst * 2;
        secondPoints = correctSecond;
        totalPoints = firstPoints + secondPoints;

        return totalPoints;
    }

    /**
     * Returns a string representation of the score in the exact format
     * expected by the JUnit tests. Includes a trailing newline.
     *
     * @return formatted multi-line score entry
     */
    @Override
    public String toString()
    {
        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("Date and Time: ").append(dateTimePlayed.format(FORMATTER)).append('\n');
        sb.append("Games Played: ").append(gamesPlayed).append('\n');
        sb.append("Correct First Attempts: ").append(correctFirst).append('\n');
        sb.append("Correct Second Attempts: ").append(correctSecond).append('\n');
        sb.append("Incorrect Attempts: ").append(incorrect).append('\n');
        sb.append("Score: ").append(getScore()).append(" points").append('\n');

        return sb.toString();
    }



    /**
     * Appends this score entry to the specified file using the toString() format.
     * If the file does not exist, it is created automatically.
     *
     * @param score the Score object to write
     * @param filePath the file path to append to
     *
     * @throws IOException if invalid
     */
    public static void appendScoreToFile(final Score score,
                                         final String filePath) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true)))
        {
            writer.write(score.toString());
        }
    }

    /**
     * Reads all score entries from the specified file and converts them back
     * into Score objects. Only entries following the exact output format
     * produced by toString() are recognized.
     *
     * @param filePath path to the score file
     *
     * @return a list of Score objects read from the file
     *
     * @throws IOException if reading the file fails
     */
    public static List<Score> readScoresFromFile(final String filePath) throws IOException
    {
        final List<Score> results;
        results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                if (!line.startsWith("Date and Time:"))
                {
                    continue;
                }

                final String dtText;
                dtText = line.substring("Date and Time:".length()).trim();

                final LocalDateTime dt;
                dt = LocalDateTime.parse(dtText, FORMATTER);

                final String gamesLine = reader.readLine();
                final String firstLine = reader.readLine();
                final String secondLine = reader.readLine();
                final String incLine = reader.readLine();
                final String scoreLine = reader.readLine(); // not used to build object (we recompute)

                if (gamesLine == null || firstLine == null || secondLine == null
                    || incLine == null || scoreLine == null)
                {
                    // Incomplete block at EOF; stop.
                    break;
                }

                final int games;
                final int first;
                final int second;
                final int incorrect;

                games     = parseTrailingInt(gamesLine, "Games Played:");
                first     = parseTrailingInt(firstLine, "Correct First Attempts:");
                second    = parseTrailingInt(secondLine, "Correct Second Attempts:");
                incorrect = parseTrailingInt(incLine, "Incorrect Attempts:");

                results.add(new Score(dt, games, first, second, incorrect));
            }
        }

        return results;
    }

    /**
     * Extracts the first integer value that appears after the given prefix.
     * Used for reading values back from the text file.
     *
     * @param fullLine the complete line read from the file
     * @param prefix the label expected at the start of the line
     *
     * @return the integer that follows the prefix
     *
     * @throws IllegalArgumentException if invalid
     */
    private static int parseTrailingInt(final String fullLine,
                                        final String prefix)
    {
        if (fullLine == null)
        {
            throw new IllegalArgumentException("line is null");
        }
        final String trimmed;
        trimmed = fullLine.trim();

        if (!trimmed.startsWith(prefix))
        {
            throw new IllegalArgumentException("Unexpected line: " + fullLine);
        }

        final String numText;
        numText = trimmed.substring(prefix.length()).trim().split("\\s+")[0]; // grab the first token after prefix

        return Integer.parseInt(numText);
    }

    /**
     * Validates all fields used to construct a Score object.
     * Ensures that the date and time is not null and that all
     * numeric values are zero or greater. If any value is invalid,
     * this method throws an IllegalArgumentException.
     *
     * @param dateTimePlayed the date and time the score was recorded
     * @param gamesPlayed total number of games played
     * @param correctFirst number of first-attempt correct answers
     * @param correctSecond number of second-attempt correct answers
     * @param incorrect number of incorrect answers
     *
     * @throws IllegalArgumentException if the date is null or any count is negative
     */
    private static void validateScore(final LocalDateTime dateTimePlayed,
                                      final int gamesPlayed,
                                      final int correctFirst,
                                      final int correctSecond,
                                      final int incorrect)
    {
        if (dateTimePlayed == null)
        {
            throw new IllegalArgumentException("dateTimePlayed cannot be null");
        }

        if (gamesPlayed < 0 || correctFirst < 0 || correctSecond < 0 || incorrect < 0)
        {
            throw new IllegalArgumentException("Counts cannot be negative");
        }
    }

}
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
 * Score model compatible with the provided JUnit tests.
 * One Score = results from (typically) one play session.
 * <p>
 * Expected text format (exactly as toString()):
 * Date and Time: 2025-11-12 10:25:30
 * Games Played: 1
 * Correct First Attempts: 6
 * Correct Second Attempts: 2
 * Incorrect Attempts: 1
 * Score: 14 points
 * <p>
 * (A trailing newline is included by toString()).
 */
public final class Score
{

    // ------------- formatter must match test -------------
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ------------- fields -------------
    private final LocalDateTime dateTimePlayed;
    private final int gamesPlayed;
    private final int correctFirst;
    private final int correctSecond;
    private final int incorrect;

    // ------------- ctor -------------
    public Score(final LocalDateTime dateTimePlayed,
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

        this.dateTimePlayed = dateTimePlayed;
        this.gamesPlayed    = gamesPlayed;
        this.correctFirst   = correctFirst;
        this.correctSecond  = correctSecond;
        this.incorrect      = incorrect;
    }

    // ------------- accessors (only getScore is required by tests) -------------
    public LocalDateTime getDateTimePlayed()
    {
        return dateTimePlayed;
    }

    public int getGamesPlayed()
    {
        return gamesPlayed;
    }

    public int getCorrectFirst()
    {
        return correctFirst;
    }

    public int getCorrectSecond()
    {
        return correctSecond;
    }

    public int getIncorrect()
    {
        return incorrect;
    }

    /**
     * Required by tests: total points = 2*first + 1*second.
     */
    public int getScore()
    {
        final int firstPoints;
        final int secondPoints;

        firstPoints  = correctFirst * 2;
        secondPoints = correctSecond;

        return firstPoints + secondPoints;
    }

    /**
     * Must match the exact string asserted in tests.
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

    // ------------- file I/O required by tests -------------

    /**
     * Appends the score (via toString()) to the given file (path string).
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
     * Reads all scores previously written with appendScoreToFile().
     * Tolerant reader: looks for the 6 expected lines starting at a "Date and Time:" line.
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

    // ------------- tiny parsing helper -------------
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
}

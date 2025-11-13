package ca.bcit.comp2522.wordGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and stores all Country objects used by the Word Game.
 *
 * A World object reads every data file from 'a.txt' to 'z.txt'
 * located in the directory provided to the constructor. Each file
 * contains the name of a country, its capital, and three facts.
 *
 * Every Country created from these files is added to an internal map
 * where the key is the country name and the value is the Country object.
 *
 * This class is responsible only for building and providing access
 * to Country data. It does not perform gameplay logic.
 *
 * Author: Arshia Adamian
 * Version: 1.0
 */
public class World
{

    public final static int NUMBER_OF_WORDS_IN_FIRST_LINE = 2;

    private final Map<String, Country> countries;

    /**
     * Creates a World and loads all country data from the directory.
     * Looks for files named 'a.txt' through 'z.txt'. Any missing files
     * are simply skipped.
     *
     * Each file is expected to store:
     *   CountryName:CapitalCity
     *   Fact 1
     *   Fact 2
     *   Fact 3
     *
     * @param directory the directory containing the country data files
     *
     * @throws IOException if any file cannot be read
     */
    public World(final Path directory)
        throws IOException
    {
        countries = new HashMap<>();

        // loop from 'a' to 'z'
        for (char letter = 'a'; letter <= 'z'; letter++)
        {
            final Path filePath;
            filePath = Paths.get(directory.toString(), letter + ".txt");

            // if the file exists, read and parse it
            if (Files.exists(filePath))
            {
                readFile(filePath);
            }
        }
    }

    /**
     * Reads a single data file and creates a Country object from it.
     * Adds the Country to the internal map using the country's name
     * as the key.
     *
     * The first line must contain a country name and capital city,
     * separated by a colon. The next three lines are the country facts.
     *
     * @param filePath the path to the country data file
     *
     * @throws IOException if the file cannot be read
     */
    private void readFile(final Path filePath)
        throws IOException
    {
        try (BufferedReader reader = Files.newBufferedReader(filePath))
        {

            String line;

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                if (line.isEmpty())
                {
                    continue;
                }

                // Split first line: country:Capital
                final String[] parts;
                parts = line.split(":");

                if (parts.length != NUMBER_OF_WORDS_IN_FIRST_LINE)
                {
                    continue;
                }

                final String countryName;
                final String provinceName;

                countryName  = parts[0].trim();
                provinceName = parts[1].trim();

                // Read 3 facts below it
                final String fact1;
                final String fact2;
                final String fact3;

                fact1 = reader.readLine();
                fact2 = reader.readLine();
                fact3 = reader.readLine();

                if (fact1 == null || fact2 == null || fact3 == null)
                {
                    break;
                }

                final Country country;
                country = new Country(countryName, provinceName, fact1, fact2, fact3);

                countries.put(countryName, country);
            }
        }
    }

    /**
     * Returns the complete map of all loaded countries.
     * The keys are country names, and the values are Country objects.
     *
     * @return a map containing all countries in the World
     */
    public Map<String, Country> getCountries()
    {
        return countries;
    }

    /**
     * Retrieves a Country object by its name.
     * Returns null if the country is not found.
     *
     * @param countryName the name of the country to retrieve
     *
     * @return the Country object, or null if no match exists
     */
    public Country getCountry(final String countryName)
    {
        return countries.get(countryName);
    }

}


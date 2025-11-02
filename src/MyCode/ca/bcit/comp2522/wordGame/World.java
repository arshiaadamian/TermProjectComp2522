package ca.bcit.comp2522.wordGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * to be written
 *
 * @author Arshia Adamian
 * @version 1.0
 */
public class World
{

    public final static int NUMBER_OF_WORDS_IN_FIRST_LINE = 2;

    private final Map<String, Country> countries;

    /**
     * Builds the world by loading all country data files from the given directory.
     * Example: dataDir = Paths.get("data") where files are data/a.txt ... data/z.txt
     *
     * @param directory the folder containing a.txt ... z.txt
     * @throws IOException if any IO error occurs during loading
     */
    public World(final Path directory) throws IOException
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
     * reads a single country file and adds country objects to the map.
     */
    private void readFile(final Path filePath) throws IOException
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
     * Returns the map of countries.
     *
     * @return counties
     */
    public Map<String, Country> getCountries()
    {
        return countries;
    }

    /**
     * Retrieves a single country by name.
     */
    public Country getCountry(final String countryName)
    {
        return countries.get(countryName);
    }

}


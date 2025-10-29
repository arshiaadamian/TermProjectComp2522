package MyCode.ca.bcit.comp2522.wordGame;

/**
 * to be written
 *
 * @author Arshia Adamian
 * @version 1.0
 */
public class Country
{

    private final String name;
    private final String capitalCityName;
    private final String[] facts;

    /**
     * constructor to initialize the values of name, capitalCityName, fact1, fact2,
     * and fact3. It is also made to validate all the values given to it as well.
     *
     * @param name the name of the country
     * @param capitalCityName the capital name of the city.
     * @param fact1 fact number one about the country.
     * @param fact2 fact number two about the country.
     * @param fact3 fact number three about the country.
     */
    Country(final String name,
             final String capitalCityName,
             final String fact1,
             final String fact2,
             final String fact3)
    {
        wordGameValidator.validate(name, capitalCityName, fact1, fact2, fact3);
        this.name = name;
        this.capitalCityName = capitalCityName;
        facts = new String[3];
        facts[0] = fact1;
        facts[1] = fact2;
        facts[2] = fact3;
    }

}

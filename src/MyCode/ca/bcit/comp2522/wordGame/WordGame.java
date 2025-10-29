package ca.bcit.comp2522.wordGame;
/**
 * To be written ...
 *
 * @author Arshia Adamian
 * @version 1.0
 */
public class WordGame
{
    private final String name;
    private final String capitalCityName;
    private final String[] facts;

    WordGame(final String name,
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

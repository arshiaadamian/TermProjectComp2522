package ca.bcit.comp2522.wordGame;

/**
 * to be written ....
 *
 * @author Arshia Adamian
 * @version 1.0
 */
public class wordGameValidator
{
    public static void validate(final String name,
                                final String capitalCityName,
                                final String fact1,
                                final String fact2,
                                final String fact3)
    {
        if (name == null || name.isBlank())
        {
            throw new IllegalArgumentException("Name can not be empty");
        }

        if (capitalCityName == null || capitalCityName.isBlank())
        {
            throw new IllegalArgumentException("capitalCityName can not be empty");
        }

        if (fact1 == null || fact1.isBlank())
        {
            throw new IllegalArgumentException("fact1 can not be empty");
        }

        if (fact2 == null || fact2.isBlank())
        {
            throw new IllegalArgumentException("fact2 can not be empty");
        }

        if (fact3 == null || fact3.isBlank())
        {
            throw new IllegalArgumentException("fact3 can not be empty");
        }

    }
}

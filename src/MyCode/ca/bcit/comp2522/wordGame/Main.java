package ca.bcit.comp2522.wordGame;

import java.io.IOException;
import java.util.Scanner;

/**
 * Console entry point and menu loop for the games collection.
 * Only the Word game is implemented; other options print a placeholder.
 *
 * @author Arshia Adamian
 * @version 1.0
 */
public final class Main
{
    
    private static final char CMD_WORD = 'W';
    private static final char CMD_NUMBER = 'N';
    private static final char CMD_MYGAME = 'M';
    private static final char CMD_QUIT = 'Q';

    private Main()
    {
    }

    /**
     * Runs the main menu loop until the user chooses to quit.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(final String[] args)
    {
        final Scanner scanner;
        scanner = new Scanner(System.in);

        boolean running;
        running = true;

        while (running)
        {
            printMenu();
            System.out.print("Your choice: ");

            final String line;
            line = scanner.nextLine();

            if (line == null || line.isBlank())
            {
                System.out.println("Invalid input. Please enter W, N, M, or Q.");
                System.out.println();
                continue;
            }

            final char choice;
            choice = Character.toUpperCase(line.trim().charAt(0));

            if (choice == CMD_WORD)
            {
                try
                {
                    final WordGame game;
                    game = new WordGame(scanner);
                    game.run();
                }
                catch (final IOException ex)
                {
                    System.out.println("Error: could not start Word game (" + ex.getMessage() + ").");
                    System.out.println();
                }
            }
            else if (choice == CMD_NUMBER)
            {
                System.out.println("Number game is not implemented yet.");
                System.out.println();
            }
            else if (choice == CMD_MYGAME)
            {
                System.out.println("<Your game> is not implemented yet.");
                System.out.println();
            }
            else if (choice == CMD_QUIT)
            {
                running = false;
            }
            else
            {
                System.out.println("Invalid input. Please enter W, N, M, or Q.");
                System.out.println();
            }
        }

        System.out.println("Goodbye.");
    }

    private static void printMenu()
    {
        System.out.println("=== Main Menu ===");
        System.out.println("Press W to play the Word game.");
        System.out.println("Press N to play the Number game.");
        System.out.println("Press M to play the <your game's name> game.");
        System.out.println("Press Q to quit.");
    }
}

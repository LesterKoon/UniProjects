# The random module is imported to generate a random selection
import random

# Variables used throughout the program
FruitList = ['apple', 'orange', 'grape', 'melon'] # List of fruits that can be selected by both the CPU and user
CPUList = []  # List of the randomised fruits that the user needs to guess
UserList = []  # List of fruits guessed by the user
CheckerList1 = []  # Lists to check the user's list with the cpu's list
CheckerList2 = []
attempts = 0  # Initialize the number of attempts taken to guess all the fruits correctly
number_of_games = 1  # The number of the game/s currently being played
game = True  # Initial statement to allow the game to start

# A function created to print the main interface for the master mind game
def interface():
    print()
    print("                        M A S T E R    M I N D                        ")
    print("                         ===================                         ")
    print()
    print("\nWelcome to the Master Mind Game.\nThe CPU will randomly generate a list 4 fruits.\nTo win this game, you would need to guess each fruit from the list correctly.")
    print()
    print("                              R U L E S !                              ")
    print("                               ========                               ")
    print()
    print("\n1. Please guess 1 fruit at a time.")
    print()
    print("\n2. To win, all 4 fruits and their position must be correct.")
    print()
    print("\n3. The list of available fruits are: \n   -- apple, orange, grape and melon --")
    print()
    print("                               G A M E {}!                               ".format(number_of_games))
    print("                                 ======                                 ")

# A function to display the score of the user's guess to the randomized list
def display_score(correct_guess, correct_fruit):
    print("Correct fruit in the right position:", correct_guess)
    print("Correct fruit but wrong position:", correct_fruit)


# Directly calling the game's interface to be displayed to the user
interface()

# Generates a randomized list of fruits with repetition allowed from the fruits in the FruitList
CPUList = random.choices(FruitList, k=4)

# To start the Master Mind Game
while game == True:
    # Increments the number of attempts with every guess
    attempts += 1

    print("\nPlease enter fruits from the list above only.")
    # Loop to get the user's guesses
    while len(UserList) != 4:
        # Returns the user's input in lower case
        guess = str(input("Choice of fruit:").lower())
        if guess not in FruitList:  # Catches wrong inputs or input formats
            print(
                "\nInvalid Input! \nPlease enter only apple, orange, grape or melon. \n")
        else:
            UserList.append(guess)  # Adds valid input into the user's list
    print("\nYour guesses were:", UserList, "\n")

    # Variables used to calculate the score of each guess
    correct_guess = 0 #Both fruit and position are correct
    correct_fruit = 0 #The fruit is correct but its in the wrong position
    i = 0
    j = 0
    k = 0

    # Loop to calculate the guesses with the correct fruit and position
    while i < len(UserList):
        if UserList[i] == CPUList[i]:
            correct_guess += 1
        else:
            CheckerList1.append(UserList[i])
            CheckerList2.append(CPUList[i])
        i += 1

    # Loop to calculate the guesses with the correct fruit abut the wrong position
    while j < len(CheckerList1):
        k = 0
        while k < len(CheckerList2):
            if CheckerList1[j] == CheckerList2[k]:
                del CheckerList2 [k]
                correct_fruit += 1
                break
            else:
                k += 1
        j += 1

    # Statement when the user guess all the fruits and their position correctly
    if UserList == CPUList:
        if attempts == 1:
            print("                           You are a MASTERMIND !!!                           ")
            print("                     You guessed it on your first attempt.                     ")
            game = False
        else:
            print("                                 Well Done....                                 ")
            print("                   It took you {} attempts to  win the game.                      ".format(attempts))
            game = False
    # Statement to display the score of each guess.
    # It also empties the user's list and checker lists, which allows the player to attempt to guess the fruits again.
    else:
        display_score(correct_guess, correct_fruit)
        UserList = []
        CheckerList1 = []
        CheckerList2 = []
        print("\nWrong Guesss, please try again.")

    # Loop once the games end which prompts the user if they want to play again or exit the game
    while game == False:
        game_end = input("\nDo you want to play again (Y/N)?").upper()
        attempts = 0  # Resets the attempts for the new game
        # Progam will exit the loop and the game will end
        if game_end in ["N", "NO", "NOP", "NOPE", "NAH", "NUH"]:
            print('Goodbye! Thank You for Playing! (╹ᆺ╹)つ')
            break
        # Start a new game
        # Reset the cpu's list to generate a new list of randomised fruits.
        elif game_end in ["Y", "YE", "YES", "YEAH", "YEA", "YUP"]:
            UserList = []
            CPUList = []
            CPUList = random.choices(FruitList, k=4)
            number_of_games += 1
            game = True
            print("\nYay!!! Lets play again...")
            interface()
        else:  # Catches invalid inputs and ends the game
            print("Invalid Input! The game will end now....")
            break

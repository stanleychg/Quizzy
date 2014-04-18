Thank you MIT for inviting me to use Github for the 1st time! It is the first version control system that I have used. Even with this application, I am learning something new!

Quick Facts:
-All images are stored in res/drawable-mdpi/
-All layouts are stored in res/layouts
-App currently designed for Landscape use
-App currently optimized for mobile phones

Quizzy, a combination of index cards and a vocabulary building application, is split into two packages:
-wordgames.game
-wordgames.game.util

Package wordgames.game contains the .java files that are tied with the interfaced. Each .java file is one layout of the application. There are three layouts:

-QuizFront.java
Users enter this screen when the application starts. It has a menu bar at the top where users can add quizzes or import/export existing quizzes from and to the SD card. Below the menu bar is a grid showing every quiz on the phone, excluding in the SD card. When an existing quiz is touched, the app presents a dialog with the option to modify, play with, or delete the quiz.

-QuizMaker.java
When users add a new quiz or modify an existing one, they are brought to this screen. The screen, similar to QuizFront.java, has a menu bar. In the right corner, users can add new words. Below the bar are where the words are displayed with their corresponding definition. The code keeps track of the # of words and displays it on the bottom right corner.

-QuizGame.java
When users chooses to play with a quiz from the dialog in QuizFront.java, they are show ten words and a random definition. The user guesses the word that matches the definition by touching the word. If it is correct, the definition and word changes. If it is incorrect, the definition and word both remain.


Package wordgames.game.util contains all the objects. Some notable .java files:

-WordPair.java
WordPair is an object containing a word and its matching definition

-Quiz.java
Quiz is an ArrayList<WordPair> Object

-QuizManager.java
QuizManager is an ArrayList<Quiz> Object

-DatabaseQuiz.java
DatabaseQuiz generates a SQLITE database to store a Quiz

-DatabaseQuizManager.java
DatabaseQuizManager provides simpler functions to manage a DatabaseQuiz

-DatabaseQuizList.java
DatabaseQuizList references an arbitrary SQLITE database that stores the name of each Quiz

-DatabaseQuizListManager
DatabaseQuizListManager provides simpler functions to manage a DatabaseQuizList

This application is fully functional; in that sense, it is "complete." However, I am continuously improving this application.

Future Updates/Wishlist:
-Tutorial for game
-Improved UI design for game + dialogs
-Better game design + additional game modes
-Improved graphics + app icon
-Standardized font sizes, color schemes, font styles
-Ability for users to categorize of quizzes
-Sound effects
-Optimization for Portrait use
-Optimization for tablet use


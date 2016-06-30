# quizzard-app

Team Members: Abe Fraifeld, Cindy Hom, Brittany Logan, Maya McCoy

ch991QuizzardFinal.zip contains all of the java and xml files, as well as the image icons necessary to run the project. The other java files in the repository are here to show a quick look at some of the code since there are a lot of files.

Instructions for running:
1. Log in to EC2 (public IP: 54.174.83.66)
2. cd into hadoop-2.6.4
3. Run the Quizzard Server using the command: bin/hadoop jar /home/ubuntu/QuizzardServer.jar 9003
    Note: 9003 is the port numver we have hard coded for the connection
4. Run the Android application

App Overview:
Android application that functions as a quiz game for users. Upon login, users are given two options -- either they are able to search quizzes or they can create their own quiz. The user interface for the quiz creation is still in progress. Upon a user's search of quizzes, they are taken to a view on which they can choose from a list of quiz categories, such as science, math, and computer science. Based on which of these buttons they click, the application produces a ListView populated with quizzes specific to this subject. Our data file contains quizzes mainly in Science and Social Studies. When the user clicks on a quiz in the ListView, they are given a list of questions and once they submit their answers they can see their results for the quiz. The intent for this app was to serve as a game and/or study tool for users.

MapReduce and Hadoop:
A large data file full of quizzes was uploaded to HDFS and is accessed using a quiz tag searcher MapReduce function. THis MapReduce function takes the entire list of quizzes of all types and returns a smaller list of quizzes that corresponds to the quiz tag that was queried (from the page of tags on the Android application). The MapReduce functions communicate with the Android app through a Socket connection. The server retrieves a request form the client as to what MapReduce function to call and with what tag and sends this information to the MapReduce functions. The MapReduce function then returns an output of the smaller list of quizzes, which the server then sends to the client through a byte array. The output file is then read by the client and used to populate the ListView of quizzes.

Android User Interface:
The user interface was designed for users to be able to logically navigate through the process of searching for and taking a quiz. The list of tags is a list of clickable icons that correspond to the different tags that trigger MapReduce functions. The ListView of quizzes is scrollable and allows users to scroll through to find the quiz that they would like to take. The questions are multiple choice and users click on radio buttons and submit their answers, after which they are presented with their results and able to navigate back to the list of tags to take more quizzes.

Quiz Objects:
The list of quizzes from MapReduce is parsed into Quiz objects with the following data members: tag, title, list of question objects, quiz author, high score, and high scorer. The question objects include a question, a list of answers, and a correct answer.

Future Improvements:
We worked on allowing users to create their own quizzes, and we were able to get this functionality working on the command line. If you run the QuizzardTestClient file on the command line and call the command create: and then insert a quiz script. 

The quiz script must follow the format of the data file, which includes "///" delimiters between each of the items. For example, if you wanted to create a quiz with two questions you could call:

create:$ Art     ///Art History 2 ///Where is Botero from? ///Turkey ///Nashville ///Colombia ///Spain ///Colombia ///Where is El Grecos House? ///Greece ///Nashville ///Toledo, Spain ///Toledo, OH ///Toledo, Spain ///Abe Fraifeld ///20 ///Brittany Logan

In the future it would be good to allow for quizzes of different types, not just multiple choice. This would allow users to create and take quizzes that have a wide range of difficulty and could be used as a study tool for users. The MapReduce also could be faster, as it now uses a linear search algorithm in order to search for quizzes.

We also included high score and high scorer member fields in the Quiz objects, and therefore it would be a good improvement to have users log in and their information could be saved to specific quizzes if they achieved a high score.

Quizzard Main Page            |  Quizzard Tag Page
:-------------------------:|:-------------------------:
![Quizzard Main Page](https://cloud.githubusercontent.com/assets/17078052/16494308/d88721ec-3eb6-11e6-8863-25d5960b6eb5.png)  |   ![Quizzard Tag Page](https://cloud.githubusercontent.com/assets/17078052/16494322/edfed2e0-3eb6-11e6-9691-94fbce1ab7d4.png)


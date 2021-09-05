# MasterThesis

This github contains program made for the experiments for my masterthesis and includes all described implementations. It can be used to solve a game with any of the 3 versions of Zielonka's algorithm. First we will first describe the format of the input files. There are 2 ways to use the program. The first way is to compile the code and start the program with command line commands. The commands and output will be described below. The second way is to import the code in an IDE like Intelij and then run the code using Junit tests. This way will be described at the bottom. The code requires maven and Java to compile.

# Program input file format
The program only accepts .gm files. These files must have the following format:
 - The first line is: 
 
        parity X;
        
  , where X is the number of the highest vertex
    
- Optionally the second line can have the format: 
    
      start Y;
    
  , with Y any number. This line will be ignored by the program.
    
- Then there is a line for each vertex in the game of the format: 
    
      index parity owner outedges "optional label";
    
  , where index must increment by 1 each line, so 0 in the first line, 1 in the second line, 2 in the third line etc. parity can have at most 8 digits, if the parity is larger than 8 digits only the smallest 8 digits will be used. owner can either be 0 or 1. outedges are a list of outedges of the format 1,2,3,4,5,etc of comma separated target vertices without or without spaces in between. "optional label" is a label that can be present, but will not be used by the program. It must be put between quotes.

- Optionally the last line can be of the format:
    
      timeout....}
    , where .... can be replaced with anything. Any line starting with timeout will be ignored.
    
    
# Program with command line
To use the program with command line, first navigate to the MasterThesis folder and open the command line. Compile with the following command:

      mvn package
  
Then the code can be executed with the following command:

      java -cp ".\zielonka\target\zielonka-1.0-SNAPSHOT.jar;.\common\target\common-1.0-SNAPSHOT.jar" org.anhu.Application severalArguments yourFilOrFolderURL

In this line severalArguments can be replaced with one or more arguments with spaces in between. The following arguments can be used:

- a: print the answer of the game
- i: print all additional information
- t: print the timing of Zielonka's algorithm
- h: print all commands
- F: file specified is a folder, runs all .gm files found in the specified folder
- D: run Zielonka's algorithm with dynamic SCC maintenance
- P: run Zielonka's algorithm with partial re-decomposition
- T: run Zielonka's algorithm with Tarjan's algorithm

The yourFilOrFolderURL argument can be replaced by the URL of the game, or the URL of a folder if F is present in severalArguments. 

# Program with Junit
The second option is to import the code into an IDE and use Junit tests to solve the games. All experiments used the Junit test method. This second method does not have as many options as the command line version. It always runs all 3 versions of Zielonka's algorithm on the game. When solving all games in a folder it will solve them in order of size and cut them off if they take more than roughly 2 hours. When solving just a single game the game will not be cut off after 2 hours. 

To run the code in an IDE using Junit:

- Go to Zielonka\src\test\Application
- To run a single file enter the file URL in the file variable and run the runFile() test case.
- To run all .gm files in a folder enter the folder URL in the folder variable and run the runAllFilesInFolder() test case In this test case. When using this test case there must be a folder named _results present in which the results will be stored.

The runAllFilesInFolder test case will print the timing and statistics used in the experiments. Next to these it will write the answer to _results\Answer_dateTime.txt, the statistic details are written to _results\dateTime.txt and _results\Excel_dateTime.txt  will contain the same results but in a line that can be copied into excel with variables in the following order:
name	#vertices	#trivial_SCC	#non_trivial_SCC	size_longest_tree	#tarjan_calls	#vertices_tarjan	#vertices_partial	#nodes_updated	time_tarjan	time_partial_prep	time_partial_tarjan	time_make_tree	time_update_decomposition	runtime_tarjan	runtime_partial	runtime_dynamic






package assignment3;
//Java imports
import java.util.*;
import java.io.*;

public class Main {

    public static final boolean DEBUG = false;

    public static ArrayList<String> dictionary;

    // static variables and constants only here.

    public static void main(String[] args) throws Exception {
        Scanner kb;	// input Scanner for commands
        PrintStream ps;	// output file, for student testing and grading only
        // If arguments are specified, read/write from/to files instead of Std IO.
        if (args.length != 0) {
            kb = new Scanner(new File(args[0]));
            ps = new PrintStream(new File(args[1]));
            System.setOut(ps);			// redirect output to ps
        } else {
            kb = new Scanner(System.in);// default input from Stdin
            ps = System.out;			// default output to Stdout
        }
        initialize();

        System.out.println("Project 3: Word Ladders");

        while(3>2){
            System.out.println("Please input two words (or type /quit to exit)");
            ArrayList<String> inputList = parse(kb);

            if(inputList.get(0).equals("/quit")){
                System.exit(0);
            }

            if(inputList.get(0).equals("INVALID")){
                System.out.println("Invalid Input");
                continue;
            }

            String start = new String(inputList.get(0));
            String end = new String(inputList.get(1));

            ArrayList<String> output = getWordLadderBFS(start, end);
            if(output == null){
                output = new ArrayList<String>();
                output.add(start);
                output.add(end);
            }
            printLadder(output);
        }
    }

    public static void initialize() {
        // initialize your static variables or constants here.
        // We will call this method before running our JUNIT tests.  So call it
        // only once at the start of main.
        Set<String> dict = makeDictionary();
        dictionary = new ArrayList<String>(dict);
    }



    /**Parse method --Liuxx edit
     * This method takes input from keyboard and determine if the input is valid
     * @param keyboard Scanner connected to System.in
     * @return ArrayList of Strings containing start word, rungs, and end word.
     * If command is /quit, return empty ArrayList.
     */
    public static ArrayList<String> parse(Scanner keyboard) {
        if(DEBUG){
            System.out.println("Parse Test");
            System.out.println("Please enter something:");
        }
        ArrayList<String> inputList = new ArrayList<String>();

        while(keyboard.hasNextLine()){
            String newLine = keyboard.nextLine();
            Scanner lineScan = new Scanner(newLine);
            String word;

            int count = 0;
            while(lineScan.hasNext()){
                word = lineScan.next().toLowerCase();
                count ++;
                inputList.add(word);

                if(DEBUG){
                    System.out.print("Input: ");
                    System.out.println(word);
                }
            }

            if(count != 2){
                if(DEBUG){
                    System.out.println("Input less/more than two words");
                }

                if(count == 1){
                    if(inputList.get(0).toLowerCase().equals("/quit")){
                        break;
                    }
                    else{
                        inputList.set(0, "INVALID");
                        break;
                    }
                }
                if(count >2){
                    inputList.set(0, "INVALID");
                    break;
                }

            }

            if(count == 2)
                break;
        }
        return inputList;
    }

    public static ArrayList<String> getWordLadderDFS(String start, String end) {

        ArrayList<String> temp = recursiveDFS(start.toUpperCase(), end.toUpperCase());

        initialize();
        
        if(temp == null){
			ArrayList<String> failList = new ArrayList<String>();
			failList.add(start);
			failList.add(end);
			return failList;
		}

        return temp;
    }

    private static ArrayList<String> recursiveDFS(String start, String end){
        // Returned list should be ordered start to end.  Include start and end.
        // If ladder is empty, return list with just start and end.
        ArrayList<String> answer = new ArrayList<String>();
        ArrayList<String> edges = fillEdges(start);
        if(edges.contains(end)){
            answer.add(start.toLowerCase());
            answer.add(end.toLowerCase());
            return answer;
        }
        dictionary.remove(start);
        while(!edges.isEmpty()){
            ArrayList<String> temp = recursiveDFS(edges.get(0), end);
            if(temp != null){
                answer.add(start.toLowerCase());
                answer.addAll(temp);
                return answer;
            }
            edges.remove(0);
        }
        return null;
    }


    public static ArrayList<String> fillEdges(String str){
        ArrayList<String> temp = new ArrayList<String>();
        for(int i = 0; i < dictionary.size(); i++){
            if(!str.equals(dictionary.get(i))){
                for(int j = 0; j < 5; j++){
                    if(str.substring(0,j).equals(dictionary.get(i).substring(0,j)) && str.substring((j+1),5).equals(dictionary.get(i).substring((j+1),5))){
                        temp.add(dictionary.get(i));
                    }
                }
            }
        }
        return temp;
    }


    /**BFS method of word ladder --Liuxx edit
     * This method computes the shortest word ladder by using breadth first search
     * @param start Strings as the start word and the end word
     * @return Word Ladder (ArrayList of Strings)
     */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {

        ArrayList<String> newList = new ArrayList<String>();

        //Check the length first
        if(start.length() != end.length()){
            newList.add("INVALID ERROR");
            return newList;
        }

        //Check if there is a word in the input string
        if(start.length() == 0){
            newList.add("INVALID ERROR");
            return newList;
        }

        //Setup dictionary
        Set<String> dict = makeDictionary();

        if(dict.isEmpty()){
            newList.add("INVALID ERROR");
            return newList;
        }

        //Setup for BFS
        //ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> temp = createBFS(start, end, dict);

        if(temp.isEmpty()){
			ArrayList<String> failList = new ArrayList<String>();
			failList.add(start);
			failList.add(end);
			return failList;
		}

        LinkedList<String> wordLadder = new LinkedList<String>();
        wordLadder.add(end);

        backTrackItr(start, end, wordLadder, temp);

        ArrayList<String> words = new ArrayList<String>(wordLadder);

        return words;
    }

    /**createBFS --Liuxx edit
     * This is a private helper function that generates the BFS Graph/Tree
     * @param start Strings and dictionary
     * @return BFS Tree (ArrayList<ArrayList<String>>)
     */
    private static ArrayList<ArrayList<String>> createBFS(String start, String end, Set<String> dict){
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        Queue<String> temp1 = new LinkedList<String>();
        Queue<Integer> temp2 = new LinkedList<Integer>();

        temp1.add(start);
        temp2.add(0);
        dict.remove(start);

        //the first level of array list
        ArrayList<String> first = new ArrayList<String>();
        first.add(start);
        temp.add(first);
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        while(!temp1.isEmpty()){
            String str = temp1.poll();
            int level = temp2.poll();

            for(int i = 0; i < str.length(); i++){
                //The execution bound of this for loop should be 26
                for(int j = 0; j < alphabet.length; j++){
                    if(str.charAt(i) == alphabet[j]){
                        //go to next iteration
                        continue;
                    }

                    //Create the temporary word
                    //The temp word differs the original word by one character
                    char[] tempChar = str.toCharArray();
                    tempChar[i] = alphabet[j];
                    String tempString = String.valueOf(tempChar);

                    //Check if the Ladder has reach to the end
                    if(tempString.equals(end)){
                        //The ladder has been found
                        //return
                        if(temp.size() == level + 2){
                            temp.remove(temp.size() - 1);
                        }

                        return temp;

                    }

                    //Check if the dict contains the word
                    if(dict.contains(tempString.toUpperCase())){
                        //the word is in dict
                        //push it into the queue and delete it from dict
                        temp1.add(tempString);
                        temp2.add(level + 1);
                        dict.remove(tempString.toUpperCase());

                        if(temp.size() == level + 1){
                            temp.add(new ArrayList<String>());
                        }

                        temp.get(level + 1).add(tempString);
                    }
                }
            }
        }
        return new ArrayList<ArrayList<String>>();
    }



    /**match --Liuxx edit
     * This is a private helper function that check if two words match (differ by one character)
     * @param x String words
     * @return true or false
     */
    private static boolean matchCheck(String x, String y){
        if(x.length() != y.length())
            return false;

        int count = 0;

        for(int i = 0; i < x.length(); i++){
            if(x.charAt(i) != y.charAt(i)){
                count ++;
            }
        }

        return (count == 1);
    }

    /**backTrackItr --Liuxx edit
     * This is a private helper function modify the ArrayList to the final word ladder
     * Back track the BFS Tree in order to get correct word ladder
     */
    private static void backTrackItr(String start, String end, LinkedList<String> ladder, ArrayList<ArrayList<String>> tree){
        int level = tree.size();
        String str = end;

        for(int i = level - 1; i >= 0; i--){
            for(int j = (tree.get(i).size() - 1); j >= 0; j--){
                String tempWord = tree.get(i).get(j);
                if(matchCheck(tempWord, str)){
                    str = tempWord;
                    ladder.push(tempWord);
                    break;
                }
            }
        }
    }


    /**backTrackRec --Liuxx edit
     * This is a private helper function modify the ArrayList to the final word ladder
     * Back track the BFS Tree using recursion in order to get correct word ladder
     */
    private static boolean backTrackRec(String end, int level, ArrayList<String> temp, ArrayList<ArrayList<String>> levelList){
        if (level == levelList.size()){
            if(matchCheck(temp.get(temp.size() - 1), end)){
                temp.add(end);
                /**
                 res.add(new ArrayList<String>(temp));
                 temp.remove(temp.size() - 1);
                 */
                return true;
            }
            return false;
        }
        else{
            ArrayList<String> wordlist = levelList.get(level);
            for(int i=0; i < wordlist.size(); i++){
                String word = wordlist.get(i);

                //check if it matches
                if (matchCheck(temp.get(temp.size() - 1), word)){
                    temp.add(word);
                    boolean check = backTrackRec(end, level + 1, temp, levelList);
                    if(check){
                        return true;
                    }
                    temp.remove(temp.size() - 1);
                }
            }
            return false;
        }
    }


    /**Print Ladder --Liuxx edit
     * this function print the ladder to the console
     */
    public static void printLadder(ArrayList<String> ladder) {
        String start = ladder.get(0);
        String end = ladder.get(ladder.size() - 1);

        if(ladder.size() == 2){
            System.out.println("no word ladder can be found between " + start + " and "
                    + end + ".");
            return;
        }

        System.out.println("a " + ladder.size() +"-rung word ladder exists between " + start + " and "
                + end + ".");
        for(int i = 0; i < ladder.size(); i++){
            System.out.println(ladder.get(i));
        }
    }
    // TODO
    // Other private static methods here

    /* Do not modify makeDictionary */
    public static Set<String>  makeDictionary () {
        Set<String> words = new HashSet<String>();
        Scanner infile = null;
        try {
            infile = new Scanner (new File("five_letter_words.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File not Found!");
            e.printStackTrace();
            System.exit(1);
        }
        while (infile.hasNext()) {
            words.add(infile.next().toUpperCase());
        }
        return words;
    }
}
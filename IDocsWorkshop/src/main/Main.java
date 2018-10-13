package main;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);


    private static ArrayList<ArrayList> moviesInfo1 = new ArrayList<>();
    private static ArrayList<ArrayList> moviesInfo2 = new ArrayList<>();
    private static ArrayList<ArrayList> moviesInfo3 = new ArrayList<>();

    private static ArrayList<Integer> percentagePerson1 = new ArrayList<>();
    private static ArrayList<Integer> percentagePerson2 = new ArrayList<>();
    private static ArrayList<Integer> percentagePerson3 = new ArrayList<>();

    private static HashMap<String, Integer> getGenreMap1 = new HashMap<>();
    private static HashMap<String, Integer> getGenreMap2 = new HashMap<>();
    private static HashMap<String, Integer> getGenreMap3 = new HashMap<>();


    private static String[][] orderedMoviesAndMinWatchedList;
    private static final String txtSuffix = ".txt";

    public static void main(String[] args) {

        System.out.println("------------STARTING APP------------");


        String regex = "^(\\d+)/(\\d+)/(\\d+) (.*) (\\d+)min (\\d+)min (\\S+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;




/*
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv(args[0])));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        int totalMovies = 0; //used for Exc#1

        for (int argNum = 0; argNum < args.length - 1; argNum++) {  //For each of the three input files

            try (BufferedReader br = new BufferedReader(new FileReader("inputFiles\\" + args[argNum] + txtSuffix))) {

                String sCurrentLine;

                //For each file use different list
                ArrayList currentList = getMovieList(argNum);
                if (currentList == null) {
                    printError("Wrong Argument Number");
                }

                while ((sCurrentLine = br.readLine()) != null) {

                    matcher = pattern.matcher(sCurrentLine);
                    if (matcher.matches()) {
                        //Create a new Array list for the next movie of that person
                        ArrayList<String> newMovieList = new ArrayList<>();
                        currentList.add(newMovieList);
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            newMovieList.add(matcher.group(i));
                        }
                        totalMovies++;
                    }
                }

            } catch (IOException e) {
                System.err.println("Error Reading File: " + args[0]);
                e.printStackTrace();
            }

        }


        //EXERCISE #1
        findMoviesAndMinWatched(totalMovies);

        //EXERCISE #2
        findPercentagesWatched();

        //EXERCISE #3
        findFavoriteGenres();

        printToOutput(args, totalMovies);

        //String[] nk = scanner.nextLine().split(" ");


        System.out.println("------------THE END------------");
    }

    private static void findFavoriteGenres() {


        for (int argNum = 0; argNum < 3; argNum++) {  //For each of the three input files

            ArrayList currentList = getMovieList(argNum);
            ArrayList percentageList = getPercentageList(argNum);
            HashMap<String, Integer> genresMap = getGenreMap(argNum);

            //Get inner lists
            for (int innerListIndex = 0; innerListIndex < currentList.size(); innerListIndex++) {
                if (Integer.parseInt(String.valueOf(percentageList.get(innerListIndex))) >= 60) {    //Add only those with 60% or more
                    ArrayList innerList = (ArrayList<String>) currentList.get(innerListIndex);
                    if (genresMap.get(innerList.get(6)) == null) {
                        genresMap.put(String.valueOf(innerList.get(6)), 1); //First time inserting that genre
                    } else {
                        genresMap.put(String.valueOf(innerList.get(6)), genresMap.get(innerList.get(6)) + 1);   //Add +1 frequency to that that genre
                    }
                }
            }
        }
    }

    private static HashMap<String, Integer> getGenreMap(int argNum) {
        switch (argNum) {
            case 0:
                return getGenreMap1;
            case 1:
                return getGenreMap2;
            case 2:
                return getGenreMap3;
            default:
                return null;
        }
    }

    /**
     * Finds percentages (rounded down)
     */
    private static void findPercentagesWatched() {

        for (int argNum = 0; argNum < 3; argNum++) {  //For each of the three input files

            ArrayList currentList = getMovieList(argNum);
            ArrayList percentageList = getPercentageList(argNum);

            //Get inner lists
            for (int innerListIndex = 0; innerListIndex < currentList.size(); innerListIndex++) {
                ArrayList innerList = (ArrayList<String>) currentList.get(innerListIndex);
                double percentage = Double.parseDouble(String.valueOf(innerList.get(5))) / Double.parseDouble(String.valueOf(innerList.get(4)));
                percentageList.add((int) (percentage * 100));
            }
        }
    }

    private static void printToOutput(String[] args, int totalMovies) {
        //Write to output file

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("inputFiles\\" + args[3] + txtSuffix), "utf-8"));
            writer.write("ORDERED LIST: ");
            writer.write(System.getProperty("line.separator"));

            //EXERCISE #1
            for (int row = 0; row < totalMovies; row++) {
                for (int col = 0; col < 2; col++) {
                    writer.write(orderedMoviesAndMinWatchedList[row][col]);
                    if (col == 0) {
                        writer.write(" ");
                    } else {
                        writer.write(System.getProperty("line.separator"));
                    }
                }
            }

            //EXERCISE #2
            writer.write(System.getProperty("line.separator"));
            writer.write(System.getProperty("line.separator"));

            for (int personIndex = 0; personIndex < 3; personIndex++) {
                writer.write(args[personIndex] + " MOVIES PERCENTAGES:");
                writer.write(System.getProperty("line.separator"));

                ArrayList<Integer> percentageList = getPercentageList(personIndex);
                int totalPercentagePerPerson = 0;


                for (int innerListIndex = 0; innerListIndex < percentageList.size(); innerListIndex++) {
                    int percentage = percentageList.get(innerListIndex);
                    writer.write("Movie: " + ((ArrayList<String>) getMovieList(personIndex).get(innerListIndex)).get(3) +
                            " | Percentage Watched: " + percentage);
                    writer.write(System.getProperty("line.separator"));

                    totalPercentagePerPerson += percentage;
                }

                //Find total
                //rounding down percentages, decimal points do not make any sense here (else use double and limit to 1 decimal digits)
                writer.write("Total Percentage: " + totalPercentagePerPerson / percentageList.size());

                writer.write(System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
            }

            //EXERCISE #3
            for (int personIndex = 0; personIndex < 3; personIndex++) {
                writer.write(args[personIndex] + " FAVORITE GENRE");
                writer.write(System.getProperty("line.separator"));

                HashMap<String, Integer> genreMap = getGenreMap(personIndex);
                int maxFreq = new TreeSet<Integer>(genreMap.values()).last(); //Sort by value

                //Iterate hashmap to find which moves have that value
                for (Map.Entry<String, Integer> entry : genreMap.entrySet()) {
                    if (entry.getValue() == maxFreq) {
                        writer.write("Genre: " + entry.getKey() + " | Frequency: " + entry.getValue());
                        writer.write(System.getProperty("line.separator"));
                    }
                }

                writer.write(System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            printError("Cannot Write to Output File");
        } catch (FileNotFoundException e) {
            printError("Cannot Write to Output File");
            e.printStackTrace();
        } catch (IOException e1) {
            printError("Cannot Write to Output File");
            e1.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private static ArrayList getMovieList(int argNum) {
        switch (argNum) {
            case 0:
                return moviesInfo1;
            case 1:
                return moviesInfo2;
            case 2:
                return moviesInfo3;
            default:
                return null;
        }

    }

    private static ArrayList<Integer> getPercentageList(int argNum) {
        switch (argNum) {
            case 0:
                return percentagePerson1;
            case 1:
                return percentagePerson2;
            case 2:
                return percentagePerson3;
            default:
                return null;
        }

    }

    private static void findMoviesAndMinWatched(int totalMovies) {

        orderedMoviesAndMinWatchedList = new String[totalMovies][2];
        int movieIndex = 0;

        //For each one of the three lists
        for (int personIndex = 0; personIndex < 3; personIndex++) {
            ArrayList currentList = getMovieList(personIndex);
            {
                //Get inner lists
                for (int innerListIndex = 0; innerListIndex < currentList.size(); innerListIndex++) {
                    ArrayList innerList = (ArrayList<String>) currentList.get(innerListIndex);
                    orderedMoviesAndMinWatchedList[movieIndex][0] = String.valueOf(innerList.get(3));
                    orderedMoviesAndMinWatchedList[movieIndex][1] = String.valueOf(innerList.get(5));
                    movieIndex++;
                }
            }
        }

        //Sort list ordered by the second column which is minutes watched
        java.util.Arrays.sort(orderedMoviesAndMinWatchedList, new java.util.Comparator<String[]>() {
            public int compare(String[] a, String[] b) {
                return Integer.compare(Integer.parseInt(a[1]), Integer.parseInt(b[1]));
            }
        });
    }


    private static void printError(String errorMsg) {
        System.err.println(errorMsg);
        System.err.println("Closing Program");
        System.exit(1);
    }
}

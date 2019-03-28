package app;

/**
 * @author: Tjeerd van der Veen & Sanne Schroduer
 * @date: 29-03-2019
 *
 * Note: known bug for choosing the directory for locally saving a CSV file with ORF results.
 * The implemented exception handling for this method only works properly on Windows OS.
 */


import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * This class contains all the functional methods for finding the ORF's
 */
public class ORFfinder {

    static File outputFile;
    static BufferedWriter bw;
    static String sequence;

    private static ArrayList<ArrayList<Integer>> orfs, reverseORFs;
    static HashMap<Integer, ArrayList<String>> resultsMap;
    private static HashMap<String, String> CodonTable = new HashMap<>();
    private static DatabaseConnection Connector = new DatabaseConnection();

    static int DNA_Hashcode;

    /**
     * Method that calls the method for the initialization of the GUI and makes the codontabel
     * @param args
     */
    public static void main(String[] args) {
        CodonTable=codonTable.makeCodonTable(CodonTable);
        initialiseGUI();
    }

    /**
     * Method that initializes the GUI
     */
    static void initialiseGUI() {
        orfGUI frame = new orfGUI();
        frame.setTitle("ORF finder application");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Method that controls if the input file is fasta format
     * @param fileName String of the path to the chosen file
     * @return
     */
    public static String controlFormat(String fileName) throws NoFastaFormatException{
        String header = "";
        try {
            BufferedReader inFile = new BufferedReader(new FileReader(fileName));
            String lineZero = inFile.readLine();

            if(lineZero.startsWith(">")) {
                header = lineZero;
                getSeq(fileName);
                //test
            } else {
                throw new NoFastaFormatException();
            }

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"Error: File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Error: IOException. Something went wrong while analyzing the sequence. \n Contact the system administrator.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,"Error: Something went wrong while controlling your file. \n Make sure you have uploaded a file.");
        }
    return header;
    }

    /**
     * Method that extracts the DNA sequence from the input file
     * @param fileName String path to the chosen file
     * @return
     */
    public static String getSeq(String fileName) {

        sequence = "";
        try {
            BufferedReader inFile = new BufferedReader(new FileReader(fileName));
            String line1 = inFile.readLine();
            String line;

            while ((line = inFile.readLine()) != null) {
                sequence += line;
            }
        sequence = sequence.toUpperCase();

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"Error: File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Error: IOException. Something went wrong while analyzing the sequence. \n Contact the system administrator.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return sequence;
    }

    /**
     * Method for analyzing given DNA code and finding the ORFs
     */
    public static HashMap analyse(boolean startIsATG){
        try {
            DNA_Hashcode = sequence.hashCode();
            System.out.println("hashcode: " + DNA_Hashcode);
            orfs = new ArrayList<>();
            reverseORFs = new ArrayList<>();

            orfs = findORFs(sequence, orfs, 0, 75, startIsATG);
            String reverseSequence = reverseString(sequence);
            reverseORFs = findORFs(reverseSequence, reverseORFs, 0, 75, startIsATG);

            ArrayList dnaORFlist = getORF_DNA_Sequence(orfs, sequence);
            dnaORFlist.addAll(getORF_DNA_Sequence(reverseORFs, reverseSequence));

            ArrayList proteinList = DNAtoAA(CodonTable, dnaORFlist);
            reverseORFs = adjustReverseOrfs(sequence.length(), reverseORFs);
            orfs.addAll(reverseORFs);
            resultsMap = fillResultsMap(proteinList, dnaORFlist, orfs);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,"Error: Something went wrong while analyzing the sequence. \n Make sure you have uploaded a file");
        }
        return resultsMap;
    }


    /**
     * Function to find ORFs within a DNA sequence
     * @param sequence String containing the DNA sequence you want to
     * @param orfs ArrayList containing ArrayLists of integers
     * @param start starting position to start looking for ORFs in the given DNA sequence
     * @param minDistance minimum distance between starting position and stop position
     * @param startIsATG boolean that decides wether ATG is used to find ORFs or not
     * @return returns an ArrayList containing an ArrayList of the starting and stopping positions of the found ORFs
     */
    public static ArrayList findORFs(String sequence, ArrayList orfs, int start, int minDistance, boolean startIsATG){

        int startIndex=start;
        int[] stops={sequence.indexOf("TAA",startIndex),sequence.indexOf("TGA",startIndex),sequence.indexOf("TAG",startIndex)};
        Arrays.sort(stops);//sort the indexes of stop codons so the lowest and highest values are identified

        while(stops[0]!=-1 && stops[1]!=-1 && stops[2]!=-1) {//checks if there are any stop codons found
            startIndex = start;
            if (startIsATG) {//checks if ATG is used as the starting point
                startIndex = sequence.indexOf("ATG", start);
            }
            int[] stopsTemp = {sequence.indexOf("TAA", startIndex), sequence.indexOf("TGA", startIndex), sequence.indexOf("TAG", startIndex)};
            stops = stopsTemp;
            Arrays.sort(stops);//sort the indexes of stop codons so the lowest and highest values are identified

            if (!startIsATG) {//checks if ATG is not used as the starting point
                startIndex = stops[0];
            }
            if (stops[0] != -1 && stops[1] != -1 && stops[2] != -1) {//checks if there are any stop codons found
                checkORFLength(startIndex, stops[2], stops[1], stops[0], minDistance, sequence, orfs);//checks if the ORF is long enough and in the stop codons are in the right reading frame
                //orfs = findORFs(sequence, orfs, start, minDistance, startIsATG);
                start = startIndex + 1;
            }
        }
        return orfs;
    }

    /**
     * Method that reverses DNA to the reverse complementary sequence
     * @param input string to be reversed
     * @return returns a String that is the reversed complementary DNA sequence of the input String
     */
    public static String reverseString(String input) {
        char[] toRevert = input.toCharArray();
        ArrayList<Character> reversedList = new ArrayList<>();
        for (char i : toRevert){
            char x;
            if(i=='T'){
                x='A';
            }else if(i=='A'){
                x='T';
            }else if (i=='G'){
                x='C';
            }else if(i=='C'){
                x='G';
            }else{
                x='N';
            }
            reversedList.add(x);
        }
        Collections.reverse(reversedList);
        return reversedList.stream().map(String::valueOf).collect(Collectors.joining());
    }

    /**
     * Method that adjusts ORF start and stop position according to their position on reverse contemplary sequence
     * @param sequenceLenght length of the DNA sequence
     * @param ORFlist list of ORFs to be adjusted
     * @return Arraylist containing arraylist with the adjusted start and stop values of the given list.
     */
    private static ArrayList<ArrayList<Integer>> adjustReverseOrfs(int sequenceLenght, ArrayList<ArrayList<Integer>> ORFlist){
        for(int i=0; i<ORFlist.size(); i++){
            int start = ORFlist.get(i).get(0)-sequenceLenght;
            int stop = ORFlist.get(i).get(1)-sequenceLenght;
            ArrayList<Integer> startStopList = new ArrayList<>();
            startStopList.add(start);
            startStopList.add(stop);
            ORFlist.set(i, startStopList);
        }
        return ORFlist;
    }


    /**
     * Method to check if the found stop positions match the reading frame of the start codon and the stop codon is far enough away
     * @param start integer with the position of the start codon
     * @param stopFurthest integer with the position of the stop codon found at the biggest position
     * @param stopMiddle integer with the position of the second furthest position
     * @param stopClosest integer with the position of the closest stop codon
     * @param minDistance minimum distance between start and stop codon
     * @param DNA sequence of DNA in wich the codons are found
     * @param orfs arraylist containing an arraylist with the start and stop positions of the ORFs
     * @return returns an arraylist containing an arraylist with the start and stop positions of the ORFs
     */
    static ArrayList checkORFLength(int start, int stopFurthest, int stopMiddle, int stopClosest, int minDistance, String DNA, ArrayList orfs){

        if(((stopClosest-start)%3)==0 && stopClosest-start>=minDistance){//checks if the furthest stop codon is in the right reading frame and far enough away
            orfs=fillORFList(start, stopClosest, orfs);
        } else if(((stopMiddle-start)%3)==0 && stopMiddle-start>=minDistance){//checks if the second furthest stop codon is in the right reading frame and far enough away
            orfs=fillORFList(start, stopMiddle, orfs);
        } else if(((stopFurthest-start)%3)==0 && stopFurthest-start>=minDistance){//checks if the closest stop codon is in the right reading frame and far enough away
            orfs=fillORFList(start, stopFurthest, orfs);
        } else if(((stopFurthest-start)%3)!=0 && ((stopMiddle-start)%3 )!=0 && ((stopClosest-start)%3)!=0){//if none of the stop codons are in the right reading frame starts looking for stop codons further away
            //looks for stop codons further away than the closest stop codon
            //only the closest stop codon should get overwritten and be put further away to avoid missing stop codons
            int[] stops={DNA.indexOf("TAA",stopClosest+1),DNA.indexOf("TGA",stopClosest+1),DNA.indexOf("TAG",stopClosest+1)};
            if(stops[0]==-1 && stops[1]==-1 && stops[2]==-1){
                return orfs;
            }
            Arrays.sort(stops);
            orfs=checkORFLength(start, stops[2], stops[1], stops[0], minDistance, DNA, orfs);//checks if the new found stop codons are in the right reading frame and at the minimal distance
        }

        return orfs;
    }

    /**
     * Method that adds the start and stop position of the found ORF into the given ArrayList
     * @param start integer with the position of the start codon
     * @param stop integer with the position of the stop codon
     * @param orfs arraylist containing an arraylist with the start and stop positions of the ORFs
     * @return returns an arraylist containing an arraylist with the start and stop positions of the ORFs
     */
    static ArrayList fillORFList(int start, int stop, ArrayList orfs){

        ArrayList<Integer> singleORF = new ArrayList<>();
        singleORF.add(start);
        singleORF.add(stop);
        orfs.add(singleORF);

        return orfs;
    }

    /**
     * Method that a list with the DNA sequences of the ORFs
     * @param orfs arraylist containing an arraylist with the start and stop positions of the ORFs
     * @param DNA string with the DNA sequence in which the ORFs are found
     * @return returns an ArrayList with the DNA sequences of the ORFs found in the DNA sequence
     */
    static ArrayList getORF_DNA_Sequence(ArrayList<ArrayList<Integer>> orfs, String DNA){
        ArrayList<String> DNA_ORF_List = new ArrayList<>();
        orfs.stream().forEach((temp) -> {//streams the orf list and adds the DNA sequence of the ORFs into the DNA_ORF_List
            DNA_ORF_List.add(DNA.substring(temp.get(0), temp.get(1)));
        });

        return DNA_ORF_List;
    }

    /**
     * Method that translates an arraylist containing DNA sequences to an arraylist containing protein sequences
     * @param CodonTable Hashmap containing the translation table
     * @param DNA Arraylist containing strings of DNA to be translated
     * @return Arraylsit<String> with translated protein sequences
     */
    static ArrayList<String> DNAtoAA(HashMap CodonTable, ArrayList<String> DNA) {

        ArrayList<String> translatedList = new ArrayList();//arraylist to store the protein sequences in
        DNA.stream().map((temp) -> temp.toUpperCase()).map((temp) -> {//streams all strings in the DNA list
            String proteinSeq = ""; //temporary protein string for each DNA sequence
            for(int i=0; i < (temp.length()/3); i++){
                if(CodonTable.containsKey(temp.substring(i*3, (i+1)*3))){//checks if the codon is found in the codon table used
                    proteinSeq+=CodonTable.get(temp.substring(i*3, (i+1)*3));//adds the protein to the string of proteins
                }else{//if the codon is not found in the codon table translates this codon to a gap
                    proteinSeq+="-";
                }
            }
            return proteinSeq;
        }).forEach((proteinSeq) -> {
            translatedList.add(proteinSeq);//inserts the codon into the codon list
        });

        return translatedList;
    }

    /**
     * Method that fills resultsmap with found results
     * @param proteinList Arraylist of protein strings
     * @param DNA_List Arraylist of DNA strings
     * @param startStopList Arraylist of Arraylist with the start and stop positions of the found ORFs
     * @return returns filled hashmap of resultsMap with ints as key and an arraylist with the starting position at index 0 the stop position at 1 and DNA string at 2 and protein string at 3
     */
    static HashMap fillResultsMap(ArrayList<String> proteinList, ArrayList<String> DNA_List, ArrayList<ArrayList<Integer>> startStopList){
        resultsMap =  new HashMap();
        for(int i=0; i<proteinList.size() ; i++){
            ArrayList<String> temp = new ArrayList();
            temp.add(Integer.toString(startStopList.get(i).get(0)));
            temp.add(Integer.toString(startStopList.get(i).get(1)));
            temp.add(DNA_List.get(i));
            temp.add(proteinList.get(i));
            resultsMap.put(i+1, temp);
        }
        Connector.Connection();
        return resultsMap;
    }


    /**
     * Method that exports the found ORF's to a CSV file and saves it locally on the user's computer
     * @param directory String of chosen directory
     * @// TODO: Make method generic for Windows and Linux OS
     */
    static void exportORFtoCSV(String directory) {
        try {
            String path = directory.replace("\\", "\\\\")+"\\" + "ORFresults.csv";
            outputFile = new File(path);
            bw = new BufferedWriter(new FileWriter(outputFile));
            bw.write("Start position"+ "," + "Stop position"+ "," + "DNA sequence" + ","+ "Amino acid sequence" + "\n");

            for(ArrayList value : resultsMap.values()) {

                String ORF = String.join(",", value+"\n").replace("[", "").replace("]", "");
                bw.write(ORF);
            }
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

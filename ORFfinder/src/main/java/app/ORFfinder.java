package app;

/**
 * @author: Tjeerd van der Veen & Sanne Schroduer
 */

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class ORFfinder {

    static File outputFile;
    static BufferedWriter bw;
    static String sequence;

    private static ArrayList<ArrayList<Integer>> orfs;
    static HashMap<Integer, ArrayList<String>> resultsMap;
    private static HashMap<String, String> CodonTable = new HashMap<>();

    public static void main(String[] args) {
        CodonTable=codonTable.makeCodonTable(CodonTable);
        initialiseGUI();

    }

    static void initialiseGUI() {
        orfGUI frame = new orfGUI();

        frame.setTitle("ORF finder application");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.pack();
        frame.setSize(1000,800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String controlFormat(String fileName) {
        String header = "";
        try {

            BufferedReader inFile = new BufferedReader(new FileReader(fileName));
            String lineZero = inFile.readLine();

            if(lineZero.startsWith(">")) {
                header = lineZero;
                getSeq(fileName);
            } else {
                //geef melding aan gebruiker
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return header;
    }

    /**
     *
     * @param fileName
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sequence;
    }

    /**
     * Starts analyzing given DNA code
     */
    public static HashMap analyse(boolean startIsATG){

        //int DNA_Hashcode = DNA.hashCode();
        orfs = new ArrayList<>();

        orfs = findORFs(sequence, orfs,  0, 75, startIsATG);

        ArrayList DNA_ORF_List = getORF_DNA_Sequence(orfs, sequence);
        ArrayList proteinList=DNAtoAA(CodonTable, DNA_ORF_List);
        resultsMap = fillResultsMap(proteinList, DNA_ORF_List, orfs);

        return resultsMap;
    }

    /**
     * Function to find ORFs within a DNA sequence
     * @param sequence string containing the DNA sequence you want to
     * @param orfs ArrayList containing ArrayLists of integers
     * @param start starting position to start looking for ORFs in the given DNA sequence
     * @param minDistance minimum distance between starting position and stop position
     * @param startIsATG boolean that decides wether ATG is used to find ORFs or not
     * @return returns an ArrayList containing an ArrayList of the starting and stopping positions of the found ORFs
     */
    public static ArrayList findORFs(String sequence, ArrayList orfs, int start, int minDistance, boolean startIsATG){

        int startIndex=start;

        if(startIsATG){//checks if ATG is used as the starting point
            startIndex=sequence.indexOf("ATG",start);
        }
        int[] stops={sequence.indexOf("TAA",startIndex),sequence.indexOf("TGA",startIndex),sequence.indexOf("TAG",startIndex)};
        Arrays.sort(stops);//sort the indexes of stop codons so the lowest and highest values are identified

        if(!startIsATG){//checks if ATG is not used as the starting point
            startIndex=stops[0];
        }
        if(stops[0]!=-1 && stops[1]!=-1 && stops[2]!=-1){//checks if there are any stop codons found
            checkORFLength(startIndex, stops[2], stops[1], stops[0], minDistance, sequence, orfs);//checks if the ORF is long enough and in the stop codons are in the right reading frame
            //start recurssion
            start=startIndex+1;
            orfs=findORFs(sequence, orfs, start, minDistance, startIsATG);
        }

        return orfs;
    }

    /**
     * Function to check if the found stop positions match the reading frame of the start codon and the stop codon is far enough away
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
     * Adds the start and stop position of the found ORF into the given ArrayList
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
     * Creates a list with the DNA sequences of the ORFs
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
     * Translates an arraylist containing DNA sequences to an arraylist containing protein sequences
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
     * fills resultsmap with found results
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

        return resultsMap;
    }

    static void exportORFtoCSV() {
        try {
            outputFile = new File("C:\\Users\\sschr\\OneDrive\\Documenten\\ORFfinder_foundORFs.csv");
            bw = new BufferedWriter(new FileWriter(outputFile));
            bw.write("Start position"+ ", " + "Stop position"+ ","+ "DNA sequence" + ","+ "Amino acid sequence" + "\n");


            for(ArrayList value : resultsMap.values()) {
                String ORF = String.join(",", value+"\n");
                bw.write(ORF);
            }
            //for (resultsMap.entry<Integer, Object> entry : resultsMap.entrySet()) {
                //String key = entry.getKey();
                //Object value = entry.getValue();
                // ...


                //String line = gene.getTaxID() + ", " + gene.getGeneID() + ", " + gene.getSymbol() + ", " + gene.getLocusTag()+ ", " + gene.getSynonyms()+ ", " + gene.getChromosome()+ ", " + gene.getMapLocation() + "\n";
                //bw.write(line);
            //}
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void exportProteinstoCSV() {

    }
}

package app;

import java.util.HashMap;

public class codonTable {


    /**
     * initiates a CodonTable as hashmap
     * @param CodonTable HashMap you want to put the codon table in (<String, String>)
     * @return returns HashMap with codons as key and ammino acid as value
     */
    public static HashMap makeCodonTable(HashMap CodonTable){

        CodonTable.put("GCA","A");
        CodonTable.put("GCC","A");
        CodonTable.put("GCG","A");
        CodonTable.put("GCT","A");
        CodonTable.put("TGC","C");
        CodonTable.put("TGT","C");
        CodonTable.put("GAC","D");
        CodonTable.put("GAT","D");
        CodonTable.put("GAA","E");
        CodonTable.put("GAG","E");
        CodonTable.put("TTC","F");
        CodonTable.put("TTT","F");
        CodonTable.put("GGA","G");
        CodonTable.put("GGC","G");
        CodonTable.put("GGG","G");
        CodonTable.put("GGT","G");
        CodonTable.put("CAC","H");
        CodonTable.put("CAT","H");
        CodonTable.put("ATA","I");
        CodonTable.put("ATC","I");
        CodonTable.put("ATT","I");
        CodonTable.put("AAA","K");
        CodonTable.put("AAG","K");
        CodonTable.put("CTA","L");
        CodonTable.put("CTC","L");
        CodonTable.put("CTG","L");
        CodonTable.put("CTT","L");
        CodonTable.put("TTA","L");
        CodonTable.put("TTG","L");
        CodonTable.put("ATG","M");
        CodonTable.put("AAC","N");
        CodonTable.put("AAT","N");
        CodonTable.put("CCA","P");
        CodonTable.put("CCC","P");
        CodonTable.put("CCG","P");
        CodonTable.put("CCT","P");
        CodonTable.put("CAA","Q");
        CodonTable.put("CAG","Q");
        CodonTable.put("AGA","R");
        CodonTable.put("AGG","R");
        CodonTable.put("CGA","R");
        CodonTable.put("CGC","R");
        CodonTable.put("CGG","R");
        CodonTable.put("CGT","R");
        CodonTable.put("AGC","S");
        CodonTable.put("AGT","S");
        CodonTable.put("TCA","S");
        CodonTable.put("TCC","S");
        CodonTable.put("TCG","S");
        CodonTable.put("TCT","S");
        CodonTable.put("ACA","T");
        CodonTable.put("ACC","T");
        CodonTable.put("ACG","T");
        CodonTable.put("ACT","T");
        CodonTable.put("GTA","V");
        CodonTable.put("GTC","V");
        CodonTable.put("GTG","V");
        CodonTable.put("GTT","V");
        CodonTable.put("TGG","W");
        CodonTable.put("TAC","Y");
        CodonTable.put("TAT","Y");
        CodonTable.put("TGA","");
        CodonTable.put("TAA","");
        CodonTable.put("TAG","");
        return CodonTable;
    }
}


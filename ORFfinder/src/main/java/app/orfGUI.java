package app;

/**
 * @author: Tjeerd van der Veen & Sanne Schroduer
 * @date: 29-03-2019
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * This class sets up the GUI and the functionality for the buttons
 */
public class orfGUI extends JFrame {

    JFileChooser fileChooser;
    static String fileName;
    static String directory;

    private JPanel mainPanel;
    private JLabel fileLabel;
    private JTextField filePath;
    private JTextArea sequenceArea;
    private JLabel nrFoundORFs;
    private JButton browseButton;
    private JButton controlButton;
    private JButton analyseButton;
    private JButton exportORFButton;
    private JLabel startLabel;
    private JLabel sequenceLabel;
    private JLabel headerLabel;
    private JTextArea headerArea;
    private JScrollPane scrollSequence;
    private JComboBox chooseStartCodon;
    private JLabel nrORFs;
    private JScrollPane scrollHeader;
    private JTable resultTable;
    private JPanel ORFpanel;
    private JPanel choosePanel;
    private JButton chooseDirectory;
    private JTextArea saveResults;

    /**
     * Function to convert the actionevents to methods in ORFfinder.java
     */
    public orfGUI () {
        this.setContentPane(mainPanel);

        saveResults.setText("If you want to save the ORF results:" +
                "\n" + "1. Choose a directory"+
                "\n" + "2. Click Export ORFs");

        //button for uploading a file by the user
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                int returnVal = fileChooser.showOpenDialog(orfGUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filePath.setText(fileChooser.getSelectedFile().toString());
                    fileName = filePath.getText();
                }

            }
        });

        this.setContentPane(mainPanel);

        headerArea.setLineWrap(true);
        headerArea.setWrapStyleWord(true);
        sequenceArea.setRows(10);
        sequenceArea.setLineWrap(true);
        sequenceArea.setWrapStyleWord(true);
        this.setContentPane(mainPanel);

        //button for controlling the extension of the file (must be fasta)
        //calls the functions for controling the format and extracting the header and DNA sequence from the input file in ORFfinder
        controlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    headerArea.setText(ORFfinder.controlFormat(fileName));
                    sequenceArea.setText(ORFfinder.getSeq(fileName));
                } catch (Exception error) {
                    System.out.println(error.getMessage());
                    JOptionPane.showMessageDialog(mainPanel, "Error: This file isn't fasta format. Make sure you upload a file in fasta format.");
                }
            }
        });
        this.setContentPane(mainPanel);

        chooseStartCodon.addItem("ATG");
        chooseStartCodon.addItem("STOP");

        //button for choosing with start codon should be used for finding the ORF's
        chooseStartCodon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox chooseStartCodon = (JComboBox) e.getSource();
                String selectedStart = (String) chooseStartCodon.getSelectedItem();
                chooseStartCodon.setSelectedItem(selectedStart);
            }
        });
        this.setContentPane(mainPanel);

        //button for analyzing the input sequence
        //calls the analyze method in ORFfinder
        //displays the number of found ORF's and a table with all the ORF information
        analyseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String selectedStart = (String) chooseStartCodon.getSelectedItem();
                    HashMap<Integer, ArrayList<String>> resultsMap = ORFfinder.analyse(selectedStart == "ATG");

                    nrFoundORFs.setText("Number of found ORF's: " + resultsMap.size());

                    String[] columnNames = {"Start position", "Stop position", "DNA sequence", "Aminoacid sequence"};
                    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

                    for (Object orfObj : resultsMap.values()) {
                        String ORF = String.valueOf(orfObj);
                        String regex = "[\\[,\\]]";
                        String[] columns = ORF.split(regex);

                        tableModel.addRow(new Object[]{columns[1], columns[2], columns[3], columns[4]});

                    }

                    resultTable.setModel(tableModel);
                } catch (NullPointerException exception) {

                }
            }
        });
        this.setContentPane(mainPanel);

        //button for choosing the directory in which the CSV file should be saved
        chooseDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(orfGUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filePath.setText(fileChooser.getSelectedFile().toString());
                    directory = fileChooser.getSelectedFile().toString();
                }
            }
        });

        //button for exporting the ORF's to a CSV file
        //calls the export ORF to CSV in ORFfinder
        exportORFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ORFfinder.exportORFtoCSV(directory);
                    JOptionPane.showMessageDialog(mainPanel, "A CSV file \"ORF results\" has been saved to " + directory);

                } catch (NullPointerException exception) {
                    JOptionPane.showMessageDialog(mainPanel, "Error: make sure you choose a directory before you export the ORFs");
                }
            }
        });

    }

}

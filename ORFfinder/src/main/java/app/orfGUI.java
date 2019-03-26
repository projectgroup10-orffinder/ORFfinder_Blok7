package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 *
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
    private JButton blastButton;
    private JLabel nrFoundProteins;
    private JButton exportProteinsButton;
    private JLabel startLabel;
    private JRadioButton atgButton;
    private JRadioButton stopButton;
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
    private JPanel proteinPanel;
    private JButton chooseDirectory;
    private JTextArea saveResults;

    /**
     *
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
                //fileName = "C:\\Users\\sschr\\OneDrive\\Documenten\\Bl7_informaticaproject\\ORFfinder_blok7_new_one\\ORFfinder\\testHashcode.fa";
                //fileName = "C:\\Users\\sschr\\OneDrive\\Documenten\\Bl7_informaticaproject\\ORFfinder\\testDNATjeerd.fa";
                fileName = "C:\\Users\\sschr\\OneDrive\\Documenten\\Bl7_informaticaproject\\ORFfinder_blok7_new_one\\ORFfinder\\geenFasta.txt";

//                fileChooser = new JFileChooser();
//                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//
//                int returnVal = fileChooser.showOpenDialog(orfGUI.this);
//
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    filePath.setText(fileChooser.getSelectedFile().toString());
//                    fileName = filePath.getText();
//                }

            }
        });

        this.setContentPane(mainPanel);

        headerArea.setLineWrap(true);
        headerArea.setWrapStyleWord(true);
        sequenceArea.setRows(10);
        sequenceArea.setLineWrap(true);
        sequenceArea.setWrapStyleWord(true);

        this.setContentPane(mainPanel);
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

        chooseStartCodon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JComboBox chooseStartCodon = (JComboBox) e.getSource();
                String selectedStart = (String) chooseStartCodon.getSelectedItem();
                chooseStartCodon.setSelectedItem(selectedStart);
            }
        });
        this.setContentPane(mainPanel);

        analyseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String selectedStart = (String) chooseStartCodon.getSelectedItem();
                    System.out.println(selectedStart);
                    HashMap<Integer, ArrayList<String>> resultsMap = ORFfinder.analyse(selectedStart == "ATG");
                    //System.out.println(resultsMap);
                    nrFoundORFs.setText("Number of found ORF's: " + Integer.toString(resultsMap.size()));

                    String[] columnNames = {"Start position", "Stop position", "DNA sequence", "Aminoacid sequence"};
                    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
                    //resultTable.setSize(1000,50);

                    //scrollTable.getViewport ().add (resultTable);

                    for (Object orfObj : resultsMap.values()) {
                        String ORF = String.valueOf(orfObj);
                        String regex = "[\\[,\\]]";
                        String[] columns = ORF.split(regex);

                        tableModel.addRow(new Object[]{columns[1], columns[2], columns[3], columns[4]});

                    }

                    //scrollTable.setViewport(resultTable);
                    resultTable.setModel(tableModel);
                } catch (NullPointerException exception) {

                }
            }
        });
        this.setContentPane(mainPanel);

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


        blastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

}

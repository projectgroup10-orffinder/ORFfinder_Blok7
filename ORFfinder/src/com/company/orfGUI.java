package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

public class orfGUI extends JFrame {

    JFileChooser fileChooser;
    static String fileName;

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
    private JScrollPane scrollHeader;
    private JComboBox chooseStartCodon;

    public orfGUI () {
        this.setContentPane(mainPanel);

        //button for uploading a file by the user
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileName = "C:\\Users\\sschr\\OneDrive\\Documenten\\ORFfinder\\testDNATjeerd.fa";
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
        controlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                headerArea.setBounds(100, 100, 100,100);
                headerArea.setText(ORFfinder.controlFormat(fileName));

                sequenceArea.setText(ORFfinder.getSeq(fileName));
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
                String selectedStart = (String) chooseStartCodon.getSelectedItem();
                System.out.println(selectedStart);
                HashMap resultsMap=ORFfinder.analyse(selectedStart=="ATG");
                nrFoundORFs.setText(Integer.toString(resultsMap.size()));



            }
        });

        exportORFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        blastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        exportProteinsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}

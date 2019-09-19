
import java.awt.Color;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author s303906
 */
public class OpenFasta extends MyFrame {

    static String fastaFilename;

    // Method for parsing FASTA file into a linked hash map 
    public void getFile(JTextArea textarea, JLabel filenameLabel) {
        FileDialog nameBox = new FileDialog(this, "Open FASTA File",
                FileDialog.LOAD);
        nameBox.setVisible(true);
        String fastaFileDirectory = nameBox.getDirectory();
        fastaFilename = nameBox.getFile();
        filenameLabel.setText(fastaFilename);
        fastaFilename = fastaFileDirectory.concat(fastaFilename);

        if (fastaFilename.contains(".fa")) {
            try {
                BufferedReader reader = new BufferedReader(
                        new FileReader(fastaFilename));
                textarea.setText(null);
                String readNextLine;
                String contig = "";
                StringBuilder build = new StringBuilder();
                while ((readNextLine = reader.readLine()) != null) {
                    if (readNextLine.contains(">")) {
                        build.setLength(0);
                        contig = readNextLine.substring(1, readNextLine.length());
                    } else {
                        build.append(readNextLine);
                    }
                    contigHashMap.put(contig, build.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    // Method for calculating the statistics for the whole FASTA file: N50, longest contig, shortest contig
    public void getGlobalStatistics(JLabel n50Label, JLabel largestContig, JLabel shortestContig) {
        sequences = contigHashMap.values().toString().replace("[", "").
                replace(",", "").replace("]", "").replaceAll("\\s+", "");
        int seqLength = sequences.length();
        int fiftyP = seqLength / 2;
        String[] seqStrings = contigHashMap.values().toArray(new String[0]);
        // Sorting contigs by length in descending order
        Arrays.sort(seqStrings, Comparator.comparingInt(String::length).reversed());
        int findingN50 = 0;
        // Finding the contig at N50
        for (int i = 0; i < seqStrings.length; i++) {
            findingN50 = findingN50 + seqStrings[i].length();
            if (findingN50 >= fiftyP) {
                n50Label.setText(Integer.toString(seqStrings[i].length())
                        + "  (" + i + " sequences)");
                break;
            }
        }
        // Finding the longest and shortest contigs
        maxLength = 0;
        minLength = 2147483647;
        for (String key : contigHashMap.keySet()) {
            maxLength = Math.max(maxLength, contigHashMap.get(key).length());
            minLength = Math.min(minLength, contigHashMap.get(key).length());
        }
        largestContig.setText(Integer.toString(maxLength));
        shortestContig.setText(Integer.toString(minLength));
    }

    // Method for calculating the GC content
    public void getContigStatistics(JLabel GClabel) {
        GCcontent = 0.0;
        GCpercent = 0.0;
        for (int i = 0; i < listItem.length(); i++) {
            if (listItem.charAt(i) == 'G' || listItem.charAt(i) == 'C') {
                GCcontent++;
            }
        }
        GCpercent = 100 * (GCcontent / listItem.length());
        f = new DecimalFormat("##.00");
        GClabel.setText(f.format(GCpercent) + "%");
    }
    
    // Method for resetting the gap viewer
    public void resetLayeredPane(JLayeredPane pane) {
        pane.removeAll();
        pane.revalidate();
        pane.repaint();
    }

    // Method for calculating gap positions in the scaffold and populating the layered pane
    public void viewGaps(JLayeredPane pane, String listitem, JLabel gapsLabel) {
        int paneWidth = pane.getWidth();
        int seqLength = listitem.length();
        String[] startArray = listitem.split("N");
        int pos = 1;
        int start = 0;
        Map<Integer, Integer> startStop = new LinkedHashMap<>();
        // Loop to find start and stop positions of the N repeats
        if (startArray.length > 1) {
            for (int i = 1; i < listitem.length(); i++) {
                if (listitem.charAt(i - 1) != 'N' && listitem.charAt(i) == 'N') {
                    startStop.put(i, i + 1);
                    start = i;
                } else if (listitem.charAt(i) == 'N') {
                    pos++;
                    startStop.put(start, start + pos);
                }
            }
        }
        
        // Moving the start and stop values into arrays in the right orders
        int[] starts = new int[startStop.size()];
        int[] stops = new int[startStop.size()];
        int index = 0;
        for (Map.Entry<Integer, Integer> entry : startStop.entrySet()) {
            starts[index] = entry.getKey();
            stops[index] = entry.getValue();
            index++;
        }
        
        // Calculating start and stop positions relative to pane width
        double[] startX = new double[startStop.size()];
        double[] stopX = new double[startStop.size()];
        // loop for calculating start and stop positions relative to layeredpane width
        for (int i = 0; i < startX.length; i++) {
            startX[i] = (paneWidth * (starts[i])/seqLength);
            stopX[i] = (paneWidth * (stops[i])/seqLength);
        }
        
        // Labelling number of contigs per scaffold
        gapsLabel.setText(Integer.toString(startX.length) + " gap(s), " + (startArray.length) + " contig(s)");

        // Setting up the gap viewer using layered pane panels
        for (int i = 0; i < startX.length; i++) {
            JPanel p = new JPanel();
            p.setBounds((int) startX[i], 0, (int)(stopX[i] - 
                    startX[i]+1), pane.getHeight());
            p.setBackground(Color.cyan);
            p.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black, 1));
            pane.add(p);
        }
    }
}

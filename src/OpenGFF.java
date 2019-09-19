
import au.com.bytecode.opencsv.CSVReader;
import java.awt.FileDialog;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dylanmead
 */
public class OpenGFF extends MyFrame {

    public void readGFF(JTable table) {
        try {
            // Opening file chooser and storing file path and file name
            FileDialog dialog = new FileDialog(this, "Open GFF File", FileDialog.LOAD);
            dialog.setVisible(true);
            String filepath = dialog.getDirectory();
            String filename = dialog.getFile();
            filename = filepath.concat(filename);

            // Creating instance of CSV reader using tab as separator for GFF
            CSVReader GFFReader = new CSVReader(new FileReader(filename), '\t');
            
            // Getting the table model so that old rows can be cleared and new rows can be added
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            tableModel.setRowCount(0);
            table.setModel(tableModel);
            /* List of string arrays to store each row and element within
                   the row from the GFFReader */
            List<String[]> rows = GFFReader.readAll();
            int numRows = 0;
            /* For loop counts the number of rows that are not the metadata
                   or the header row (contain '#') */
            for (String[] row : rows) {
                if (!Arrays.toString(row).trim().contains("#")) {
                    numRows++;
                }
            }
            // Loop adds the data to the appropriate table columns
            for (int i = rows.size() - numRows; i < rows.size(); i++) {
                tableModel.addRow(new Object[]{rows.get(i)[0], rows.get(i)[2], rows.get(i)[3],
                    rows.get(i)[4], rows.get(i)[5], rows.get(i)[6], rows.get(i)[7]});
            }

        } catch (Exception e) {
        }

    }
}

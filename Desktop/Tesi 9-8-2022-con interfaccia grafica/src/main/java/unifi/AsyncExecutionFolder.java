package unifi;

import javax.swing.*;

import com.github.owlcs.ontapi.OntManagers;
import org.apache.commons.io.input.BOMInputStream;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.swing.*;
import java.io.*;

public class AsyncExecutionFolder extends Thread {

    private final JTextArea textArea;
    private final JTextField tFolder, tOntology;
    private final JButton b;

    public AsyncExecutionFolder(JTextField tFolder, JTextField tOntology, JButton b, JTextArea textArea) {
        this.textArea = textArea;
        this.tFolder = tFolder;
        System.out.println(this.tFolder.getText());
        this.tOntology = tOntology;
        this.b = b;
    }


    @Override
    public void run() {
        textArea.append("--------------------Starting------------------\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
        InputStream inputStream;


        File folder = new File(tFolder.getText());
        String[] listOfFiles = folder.list();


        for (int i = 0; i < listOfFiles.length; i++) {

            if(listOfFiles[i].equals(".DS_Store")){
                continue;
            }
            try {
                inputStream = new FileInputStream(tFolder.getText()+'/'+listOfFiles[i]);
                System.err.println(tFolder.getText()+'/'+listOfFiles[i]);

            } catch (FileNotFoundException fileNotFoundException) {
                textArea.append("Invalid CSV File!\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
                b.setEnabled(true);
                return;
            }

            textArea.append("Parsing CSV....\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
            InputStreamReader reader = new InputStreamReader(new BufferedInputStream(bOMInputStream));
            BufferedReader br = new BufferedReader(reader);

            OWLOntologyManager man = OntManagers.createONT();
            File file = new File(tOntology.getText());
            OWLOntology o;

            textArea.append("Loading Ontology...\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            try {
                o = man.loadOntologyFromOntologyDocument(file);
            } catch (OWLOntologyCreationException e) {
                textArea.append("Invalid OWL File!\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
                b.setEnabled(true);
                return;
            }

            textArea.append("Adding data to Ontology....\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            OntologyBuilder builder = new OntologyBuilder(o, man, br, file);

            try {
                builder.build();
            } catch (Exception e) {
                textArea.append("Failed to add data to Ontology");
                textArea.setCaretPosition(textArea.getDocument().getLength());
                return;
            }

            textArea.append("Generating SVG....\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            SVGGenerator svgGenerator;

            try {
                svgGenerator = new SVGGenerator(o);
                svgGenerator.generate(tFolder.getText()+'/'+listOfFiles[i]);
            } catch (Exception e) {
                e.printStackTrace();
                //logger.debug(e.getMessage());
                textArea.append("Failed to generate SVG");
                textArea.setCaretPosition(textArea.getDocument().getLength());
                return;
            }

            textArea.append("-----------------SVG Generated----------------\n\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            b.setEnabled(true);

        }

    }
}

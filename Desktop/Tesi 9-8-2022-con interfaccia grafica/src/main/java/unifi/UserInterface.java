package unifi;

import javax.swing.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class UserInterface {
    private JTextField t1, tOntology,tFolder;
    private JTextArea textArea;

    public void show() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        JFrame f = new JFrame();

        JLabel label = new JLabel("Select CSV File");
        label.setBounds(10, 0, 200, 30);
        f.add(label);

        t1 = new JTextField("");
        t1.setBounds(10, 30, 280, 30);
        f.add(t1);

        JButton b1 = new JButton("Open File");
        b1.setBounds(290, 30, 100, 30);
        f.add(b1);

        b1.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("/Users/nicc/Desktop/Tesi 9-8-2022-con interfaccia grafica");
            fileChooser.setFileFilter(new FileTypeFilter("csv"));
            int n = fileChooser.showOpenDialog(null);
            if (n == JFileChooser.APPROVE_OPTION) {
                t1.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }

        });

        label = new JLabel("Select OWL File");
        label.setBounds(10, 70, 200, 30);
        f.add(label);

        tOntology = new JTextField("");
        tOntology.setBounds(10, 100, 280, 30);
        f.add(tOntology);

        JButton b2 = new JButton("Open File");
        b2.setBounds(290, 100, 100, 30);
        f.add(b2);

        b2.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("/Users/nicc/Desktop/Tesi 9-8-2022-con interfaccia grafica");
            fileChooser.setFileFilter(new FileTypeFilter("owl"));
            int n = fileChooser.showOpenDialog(null);
            if (n == JFileChooser.APPROVE_OPTION) {
                tOntology.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JRadioButton jRadioButton_1 = new JRadioButton("Use Ontology only as a Model");
        JRadioButton jRadioButton_2 = new JRadioButton("Save data to Ontology");
        jRadioButton_1.setBounds(10, 150, 300, 20);
        jRadioButton_2.setBounds(10, 170, 300, 20);
        f.add(jRadioButton_1);
        f.add(jRadioButton_2);
        jRadioButton_1.setSelected(true);

        jRadioButton_1.addActionListener(e -> {
            jRadioButton_2.setSelected(false);
            OntologyBuilder.setSaveToOntology(FALSE);
        });

        jRadioButton_2.addActionListener(e -> {
            jRadioButton_1.setSelected(false);
            OntologyBuilder.setSaveToOntology(TRUE);
        });


        JButton b3 = new JButton("Generate SVG");
        b3.setBounds(125, 190, 150, 30);
        f.add(b3);

        b3.addActionListener(e -> {
            b3.setEnabled(false);
            Thread thread = new AsyncExecution(t1, tOntology, b3, textArea);
            thread.start();
        });

/////////// ADDED
/////////// ADDED

        JLabel labelFolder = new JLabel("Select CSV Folder");
        labelFolder.setBounds(10, 210, 200, 30);
        f.add(labelFolder);

        tFolder = new JTextField("");
        tFolder.setBounds(10, 240, 280, 30);
        f.add(tFolder);

        JButton buttonFolder = new JButton("Open folder");
        buttonFolder.setBounds(290, 240, 100, 30 );
        //290, 300, 100, 30     60, 280, 300, 30
        f.add(buttonFolder);


        buttonFolder.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int n = fileChooser.showOpenDialog(null);
            if (n == JFileChooser.APPROVE_OPTION) {
                tFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });


        JButton buttonGenerateSVGFromFolder = new JButton("Generate SVG from folder");
        buttonGenerateSVGFromFolder.setBounds(60, 280, 300, 30);
        f.add(buttonGenerateSVGFromFolder);

        buttonGenerateSVGFromFolder.addActionListener(e -> {
            buttonGenerateSVGFromFolder.setEnabled(false);
            Thread thread = new AsyncExecutionFolder(tFolder, tOntology, buttonGenerateSVGFromFolder, textArea);
            thread.start();
        });

/////////// ADDED
/////////// ADDED

        textArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(10, 320, 380, 250);
        f.add(scroll);

        char c = '\u00A9';
        label = new JLabel("Copyright " + c + " 2020 - Mattia Bacci");
        label.setBounds(10, 500, 350, 30);
        f.add(label);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.updateComponentTreeUI(f);

        f.setSize(400, 550);
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

    }

}

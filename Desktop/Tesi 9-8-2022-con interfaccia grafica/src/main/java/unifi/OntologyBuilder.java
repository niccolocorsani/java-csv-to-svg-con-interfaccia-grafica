package unifi;

import org.apache.jena.ontology.ObjectProperty;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class OntologyBuilder {

    //// Su find prefix ha detto, fai ritornare una costante e bona
    //  "http://www.disit.org/altair/resource/"
    // su il tipo di sostanza che gira, ha detto: metti cosa del tipo "cloro_to" (come objectProperty) anziche "to"


    private final String[] prefixes = {"http://www.disit.org/saref4bldg-ext/", "https://saref.etsi.org/saref4bldg/", "https://saref.etsi.org/core/", "http://www.disit.org/altair/resource/"};
    private String xxxx = "http://www.disit.org/saref4bldg-ext/" + "https://saref.etsi.org/saref4bldg/" + "https://saref.etsi.org/core/" + "http://www.disit.org/altair/resource/";
    private final OWLOntology o;
    private final OWLOntologyManager man;
    private final OWLDataFactory factory;
    private final BufferedReader br;
    private final File file;
    private static boolean willSave = FALSE;

    public OntologyBuilder(OWLOntology o, OWLOntologyManager man, BufferedReader br, File file) {
        this.o = o;
        this.man = man;
        this.br = br;
        this.file = file;
        factory = man.getOWLDataFactory();
    }

    public void build() throws IOException, OWLOntologyStorageException {

        OWLNamedIndividual individual, individual_2;
        OWLClass owlClass;
        OWLObjectProperty owlObjectProperty;
        OWLDataProperty owlDataProperty;
        OWLClassAssertionAxiom classAssertionAxiom;
        OWLObjectPropertyAssertionAxiom owlObjectPropertyAssertionAxiom;
        OWLDataPropertyAssertionAxiom owlDataPropertyAssertionAxiom;
        PrefixManager pm;
        Map<String, String> individuals_iri = new HashMap<>(); // per come è impostato il software e con la riga in questione in ogni csv deve essere definito tutti gli oggetti anche se sono stati precedentemente definiti
        String row;
        int dataProperty;
        List addedLines = new ArrayList<String>();
        while ((row = br.readLine()) != null) {
            try {
                addedLines.add(row);


                String[] data = row.split(";");

                if (row.contains("shap"))
                    System.out.println("x");

                switch (data[1]) {

                    case "Class":


                        pm = new DefaultPrefixManager(null, null, "http://www.disit.org/altair/resource/");
                        individuals_iri.put(data[0], pm.getDefaultPrefix());
                        individual = factory.getOWLNamedIndividual(":" + data[0], pm);
                        pm = findClassPrefix(o, data[2]);

                        owlClass = factory.getOWLClass(":" + data[2], pm);



                        classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, individual);
                        man.addAxiom(o, classAssertionAxiom);
                        break;


                    case "ObjectProperty":

/////////////    experiments
                       // o.axioms().forEach(System.out::println);
                        IRI IOR = IRI.create("http://www.disit.org/saref4bldg-ext/");
                        OWLObjectProperty ob = factory.getOWLObjectProperty(IOR + "to_ferroso");
                        OWLDeclarationAxiom da = factory.getOWLDeclarationAxiom(ob);


                        o.add(da);


                        ob = factory.getOWLObjectProperty(IOR + "to_cloro");
                        da = factory.getOWLDeclarationAxiom(ob);
                        o.add(da);


                        ob = factory.getOWLObjectProperty(IOR + "to_cloruro_cerrico");
                        da = factory.getOWLDeclarationAxiom(ob);
                        o.add(da);

                      //  o.axioms().forEach(System.err::println);

/////////////    experiments
                        pm = new DefaultPrefixManager(null, null, individuals_iri.get(data[0]));
                        individual = factory.getOWLNamedIndividual(":" + data[0], pm);
                        pm = findObjectPropertyPrefix(o, data[2]);
                        owlObjectProperty = factory.getOWLObjectProperty(":" + data[2], pm);

                        if (individuals_iri.get(data[3]) != null) // se è stato definito all'interno del csv
                            pm = new DefaultPrefixManager(null, null, individuals_iri.get(data[3]));
                        else {
                            java.util.Set<OWLEntity> entOnt = o.getSignature();
                            for (OWLEntity a : entOnt) {
                                if (a.toString().replace("<", "").replace(">", "").contains(data[3])) {
                                    String IRI = a.toString().replace("<", "").replace(">", "").replace(data[3], "").replace("start", "").replace(" ", "");

                                    if (!this.xxxx.contains(IRI))
                                        System.out.println(IRI);
                                    pm = new DefaultPrefixManager(null, null, IRI);
                                }
                            }
                        }


                        individual_2 = factory.getOWLNamedIndividual(":" + data[3], pm);
                        owlObjectPropertyAssertionAxiom = factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, individual, individual_2);
                        man.addAxiom(o, owlObjectPropertyAssertionAxiom);
                        break;
                    case "DataProperty":
                        pm = new DefaultPrefixManager(null, null, individuals_iri.get(data[0]));
                        individual = factory.getOWLNamedIndividual(":" + data[0], pm);
                        pm = findDataPropertyPrefix(o, data[2]);
                        owlDataProperty = factory.getOWLDataProperty(":" + data[2], pm);

                        if (data[3].equals("TRUE")) {
                            owlDataPropertyAssertionAxiom = factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, individual, TRUE);
                        } else if (data[3].equals("FALSE")) {
                            owlDataPropertyAssertionAxiom = factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, individual, FALSE);
                        } else {
                            try {
                                dataProperty = Integer.parseInt(data[3]);
                                owlDataPropertyAssertionAxiom = factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, individual, dataProperty);

                            } catch (NumberFormatException e) {
                                owlDataPropertyAssertionAxiom = factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, individual, data[3]);
                                System.err.println(row);

                            }
                        }

                        man.addAxiom(o, owlDataPropertyAssertionAxiom);
                        break;
                }
            } catch (Exception e) {
                System.err.println(row);
                e.printStackTrace();
            }
        }

        br.close();

        if (willSave) {
            man.saveOntology(o, new TurtleDocumentFormat(), new FileOutputStream(file));
        }
    }

    private PrefixManager findClassPrefix(OWLOntology o, String toCkeck) throws IOException {
        PrefixManager pm = null;

        for (int i = 0; i < 3; i++) {

            if (o.containsClassInSignature(IRI.create(prefixes[i] + toCkeck), Imports.INCLUDED)) {
                pm = new DefaultPrefixManager(null, null, prefixes[i]);// prima
                break;
            }
        }
        if (pm == null) {
            throw new IOException();
        }
        return pm;
    }

    private PrefixManager findObjectPropertyPrefix(OWLOntology o, String toCkeck) throws IOException {
        PrefixManager pm = null;
        for (int i = 0; i < 3; i++) {
            if (o.containsObjectPropertyInSignature(IRI.create(prefixes[i] + toCkeck), Imports.INCLUDED)) {
                pm = new DefaultPrefixManager(null, null, prefixes[i]);
                break;
            }
        }
        if (pm == null) {
            throw new IOException();
        }
        return pm;
    }

    private PrefixManager findDataPropertyPrefix(OWLOntology o, String toCkeck) throws IOException {
        PrefixManager pm = null;
        for (int i = 0; i < 3; i++) {
            if (o.containsDataPropertyInSignature(IRI.create(prefixes[i] + toCkeck), Imports.INCLUDED)) {
                pm = new DefaultPrefixManager(null, null, prefixes[i]);
                break;
            }
        }
        if (pm == null) {

            throw new IOException();
        }
        return pm;
    }

    public static void setSaveToOntology(boolean b) {
        willSave = b;
    }
}

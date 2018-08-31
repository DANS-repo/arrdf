package nl.knaw.dans.repo.arrdf;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.File;
import java.net.URL;

/**
 * Created on 2018-08-17 14:59.
 */
public class Driller {

    public static void main(String[] args) throws Exception {
        //String url = "https://repository.huygens.knaw.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/dwc/dataset.nq";
        //String url = "https://repository.huygens.knaw.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/charterportaal/dataset.nq";
        //String url = "https://repository.huygens.knaw.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/bioport/dataset.nq";
        //String url = "https://repository.huygens.knaw.nl/v5/resourcesync/u0c0efe7fb8a246c7ebb7b0d710d7b19d5e60ce3d/vocopvarenden2/dataset.nq";
        //String url = "https://repository.huygens.knaw.nl/v5/resourcesync/u0c0efe7fb8a246c7ebb7b0d710d7b19d5e60ce3d/prizepapers/dataset.nq";

        //String url = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/gemeentegeschiedenisnl/dataset.nq";
        //String url = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/gemeentegeschiedenis/dataset.nq";
        //String url = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/nlgis/dataset.nq";
        //String url = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/bioport/dataset.nq";
        //String url = "https://data.anansi.clariah.nl/v5/resourcesync/u74ccc032adf8422d7ea92df96cd4783f0543db3b/tbi/dataset.nq";

        String url = "https://data.anansi.clariah.nl/v5/resourcesync/u33707283d426f900d4d55b410a78996dc730b2f7/personen/dataset.nq";
        //String url = "https://data.anansi.clariah.nl/v5/resourcesync/u33707283d426f900d4d55b410a78996dc730b2f7/concepten/dataset.nq";
        //String url = "https://data.anansi.clariah.nl/v5/resourcesync/u33707283d426f900d4d55b410a78996dc730b2f7/plaatsen/dataset.nq";

        drillDown(new URL(url));

        // File file = new File("src/test/resources/driller/anansi/dwc.nq");
        // drillDown(file);
    }

    private static void drillDown(URL url) throws Exception {
        System.out.println("Inspecting: " + url);
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();
        ValueFactory f = repo.getValueFactory();

        try (RepositoryConnection con = repo.getConnection()) {
            con.add(url, null, RDFFormat.NQUADS);
            System.out.println("size of repo: " + con.size());
        }
        drillDown(repo);
    }

    private static void drillDown(File file) throws Exception {
        System.out.println("Inspecting: " + file);
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();
        ValueFactory f = repo.getValueFactory();

        try (RepositoryConnection con = repo.getConnection()) {
            con.add(file, null, RDFFormat.NQUADS);
            System.out.println("size of repo: " + con.size());
        }
        drillDown(repo);
    }

    private static void drillDown(Repository repo) throws Exception {
        System.out.println("count distinct subjects  : " + countDistinctSubjects(repo));
        System.out.println("count distinct predicates: " + countDistinctPredicates(repo));
        System.out.println("count distinct objects   : " + countDistinctObjects(repo));
        //listDistinctPredicates(repo);
        listPredicates(repo);
        listStatements(repo, "http://data.anansi.clariah.nl/bioport/predicate/funeralPlace", null, null);
        countPredNamespace(repo, "http://example.org");
        countObjNamespace(repo, "http://example.org");
    }

    private static long countDistinctSubjects(Repository repo) throws Exception {
        String q = "SELECT (COUNT(DISTINCT ?s) AS ?subjs) " +
          "WHERE { " +
          "?s ?p ?o " +
          "}";
        try (RepositoryConnection con = repo.getConnection()) {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, q);
            TupleQueryResult rs = query.evaluate();
            long subjCount = Long.parseLong(rs.next().getValue("subjs").stringValue());
            return  subjCount;
        }
    }

    private static long countDistinctPredicates(Repository repo) throws Exception {
        String q = "SELECT (COUNT(DISTINCT ?p) AS ?preds) " +
          "WHERE { " +
          "?s ?p ?o " +
          "}";
        try (RepositoryConnection con = repo.getConnection()) {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, q);
            TupleQueryResult rs = query.evaluate();
            long predCount = Long.parseLong(rs.next().getValue("preds").stringValue());
            return  predCount;
        }
    }

    private static long countDistinctObjects(Repository repo) throws Exception {
        String q = "SELECT (COUNT(DISTINCT ?o) AS ?objs) " +
          "WHERE { " +
          "?s ?p ?o " +
          "}";
        try (RepositoryConnection con = repo.getConnection()) {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, q);
            TupleQueryResult rs = query.evaluate();
            long objCount = Long.parseLong(rs.next().getValue("objs").stringValue());
            return  objCount;
        }
    }

    private static void listDistinctPredicates(Repository repo) throws Exception {
        String q = "SELECT DISTINCT ?p " +
          "WHERE { " +
          "?s ?p ?o " +
          "}";
        try (RepositoryConnection con = repo.getConnection()) {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, q);
            TupleQueryResult rs = query.evaluate();
            while (rs.hasNext()) {
                System.out.println(rs.next().getValue("p"));
            }
        }
    }

    private static void listPredicates(Repository repo) throws Exception {
        System.out.println("\nPredicates");
        String q = "SELECT ?p (COUNT(?p) as ?cp)" +
          "WHERE { " +
          "?s ?p ?o " +
          "} " +
          "GROUP BY ?p " +
          "ORDER BY ?cp";
        try (RepositoryConnection con = repo.getConnection()) {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, q);
            TupleQueryResult rs = query.evaluate();
            while (rs.hasNext()) {
                BindingSet bs = rs.next();
                System.out.println(Long.parseLong(bs.getValue("cp").stringValue()) + " \t" + bs.getValue("p"));
            }
        }
    }

    private static int countPredNamespace(Repository repo, String ns) throws Exception {
        String q = "SELECT ?p " +
          "WHERE { " +
          "?s ?p ?o ." +
          "FILTER(STRSTARTS(STR(?p), \"" + ns + "\")) " +
          "}";
        try (RepositoryConnection con = repo.getConnection()) {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, q);
            TupleQueryResult rs = query.evaluate();
            int predCount = 0;
            while(rs.hasNext()) {
                rs.next();
                predCount++;
            }
            System.out.println("\nCount predicates that start with " + ns + " " + predCount);
            return  predCount;
        }
    }

    private static int countObjNamespace(Repository repo, String ns) throws Exception {
        String q = "SELECT ?o " +
          "WHERE { " +
          "?s ?p ?o ." +
          "FILTER(STRSTARTS(STR(?o), \"" + ns + "\")) " +
          "}";
        try (RepositoryConnection con = repo.getConnection()) {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, q);
            TupleQueryResult rs = query.evaluate();
            int objCount = 0;
            while(rs.hasNext()) {
                rs.next();
                objCount++;
            }
            System.out.println("\nCount objects that start with " + ns + " " + objCount);
            return  objCount;
        }
    }

    private static void listStatements(Repository repo, String subj, String pred, String obj) throws Exception {
        System.out.println("\nStatements with " + subj + " " + pred + " " + obj);
        Resource sub = subj == null? null : repo.getValueFactory().createIRI(subj);
        IRI pre = pred == null ? null : repo.getValueFactory().createIRI(pred);
        Value ob = obj == null ? null : repo.getValueFactory().createIRI(obj);

        try (RepositoryConnection con = repo.getConnection()) {
            try (RepositoryResult<Statement> result = con.getStatements(sub, pre, null);) {
                while (result.hasNext()) {
                    Statement st = result.next();
                    System.out.println("contains: " + st);
                }
            }
        }
    }


}

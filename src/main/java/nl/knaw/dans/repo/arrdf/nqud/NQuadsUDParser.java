package nl.knaw.dans.repo.arrdf.nqud;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserFactory;
import org.eclipse.rdf4j.rio.RioSetting;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.NTriplesParserSettings;
import org.eclipse.rdf4j.rio.nquads.NQuadsParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Stack;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.rdf4j.rio.RDFFormat.NO_NAMESPACES;
import static org.eclipse.rdf4j.rio.RDFFormat.SUPPORTS_CONTEXTS;

/**
 * Copied from nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j.parsers.NquadsUdParser,
 * without the nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j.parsers.TimRdfHandler.
 */
public class NQuadsUDParser extends NQuadsParser {

    private static final RDFFormat NQUAD_UD_FORMAT = new RDFFormat(
      "NQuadsUnifiedDiff",
      "application/vnd.timbuctoo-rdf.nquads_unified_diff",
      UTF_8,
      "nqud",
      NO_NAMESPACES,
      SUPPORTS_CONTEXTS
    );
    private Stack<Integer> actions;

    private int plusStatementCount;
    private int minusStatementCount;

    public NQuadsUDParser() {
        actions = new Stack<>();
        ParserConfig config = getParserConfig();
        config.set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, true);
        config.set(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS, true);
        config.set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
    }

    public synchronized void parse(URL url, String baseUri) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            parse(reader, baseUri);
        }
    }

    @Override
    public synchronized void parse(Reader reader, String baseUri)
      throws IOException, RDFParseException, RDFHandlerException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader can not be 'null'");
        }
        // if (baseUri == null) {
        //     throw new IllegalArgumentException("base URI can not be 'null'");
        // }

        if (rdfHandler != null) {
            rdfHandler.startRDF();
        }

        this.reader = reader;
        lineNo = 1;

        reportLocation(lineNo, 1);

        try {
            int character = readCodePoint();
            while (character != -1) {
                if (character == '#' || character == ' ' || character == '@' || character == '\\') {
                    // Comment, ignore
                    character = skipLine(character);
                } else if (character == '+' || character == '-') {
                    int action = character;
                    character = readCodePoint();
                    if (character == '<' || character == '_') {
                        setAction(action);
                    } else {
                        character = skipLine(character);
                    }
                } else if (character == '\r' || character == '\n') {
                    // Empty line, ignore
                    character = skipLine(character);
                } else {
                    character = parseQuad(character);
                }
            }
        } finally {
            clear();
        }

        if (rdfHandler != null) {
            rdfHandler.endRDF();
        }
    }

    private void setAction(int action) {
        actions.push(action);
        if (action == '+') {
            plusStatementCount++;
        } else {
            minusStatementCount++;
        }
    }

    private int parseQuad(int character)
      throws IOException, RDFParseException, RDFHandlerException {

        boolean ignoredAnError = false;
        try {
            character = parseSubject(character);

            character = skipWhitespace(character);

            character = parsePredicate(character);

            character = skipWhitespace(character);

            character = parseObject(character);

            character = skipWhitespace(character);

            // Context is not required
            if (character != '.') {
                character = parseContext(character);
                character = skipWhitespace(character);
            }
            if (character == -1) {
                throwEOFException();
            } else if (character != '.') {
                reportFatalError("Expected '.', found: " + new String(Character.toChars(character)));
            }

            character = assertLineTerminates(character);
        } catch (RDFParseException rdfpe) {
            if (getParserConfig().isNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES)) {
                reportError(rdfpe, NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
                ignoredAnError = true;
            } else {
                throw rdfpe;
            }
        }

        character = skipLine(character);

        if (!ignoredAnError) {
            Statement st = createStatement(subject, predicate, object, context);
            if (rdfHandler != null) {
                rdfHandler.handleStatement(st);
            }
        }

        subject = null;
        predicate = null;
        object = null;
        context = null;

        return character;
    }

    // @Override
    // public RDFParser setRDFHandler(RDFHandler handler) {
    //   if (handler instanceof TimRdfHandler) {
    //     // It might be nicer to override statement, to make it contain the action, but it takes to much effort for
    // now.
    //     ((TimRdfHandler) handler).registerActionSupplier(() -> actions.pop());
    //   }
    //   return super.setRDFHandler(handler);
    // }


    @Override
    public RDFParser setRDFHandler(RDFHandler handler) {
        if (handler instanceof NQuadsUDAssembler) {
            ((NQuadsUDAssembler) handler).registerActionSupplier(() -> actions.pop());
        }
        return super.setRDFHandler(handler);
    }

    public boolean isAssertion() {
        return actions.pop() == '+';
    }

    public int getPlusStatementCount() {
        return plusStatementCount;
    }

    public int getMinusStatementCount() {
        return minusStatementCount;
    }

    @Override
    public RDFFormat getRDFFormat() {
        return NQUAD_UD_FORMAT;
    }

    public static class NquadsUdParserFactory implements RDFParserFactory {
        @Override
        public RDFFormat getRDFFormat() {
            return NQUAD_UD_FORMAT;
        }

        @Override
        public RDFParser getParser() {
            return new NQuadsUDParser();
        }
    }

}

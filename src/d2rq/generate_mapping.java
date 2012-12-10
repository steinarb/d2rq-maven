package d2rq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import jena.cmdline.ArgDecl;
import jena.cmdline.CommandLine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.d2rq.CommandLineTool;
import org.d2rq.SystemLoader;
import org.d2rq.lang.D2RQWriter;
import org.d2rq.mapgen.MappingGenerator;

import com.hp.hpl.jena.rdf.model.Model;


/**
 * Command line interface for {@link MappingGenerator}.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class generate_mapping extends CommandLineTool {
	private final static Log log = LogFactory.getLog(generate_mapping.class);
	
	public static void main(String[] args) {
		new generate_mapping().process(args);
	}
	
	public void usage() {
		System.err.println("usage: generate-mapping [options] jdbcURL");
		System.err.println();
		printStandardArguments(false, false);
		System.err.println("  Options:");
		printConnectionOptions(true);
		System.err.println("    -o outfile.ttl  Output file name (default: stdout)");
		System.err.println("    -v              Generate RDFS+OWL vocabulary instead of mapping file");
		System.err.println("    --verbose       Print debug information");
		System.err.println();
		System.exit(1);
	}

	private ArgDecl outfileArg = new ArgDecl(true, "o", "out", "outfile");
	private ArgDecl vocabAsOutput = new ArgDecl(false, "v", "vocab");
	
	public void initArgs(CommandLine cmd) {
		cmd.add(outfileArg);
		cmd.add(vocabAsOutput);
	}

	public void run(CommandLine cmd, SystemLoader loader) throws IOException {
		if (cmd.numItems() == 1) {
			loader.setJdbcURL(cmd.getItem(0));
		}
		
		PrintStream out;
		if (cmd.contains(outfileArg)) {
			File f = new File(cmd.getArg(outfileArg).getValue());
			log.info("Writing to " + f);
			out = new PrintStream(new FileOutputStream(f));
		} else {
			log.info("Writing to stdout");
			out = System.out;
		}

		MappingGenerator generator = loader.getMappingGenerator();
		try {
			if (cmd.contains(vocabAsOutput)) {
				Model model = generator.getVocabularyModel();
				model.write(out, "TURTLE");
			} else {
				new D2RQWriter(generator.getMapping()).write(out);
			}
		} finally {
			loader.close();
		}
	}
}

package edu.isi.bmkeg.lapdf.bin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import edu.isi.bmkeg.lapdf.controller.LapdfEngine;
import edu.isi.bmkeg.lapdf.model.ChunkBlock;
import edu.isi.bmkeg.lapdf.model.LapdfDirection;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.lapdf.model.RTree.RTChunkBlock;
import edu.isi.bmkeg.lapdf.model.spatial.SpatialEntity;

/**
 * This script runs through the digital library and extracts all fragments for a
 * given corpus
 * 
 * @author Gully
 * 
 */
public class ExtractFigureImagesFromFile {

	public static class Options {

		@Option(name = "-outDir", usage = "Output", required = true, metaVar = "OUTPUT")
		public File outdir;

		@Option(name = "-pdf", usage = "PDF", required = true, metaVar = "PDF")
		public File pdf;

		@Option(name = "-stem", usage = "Stem", required = true, metaVar = "Stem")
		public String stem = "";

	}

	private static Logger logger = Logger.getLogger(ExtractFigureImagesFromFile.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Options options = new Options();
		Pattern patt = Pattern.compile("^(Figure|Fig\\.{0,1})\\s+(\\d+)");

		Map<Integer, Set<String>> pmids = new HashMap<Integer, Set<String>>();
		Map<Integer, String> pmcids = new HashMap<Integer, String>();
		Map<String, String> pdfLocs = new HashMap<String, String>();

		CmdLineParser parser = new CmdLineParser(options);

		int nLapdfErrors = 0, nSwfErrors = 0, total = 0;

		try {

			parser.parseArgument(args);

			LapdfEngine eng = new LapdfEngine();
			Map<String, BufferedImage> imageList = eng.extractFiguresFromArticle(options.pdf);
			
			for(String name : imageList.keySet()) {

				BufferedImage img = imageList.get(name);
				File outputfile = new File(options.outdir + "/"
						+ options.stem + "_" + name + ".png");
				ImageIO.write(img, "png", outputfile);

				logger.info("Extracting image to " + options.outdir
						+ "/" + options.stem + "_" + name
						+ ".png");
				
			}

		} catch (CmdLineException e) {

			System.err.println(e.getMessage());
			System.err.print("Arguments: ");
			parser.printSingleLineUsage(System.err);
			System.err.println("\n\n Options: \n");
			parser.printUsage(System.err);
			System.exit(-1);

		} catch (Exception e2) {

			e2.printStackTrace();

		}

	}

}

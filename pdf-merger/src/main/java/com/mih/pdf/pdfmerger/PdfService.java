package com.mih.pdf.pdfmerger;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;

public class PdfService {
    public static void main(String[] args) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();

        //Setting the destination file
        merger.setDestinationFileName("/Users/pf65op/Downloads/tinta-ingrid.pdf");
        Stream.of("/Users/pf65op/Downloads/certificat casatorie.pdf",
                "/Users/pf65op/Downloads/tinta ingrid mihaita.pdf",
                "/Users/pf65op/Downloads/tinta oana georgiana.pdf")
                .map(s -> new File(s))
                .forEach(f -> addAll(merger, f));

        merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        System.out.println("Documents merged");
        toImage("/Users/pf65op/Downloads/tinta-ingrid.pdf", "/Users/pf65op/Downloads/tinta-ingrid");
    }

    private static void addAll(PDFMergerUtility merger, File f) {
        try {
            merger.addSource(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void toImage(String in, String out) throws IOException {

        PDDocument pd = PDDocument.load (new File (in));
        PDFRenderer pr = new PDFRenderer (pd);
        int[] dpis = {50, 100, 100, 50};
        for (int i = 0; i< pd.getNumberOfPages(); i++) {
            BufferedImage bi = pr.renderImageWithDPI(i, dpis[i]);
            ImageIO.write(bi, "JPG", new File(out + "-" + i + ".jpg"));
        }
    }
}

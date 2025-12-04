package mx.edu.utez.proyectorecetario.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import mx.edu.utez.proyectorecetario.dao.RecetaDAO;
import mx.edu.utez.proyectorecetario.dao.UsuarioDAO;
import mx.edu.utez.proyectorecetario.model.Receta;
import mx.edu.utez.proyectorecetario.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFUtil {

    public static void exportarReceta(String rutaArchivo, Receta receta) throws IOException {
        PdfWriter writer = new PdfWriter(rutaArchivo);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        Paragraph titulo = new Paragraph(receta.getTitulo() != null ? receta.getTitulo() : "Sin título")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18);
        document.add(titulo);

        Usuario autor = new UsuarioDAO().buscarPorId(receta.getId_usuario());
        String nombreAutor = (autor != null) ? autor.getNombre_usuario() : "Desconocido";
        Paragraph pAutor = new Paragraph("Autor: " + nombreAutor)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(pAutor);

        List<Integer> idsCategorias = new RecetaDAO().obtenerCategoriasPorReceta(receta.getId_receta());
        String[] todasCategorias = {"desayuno","comida","cena","postre","bebida",
                "vegetariano","saludable","internacional","carne","pescado"};
        StringBuilder sbCategorias = new StringBuilder("Categorías: ");
        for (int i = 0; i < idsCategorias.size(); i++) {
            int id = idsCategorias.get(i);
            if (id >= 1 && id <= todasCategorias.length) {
                sbCategorias.append(todasCategorias[id-1]);
                if (i < idsCategorias.size() - 1) sbCategorias.append(", ");
            }
        }
        Paragraph pCategorias = new Paragraph(sbCategorias.toString())
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(pCategorias);

        if (receta.getDescripcion() != null && !receta.getDescripcion().isEmpty()) {
            Paragraph pDescripcion = new Paragraph("Descripción:\n" + receta.getDescripcion())
                    .setFontSize(12);
            document.add(pDescripcion);
        }

        if (receta.getImagen() != null && !receta.getImagen().isEmpty()) {
            File imgFile = new File(System.getProperty("user.home") + "/Recetario/imagenes/" + receta.getImagen());
            if (imgFile.exists()) {
                ImageData data = ImageDataFactory.create(imgFile.getAbsolutePath());
                Image img = new Image(data).scaleToFit(400, 400).setAutoScale(true);
                document.add(img);
            }
        }

        Paragraph pExtra = new Paragraph(
                "Dificultad: " + (receta.getDificultad() != null ? receta.getDificultad() : "-") +
                        " | Duración: " + (receta.getDuracion() != null ? receta.getDuracion() : "-")
        ).setFontSize(12);
        document.add(pExtra);

        if (receta.getIngredientes() != null && !receta.getIngredientes().isEmpty()) {
            Paragraph pIngredientes = new Paragraph("Ingredientes:\n" + receta.getIngredientes())
                    .setFontSize(12);
            document.add(pIngredientes);
        }

        if (receta.getPasos() != null && !receta.getPasos().isEmpty()) {
            Paragraph pPasos = new Paragraph("Preparación:\n" + receta.getPasos())
                    .setFontSize(12);
            document.add(pPasos);
        }

        document.close();
    }

}

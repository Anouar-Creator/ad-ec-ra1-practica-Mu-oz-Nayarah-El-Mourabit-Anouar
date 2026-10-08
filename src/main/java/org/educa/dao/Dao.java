package org.educa.dao;

import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.List;

public class Dao implements DaoInter{

/**
     *Lee los productos del fichero xml y los devuelve a una lista
     * @param fileXml es la ruta del fichero XML
     * @return lista de los productos leidos del xml
     * @throws JAXBException  Excepcion procesando el fichero XML
     */
    @Override
    public List<Producto> readFile(String fileXml) throws JAXBException {

        JAXBContext contexto = JAXBContext.newInstance(Productos.class);
        Unmarshaller unmarshaller=contexto.createUnmarshaller();

        Productos productos =(Productos) unmarshaller.unmarshal(new File(fileXml));

        return productos.getProducto();


        /**
     * Sirve paara escribir y crear el resumen en el fichero de texto
     * @param path ruta donde se guardará el fichero
     * @param fecha es elmes y año que aparecerá en el nombre del fichero
     * @param summary es resumen que se va a guardar
     * @throws IOException sí ocurre un error al crear o escribir el fichero
     */
    @Override
    public void exportSummary(String path, String fecha, SummaryEntity summary) throws IOException {

        File carpeta = new File(path);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        File ficheroTXT = new File(
                carpeta,
                "result_" + fecha + ".txt"
        );

        BufferedWriter writer = new BufferedWriter(
                new FileWriter(ficheroTXT)
        );

        writer.write(summary.toPrint());

        writer.close();

}
}

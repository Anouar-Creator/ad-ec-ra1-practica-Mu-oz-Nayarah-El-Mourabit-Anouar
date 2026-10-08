package org.educa.dao;

import generated.Producto;
import jakarta.xml.bind.JAXBException;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.List;
public interface DaoInter {

  /**
     *Lee los productos del fichero xml y los devuelve a una lista
     * @param fileXml es la ruta del fichero XML
     * @return lista de los productos leidos del xml
     * @throws JAXBException  Excepcion procesando el fichero XML
     */
    List<Producto> readFile(String fileXml) throws JAXBException;

   /**
     * Guarda el resumen en un  fichero de texto
     * @param path ruta donde se guardará el fichero
     * @param fecha es elmes y año que aparecerá en el nombre del fichero
     * @param summary es resumen que se va a guardar
     */
    void exportSummary(String path, String fecha, SummaryEntity summary) throws IOException;




}

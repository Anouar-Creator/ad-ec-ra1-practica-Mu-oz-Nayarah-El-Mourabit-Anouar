package org.educa.service;

import generated.Producto;
import jakarta.xml.bind.JAXBException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.educa.dao.Dao;
import org.educa.dao.DaoInter;
import org.educa.entity.ProductoEntity;
import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    /**
     *Lee los productos del fichero XML y crea una lista de ProductoEntity
     * @param fileXml La ruta al fichero XML
     * @return Devueleve los productos
     * @throws JAXBException Excepcion procesando el fichero XML
     */

    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {
         // Queremos acceder al DAO para poder obtener los productos del XML
        DaoInter dao=new Dao();
        // Queremos obtener todos los productos que contiene el fichero XML
        List<Producto> productos=dao.readFile(fileXml);
        List<ProductoEntity> listaProductos=new ArrayList<>();

        for(Producto producto:productos){

            ProductoEntity entidad=new ProductoEntity();

            entidad.setProducto(producto);
            // Queremos obtener cuánto dinero se descuenta del precio según el porcentaje indicado
            BigDecimal descuento=producto.getPrecio().multiply(producto.getDescuento().divide(new BigDecimal(100)));
            // Queremos obtener el precio que queda después de aplicar el descuento
            BigDecimal preciofinal=producto.getPrecio().subtract(descuento);

            BigDecimal CosteEnvio= producto.getCostes().getCostesEnvio();

            BigDecimal CosteAlmacenaje= producto.getCostes().getCostesAlmacenaje();

            BigDecimal coste=CosteEnvio.add(CosteAlmacenaje);

            BigDecimal  Beneficio=preciofinal.subtract(coste);
            // Queremos guardar el precio final calculado en la entidad
            entidad.setPrecioFinal(preciofinal);
            entidad.setCost(coste);
            entidad.setProfit(Beneficio);

            listaProductos.add(entidad);


        }
        return listaProductos;
    }
    

    /**
     * Sirve para guardar el resumen en un fichero de texto
     * @param path donde se guarda el fichero
     * @param fileXml ruta del fichero xml
     * @throws JAXBException si pasa algun errror al leer
     */
    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
         DaoInter dao = new Dao();
        List<ProductoEntity> listaProductos = readFile(fileXml);
        int numeroProductos = listaProductos.size();


        //Inicializo el benificio total a 0
        BigDecimal beneficioTotal = BigDecimal.ZERO;

        //Sumo los beneficios
        for (ProductoEntity producto : listaProductos) {
            beneficioTotal = beneficioTotal.add(producto.getProfit());
        }

        File ficheroXML = new File(fileXml);

        String nombreFichero = ficheroXML.getName();

        //Saco el nombre sin la extencion
        String nombreSinExtension = nombreFichero.substring(0, nombreFichero.lastIndexOf("."));

        //Saco el mes y año del fichero
        String fecha = nombreSinExtension.substring(nombreSinExtension.indexOf("_") + 1);

        String rutaAbsoluta = ficheroXML.getAbsolutePath();


        //Obtengo el tamaño del fichero en bytes
        long tamanioFichero = ficheroXML.length();

        //Creamos el resumen
        SummaryEntity summary = new SummaryEntity(fecha, numeroProductos, beneficioTotal, rutaAbsoluta, nombreSinExtension, tamanioFichero);

        dao.exportSummary(path, fecha, summary);

    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}

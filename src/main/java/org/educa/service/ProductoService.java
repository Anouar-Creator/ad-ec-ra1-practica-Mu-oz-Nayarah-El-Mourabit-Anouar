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
        DaoInter dao = new Dao();
        List<ProductoEntity> lista = readFile(fileXml);

        Workbook workbook = new XSSFWorkbook();
        Sheet hoja = workbook.createSheet("Productos");

        // Ponemos los formatos del dinero y los porcentajes
        short formatoDinero = workbook.createDataFormat().getFormat("#,##0.00 €");
        short formatoPorcentaje = workbook.createDataFormat().getFormat("0.00%");

        // Ponemos estilos de cabecera
        CellStyle estiloCabecera = workbook.createCellStyle();
        Font fuenteCabecera = workbook.createFont();
        fuenteCabecera.setBold(true);
        estiloCabecera.setFont(fuenteCabecera);
        estiloCabecera.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecera.setVerticalAlignment(VerticalAlignment.CENTER);

        estiloCabecera.setBorderTop(BorderStyle.THIN);
        estiloCabecera.setBorderBottom(BorderStyle.THIN);
        estiloCabecera.setBorderLeft(BorderStyle.THIN);
        estiloCabecera.setBorderRight(BorderStyle.THIN);

        estiloCabecera.setTopBorderColor(IndexedColors.GREEN.getIndex());
        estiloCabecera.setBottomBorderColor(IndexedColors.GREEN.getIndex());
        estiloCabecera.setLeftBorderColor(IndexedColors.GREEN.getIndex());
        estiloCabecera.setRightBorderColor(IndexedColors.GREEN.getIndex());


        // Elegimos colores (en este caso decidimos dejar el verde y blanco del ejemplo)
        CellStyle colorVerde = workbook.createCellStyle();
        colorVerde.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        colorVerde.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        colorVerde.setAlignment(HorizontalAlignment.CENTER);
        colorVerde.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle colorBlanco = workbook.createCellStyle();
        colorBlanco.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        colorBlanco.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        colorBlanco.setAlignment(HorizontalAlignment.CENTER);
        colorBlanco.setVerticalAlignment(VerticalAlignment.CENTER);

        // Ponemos los bordes verdes a los estilos
        for (CellStyle estilo : new CellStyle[]{colorVerde, colorBlanco}) {
            estilo.setBorderTop(BorderStyle.THIN);
            estilo.setBorderBottom(BorderStyle.THIN);
            estilo.setBorderLeft(BorderStyle.THIN);
            estilo.setBorderRight(BorderStyle.THIN);

            estilo.setTopBorderColor(IndexedColors.GREEN.getIndex());
            estilo.setBottomBorderColor(IndexedColors.GREEN.getIndex());
            estilo.setLeftBorderColor(IndexedColors.GREEN.getIndex());
            estilo.setRightBorderColor(IndexedColors.GREEN.getIndex());
        }


        // Estilos de código, dinero y porcentaje
        Font fuenteNegrita = workbook.createFont();
        fuenteNegrita.setBold(true);

        CellStyle codigoVerde = workbook.createCellStyle(); codigoVerde.cloneStyleFrom(colorVerde); codigoVerde.setFont(fuenteNegrita);
        CellStyle codigoBlanco = workbook.createCellStyle(); codigoBlanco.cloneStyleFrom(colorBlanco); codigoBlanco.setFont(fuenteNegrita);

        CellStyle dineroVerde = workbook.createCellStyle(); dineroVerde.cloneStyleFrom(colorVerde); dineroVerde.setDataFormat(formatoDinero);
        CellStyle dineroBlanco = workbook.createCellStyle(); dineroBlanco.cloneStyleFrom(colorBlanco); dineroBlanco.setDataFormat(formatoDinero);

        CellStyle porcentajeVerde = workbook.createCellStyle(); porcentajeVerde.cloneStyleFrom(colorVerde); porcentajeVerde.setDataFormat(formatoPorcentaje);
        CellStyle porcentajeBlanco = workbook.createCellStyle(); porcentajeBlanco.cloneStyleFrom(colorBlanco); porcentajeBlanco.setDataFormat(formatoPorcentaje);

        // Titulos de cada fila
        Row filaCabecera = hoja.createRow(0);
        String[] cabeceras = {
                "Codigo", "Número de Serie", "Precio", "Descuento", "Precio Final", "Costes Envío", "Costes Almacenaje", "Beneficio"
        };

        for (int i = 0; i < cabeceras.length; i++) {
            Cell celda = filaCabecera.createCell(i);
            celda.setCellValue(cabeceras[i]);
            celda.setCellStyle(estiloCabecera);
        }



        
    }
}

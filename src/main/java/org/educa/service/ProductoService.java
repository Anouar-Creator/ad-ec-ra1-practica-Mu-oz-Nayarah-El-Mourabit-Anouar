package org.educa.service;

import generated.Producto;
import jakarta.xml.bind.JAXBException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.educa.dao.Dao;
import org.educa.dao.DaoInter;
import org.educa.entity.ProductoEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoService {

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
    

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        //TODO: Implementar

    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}

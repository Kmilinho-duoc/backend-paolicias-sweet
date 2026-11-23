package com.paolicias.productos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.paolicias.productos.entities.Producto;
import com.paolicias.productos.repository.ProductoRepository;

// Al iniciar, si la tabla está vacía, carga los 5 productos de ejemplo del catálogo de la app
@Component
public class DatosIniciales implements CommandLineRunner {

    private final ProductoRepository productoRepo;

    public DatosIniciales(ProductoRepository productoRepository) {
        this.productoRepo = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepo.count() > 0) {
            return;
        }

        productoRepo.save(crear("Torta Tres Leches", "Bizcocho húmedo con crema", "Tortas", 28000, false,
                "🎂", 0xFFFFD9E2L, "torta_tres_leches",
                List.of("Huevos", "Harina", "Azúcar", "Leche entera", "Leche condensada", "Crema de leche", "Esencia de vainilla"),
                "12 porciones", "Gluten, huevo y lácteos", "3 días antes del evento", new HashMap<>()));

        productoRepo.save(crear("Torta Selva Negra", "Chocolate, cerezas y crema", "Tortas", 32000, false,
                "🍫", 0xFFE8DEF8L, "torta_selva_negra",
                List.of("Cacao amargo", "Harina", "Huevos", "Azúcar", "Crema chantilly", "Cerezas en almíbar", "Chocolate rallado"),
                "12 porciones", "Gluten, huevo y lácteos", "3 días antes del evento", new HashMap<>()));

        productoRepo.save(crear("Pastelería fina", "Mini tartaletas y petit fours", "Pastelería", 18000, false,
                "🧁", 0xFFFFF0C2L, "pasteleria_fina",
                List.of("Masa quebrada", "Crema pastelera", "Frutas de temporada", "Ganache de chocolate", "Almendras"),
                "30 piezas surtidas", "Gluten, huevo, lácteos y frutos secos", "5 días antes del evento", new HashMap<>()));

        productoRepo.save(crear("Coffee break corporativo", "Para 10 a 100 personas", "Catering", 6500, true,
                "☕", 0xFFD5F0DCL, "coffee_break",
                List.of("Café de grano y té", "Mini sándwiches surtidos", "Mini kuchenes", "Galletas artesanales", "Jugo natural"),
                "De 10 a 100 personas",
                "Gluten y lácteos. Consultar opciones vegetarianas, sin gluten o aptas para hipertensos",
                "7 días antes del evento",
                new HashMap<>(Map.of(
                        "10", 6500, "20", 6400, "30", 6300, "40", 6200, "50", 6100,
                        "60", 6000, "70", 5900, "80", 5800, "90", 5700, "100", 5600))));

        productoRepo.save(crear("Cheesecake de frutilla", "Base crocante y frutos rojos", "Pastelería", 24000, false,
                "🍰", 0xFFFFD9E2L, "cheese_frutilla",
                List.of("Galleta molida", "Mantequilla", "Queso crema", "Crema de leche", "Frutillas", "Azúcar"),
                "10 porciones", "Gluten y lácteos", "3 días antes del evento", new HashMap<>()));
    }

    private Producto crear(String nombre, String descripcion, String categoria, int precio, boolean porPersona,
            String emoji, long colorFondo, String imagen, List<String> ingredientes, String rinde,
            String alergenos, String anticipacion, Map<String, Integer> tarifario) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setPorPersona(porPersona);
        producto.setEmoji(emoji);
        producto.setColorFondo(colorFondo);
        producto.setImagen(imagen);
        producto.setIngredientes(new java.util.ArrayList<>(ingredientes));
        producto.setRinde(rinde);
        producto.setAlergenos(alergenos);
        producto.setAnticipacion(anticipacion);
        producto.setTarifario(tarifario);
        return producto;
    }
}

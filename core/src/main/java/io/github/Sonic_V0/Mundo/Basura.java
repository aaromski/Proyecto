package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;

/**
 * Representa una unidad de {@link Contaminacion} del tipo basura dentro del mundo Box2D.
 *
 * Esta clase extiende {@link Contaminacion} y configura sus propiedades visuales y físicas
 * específicas para el comportamiento de basura en el juego.
 *
 * @author Aarom Luces
 */
public class Basura extends Contaminacion {

    /**
     * Crea una nueva instancia de basura en el mundo físico.
     *
     * @param world El mundo de Box2D donde será añadida la basura.
     */
    public Basura(World world) {
        super(world); // Pasa el world al constructor de Contaminacion
        textura = new Sprite(new Texture("Mapa1/trash.png"));
        textura.setSize(0.8f, 0.8f);
    }

    /**
     * Crea el cuerpo físico de la basura en una posición específica.
     *
     * @param posicion Vector de posición donde se colocará la basura.
     */
    @Override
    public void crearCuerpo(Vector2 posicion) {
        super.crearCuerpo(posicion);
    }

    /**
     * Configura el filtro de colisión para el cuerpo de basura.
     *
     * Establece la categoría como {@code CATEGORY_TRASH} y evita colisiones con {@code CATEGORY_ROBOT}.
     *
     * @param fdef La definición del fixture que será configurada.
     */
    @Override
    protected void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_TRASH;
        fdef.filter.maskBits = ~(Constantes.CATEGORY_ROBOT);
    }
}

package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;

/**
 * Clase que representa una nube de contaminación en el juego.
 * <p>
 * La nube es un tipo especial de contaminación que se mueve por el escenario
 * y puede interactuar con otros elementos del juego. Hereda de la clase Contaminacion.
 *
 * @author Yoryelis Ocando
 * @version 1.0
 * @see Contaminacion
 */
public class Nube extends Contaminacion {

    /**
     * Constructor de la clase Nube.
     *
     * @param world Mundo Box2D donde existirá el cuerpo físico de la nube
     */
    public Nube(World world) {
        super(world); // Pasa el world al constructor de Contaminacion
        textura = new Sprite(new Texture("Mapa1/dr-robotnik-130.png"));
        textura.setSize(0.8f, 0.8f); // Tamaño del sprite de la nube
    }

    /**
     * Crea el cuerpo físico de la nube y le aplica un impulso inicial.
     *
     * @param posicion Posición inicial donde se creará la nube
     * @param direccion Vector que indica la dirección y fuerza del movimiento inicial
     */
    public void crearCuerpo(Vector2 posicion, Vector2 direccion) {
        super.crearCuerpo(posicion); // Llama al crearCuerpo de Contaminacion

        float fuerza = 3.0f; // Fuerza del impulso inicial
        // Aplica un impulso lineal en la dirección especificada
        cuerpo.applyLinearImpulse(direccion.scl(fuerza), cuerpo.getWorldCenter(), true);
    }

    /**
     * Configura los filtros de colisión específicos para la nube.
     * <p>
     * La nube no colisionará con robots (CATEGORY_ROBOT) ni con basura (CATEGORY_TRASH).
     *
     * @param fdef Definición del fixture que se va a configurar
     */
    @Override
    protected void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_NUBE; // Categoría de este objeto
        // Máscara de bits que indica con qué categorías NO colisionará
        fdef.filter.maskBits = (short) ~(Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_TRASH);
    }
}

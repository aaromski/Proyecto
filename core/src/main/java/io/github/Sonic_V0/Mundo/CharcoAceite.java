package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import io.github.Sonic_V0.Constantes;

/**
 * Clase que representa un charco de aceite recolectable en el juego.
 * <p>
 * Aunque visualmente se representa como un charco de aceite, funciona como un ítem
 * recolectable por Sonic otorgando puntos al jugador.
 * Es un sensor para los robots, para los personajes es un objeto.
 *
 * @author Miguel Rivas
 * @version 1.1
 * @see Contaminacion
 * @see Constantes
 */
public class CharcoAceite extends Contaminacion {
    /**
     * Constructor de la clase CharcoAceite.
     * Inicializa el charco de aceite con su representación gráfica.
     *
     * @param world El mundo Box2D donde se creará este elemento.
     */
    public CharcoAceite(World world) {
        super(world);
        textura = new Sprite(new Texture("Mapa1/oil.png"));
        textura.setSize(1.5f, 1.5f);
    }

    /**
     * Crea el cuerpo físico para el charco de aceite en el mundo Box2D.
     * Este método sobrescribe el comportamiento de la clase padre para asegurar
     * que el cuerpo se configure correctamente.
     *
     * @param posicion Posición inicial donde se creará el cuerpo del charco.
     */
    @Override
    public void crearCuerpo(Vector2 posicion) {
        super.crearCuerpo(posicion);
    }

    /**
     * Configura los filtros de colisión para el charco de aceite.
     * Este charco actúa como un sensor y colisiona con todo excepto con los robots.
     *
     * @param fdef La definición del fixture a configurar.
     */
    @Override
    protected void configurarFiltro(FixtureDef fdef) {
        fdef.isSensor = true; // El charco es un sensor, no un objeto sólido.
        fdef.filter.categoryBits = Constantes.CATEGORY_TRASH; // Pertenece a la categoría de basura.
        // Colisiona con todo excepto con los robots.
        fdef.filter.maskBits = ~(Constantes.CATEGORY_ROBOT);
    }
}

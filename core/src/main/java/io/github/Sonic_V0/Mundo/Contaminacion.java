package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;

/**
 * Clase abstracta que representa un elemento de contaminación en el juego.
 * <p>
 * Proporciona la base para diferentes tipos de contaminación con funcionalidades comunes
 * como creación de cuerpo físico, renderizado y manejo de estado.
 *
 * @author Jesus
 * @version 1.1
 * @see Nube
 * @see Constantes
 */
public abstract class Contaminacion {
    /** Cuerpo físico Box2D asociado a este elemento */
    protected Body cuerpo;
    /** Sprite gráfico que representa el elemento */
    protected Sprite textura;
    /** Indica si el elemento está activo en el juego */
    protected boolean activa = true;
    /** Referencia al mundo Box2D donde existe este elemento */
    protected final World world;

    /**
     * Constructor base para elementos de contaminación.
     *
     * @param world Mundo Box2D donde se creará este elemento
     */
    public Contaminacion(World world) {
        this.world = world;
    }

    /**
     * Crea el cuerpo físico para este elemento de contaminación.
     *
     * @param posicion Posición inicial donde se creará el cuerpo
     */
    public void crearCuerpo(Vector2 posicion) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.4f, 0.4f); // Tamaño de la caja de colisión

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;       // Densidad media
        fdef.friction = 0.3f;    // Fricción baja

        BodyDef bdef = new BodyDef();
        // Las nubes son dinámicas, otros elementos son estáticos
        if (this instanceof Nube) {
            bdef.type = BodyDef.BodyType.DynamicBody;
        } else {
            bdef.type = BodyDef.BodyType.StaticBody;
        }

        bdef.position.set(posicion);

        cuerpo = world.createBody(bdef);
        configurarFiltro(fdef); // Configuración específica de cada subclase
        cuerpo.createFixture(fdef).setUserData(this);
        shape.dispose(); // Importante liberar recursos de forma
    }

    /**
     * Método abstracto para configurar los filtros de colisión.
     * <p>
     * Cada subclase debe implementar su propia configuración de colisiones.
     *
     * @param fdef Definición del fixture a configurar
     */
    abstract void configurarFiltro(FixtureDef fdef);

    /**
     * Dibuja el elemento de contaminación en el batch proporcionado.
     *
     * @param batch SpriteBatch donde se realizará el renderizado
     */
    public void render(SpriteBatch batch) {
        if (!activa) return; // No renderizar si no está activo

        // Centrar el sprite en la posición del cuerpo físico
        Vector2 pos = cuerpo.getPosition();
        textura.setPosition(
            pos.x - textura.getWidth() / 2,
            pos.y - textura.getHeight() / 2
        );

        textura.draw(batch);
    }

    /**
     * Destruye el cuerpo físico y desactiva este elemento.
     * <p>
     * Utiliza la referencia interna al mundo (world) en lugar de recibirlo como parámetro.
     */
    public void destruir() {
        if (cuerpo != null && activa) {
            this.world.destroyBody(cuerpo);
            cuerpo = null;
            activa = false; // Marcar como inactivo
        }
    }

    /**
     * Verifica si este elemento está activo en el juego.
     *
     * @return true si está activo, false en caso contrario
     */
    public boolean estaActiva() {
        return activa;
    }

    /**
     * Desactiva este elemento y suma puntos solo si representa basura.
     *
     * @param op Tipo de elemento:
     *           1 para basura, suma puntos en SCORE[0];
     *           ..* para nube (no suma puntos);
     */
    public void setActiva(int op) {
        if (activa) { // Solo si está activo puede sumar puntos y desactivarse
            if (op == 1) { // Solo basura suma puntos
                Constantes.SCORE[0] += 5;
            }
            activa = false; // Desactiva el elemento
        }
    }

    /**
     * Obtiene el cuerpo físico asociado a este elemento.
     *
     * @return El cuerpo Box2D de este elemento
     */
    public Body getCuerpo() {
        return cuerpo;
    }

    /**
     * Libera los recursos gráficos utilizados por este elemento.
     * <p>
     * Nota: El cuerpo físico debe ser destruido con el método destruir() previamente.
     */
    public void dispose() {
        if (textura != null && textura.getTexture() != null) {
            textura.getTexture().dispose();
        }
    }
}

package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import io.github.Sonic_V0.Constantes;

/**
 * Clase que representa al personaje jugable Knuckles.
 * <p>
 * Knuckles es un personaje "amigable" que tiene la habilidad especial de golpear
 * y destruir enemigos. Gestiona sus propias animaciones, movimientos y la lógica
 * de su ataque de golpe.
 *
 * @author Miguel Rivas
 * @version 1.0
 * @see Amigas
 * @see Constantes
 */
public class Knuckles extends Amigas {
    /** Animación de la explosión o impacto de un golpe. */
    private Animation<TextureRegion> explosion;
    /** Indica si Knuckles está actualmente realizando un golpe. */
    private boolean golpeando = false;
    /** El fixture que representa el área de golpe de Knuckles. */
    private Fixture golpeFixture;
    /** La forma del círculo utilizada para el fixture de golpe. */
    private CircleShape golpeShape;
    /** Tiempo transcurrido desde que el golpe se activó. */
    private float tiempoGolpeActivo = 0f;
    /** Duración durante la cual el golpe es activo y puede causar daño. */
    private final float DURACION_GOLPE_ACTIVO = 0.1f;
    /** Tiempo en la animación de golpe en el que el golpe se vuelve activo. */
    private final float TIEMPO_GOLPE_COMIENZO = 0.2f;
    /** Indica si la animación de impacto debe mostrarse. */
    private boolean mostrarImpacto = false;
    /** Tiempo de estado para la animación de impacto. */
    private float estadoImpacto = 0f;
    /** El frame actual de la animación de impacto. */
    private TextureRegion frameImpacto;
    /** Dirección del impacto del golpe (0=Ninguna, 1=Izq, 2=Der, 3=Arriba, 4=Abajo, 5-8 para diagonales). */
    private int direccionImpacto = 0;

    /**
     * Constructor de la clase Knuckles.
     *
     * @param posicion Posición inicial de Knuckles en el mundo.
     * @param world El mundo Box2D donde reside Knuckles.
     */
    public Knuckles (Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Knuckles";
        crearGolpeFixture(); // Crea el fixture de golpe
    }

    /**
     * Crea el fixture de golpe (sensor) para Knuckles.
     * Este fixture se utiliza para detectar colisiones con enemigos durante el ataque.
     */
    private void crearGolpeFixture() {
        final float PALO_RADIUS = 0.7f; // Radio del área de golpe
        golpeShape = new CircleShape();
        golpeShape.setRadius(PALO_RADIUS);

        // Posición inicial en el centro del cuerpo
        golpeShape.setPosition(new Vector2(0f, 0f));

        FixtureDef fdef = new FixtureDef();
        fdef.shape = golpeShape;
        fdef.isSensor = true; // El golpe es un sensor
        fdef.filter.categoryBits = Constantes.CATEGORY_GOLPE_PERSONAJES; // Categoría de golpe de personaje
        fdef.filter.maskBits = 0; // Inicialmente inactivo (no colisiona con nada)

        golpeFixture = body.createFixture(fdef);
        golpeFixture.setUserData("golpeKnuckles"); // Identificador del fixture
    }

    /**
     * Mueve la posición del fixture de golpe según la dirección actual de Knuckles.
     * Esto permite que el golpe se extienda en la dirección del movimiento del personaje.
     */
    private void moverGolpeSegunDireccion() {
        if (golpeFixture != null) {
            body.destroyFixture(golpeFixture); // Elimina el fixture anterior para recrearlo en la nueva posición
        }

        final float RADIO = 0.7f;
        final float DISTANCIA = 0.8f; // Distancia del offset del golpe
        Vector2 offset = new Vector2(); // Vector de desplazamiento del golpe

        // Calcula el offset según la dirección de impacto
        switch (direccionImpacto) {
            case 1: offset.set(-DISTANCIA, 0); break; // Izquierda
            case 2: offset.set(DISTANCIA, 0); break;  // Derecha
            case 3: offset.set(0, DISTANCIA); break;  // Arriba
            case 4: offset.set(0, -DISTANCIA); break; // Abajo
            case 5: offset.set(-DISTANCIA, DISTANCIA); break; // Diagonal Arriba Izquierda
            case 6: offset.set(DISTANCIA, DISTANCIA); break;  // Diagonal Arriba Derecha
            case 7: offset.set(-DISTANCIA, -DISTANCIA); break; // Diagonal Abajo Izquierda
            case 8: offset.set(DISTANCIA, -DISTANCIA); break;  // Diagonal Abajo Derecha
        }

        CircleShape newShape = new CircleShape();
        newShape.setRadius(RADIO);
        newShape.setPosition(offset); // Establece la posición del sensor de golpe

        FixtureDef fdef = new FixtureDef();
        fdef.shape = newShape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constantes.CATEGORY_GOLPE_PERSONAJES;
        fdef.filter.maskBits = Constantes.CATEGORY_ROBOT; // Solo colisiona con robots

        golpeFixture = body.createFixture(fdef);
        golpeFixture.setUserData("golpeKnuckles");

        newShape.dispose(); // Libera los recursos de la forma
    }

    /**
     * Activa el fixture de golpe de Knuckles, permitiendo que colisione con robots.
     */
    private void activarGolpeFixture() {
        activarGolpe(); // Llama al método que realmente modifica el filtro
    }

    /**
     * Desactiva el fixture de golpe de Knuckles, impidiendo que colisione con otros cuerpos.
     */
    private void desactivarGolpeFixtures() {
        if (golpeFixture != null) {
            Filter filter = golpeFixture.getFilterData();
            filter.maskBits = 0; // Desactiva todas las colisiones
            golpeFixture.setFilterData(filter);
        }
    }

    /**
     * Inicializa todas las animaciones y sprites de Knuckles.
     * Carga las texturas y define las animaciones para correr, golpear, etc.
     *
     * @param x Posición X inicial para el sprite.
     * @param y Posición Y inicial para el sprite.
     */
    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteKnuckles/KnucklesSprite.atlas"));
        TextureAtlas atlas2 = new TextureAtlas((Gdx.files.internal("SpriteKnuckles/explosion.atlas"))); // Atlas para la explosión
        sprite = atlas.createSprite("KnucklesStanding1"); // Sprite por defecto (parado)
        sprite.setSize(30f / Constantes.PPM, 39f / Constantes.PPM); // Ajusta tamaño del sprite
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = crearAnimacion("KnucklesRunning", 10, 0.09f); // Animación de correr
        abajo = crearAnimacion("Abajo", 6, 0.1f); // Animación de moverse hacia abajo
        arriba = crearAnimacion("Arriba", 8, 0.1f); // Animación de moverse hacia arriba
        diagonalarr = crearAnimacion("DiagonalTrasera", 8, 0.1f); // Animación de diagonal trasera
        diagonalabj = crearAnimacion("DiagonalDelantera", 6, 0.1f); // Animación de diagonal delantera

        habilidad = crearAnimacion("knucklesFist", 8, 0.1f); // Animación de golpe/habilidad

        Array<TextureRegion> framesExplosion = new Array<>();
        for (int i = 1; i <= 12; i++) {
            TextureRegion frame = atlas2.findRegion("explosion" + i);
            if (frame == null) {
                System.out.println("No se encontró explosion" + i); // Mensaje de depuración si falta un frame
            } else {
                framesExplosion.add(frame);
            }
        }
        explosion = new Animation<>(0.1f, framesExplosion, Animation.PlayMode.NORMAL); // Animación de explosión

        frameActual = new TextureRegion(sprite); // Inicializa el frame actual
    }

    /**
     * Configura los filtros de colisión para el cuerpo principal de Knuckles.
     * Knuckles colisiona con robots, basura, sensores, nubes y objetos del mapa.
     *
     * @param fdef La definición del fixture a configurar.
     */
    @Override
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES; // Pertenece a la categoría de personajes
        fdef.filter.maskBits = (short) (Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_TRASH |
            Constantes.CATEGORY_SENSOR | Constantes.CATEGORY_NUBE |
            Constantes.CATEGORY_OBJETOS); // Colisiona con estas categorías
    }

    /**
     * Activa el fixture de golpe de Knuckles, permitiendo que cause daño a los robots.
     * Este método es llamado internamente.
     */
    private void activarGolpe() {
        if (golpeFixture != null) {
            Filter filter = golpeFixture.getFilterData();
            filter.maskBits = Constantes.CATEGORY_ROBOT; // Habilita la colisión solo con robots
            golpeFixture.setFilterData(filter);
        }
    }

    /**
     * Inicia el ataque de golpe de Knuckles.
     * Establece el estado de golpeo, reinicia el tiempo de animación y determina la dirección del impacto.
     */
    public void golpear() {
        if (!golpeando) { // Solo permite golpear si no está golpeando ya
            golpeando = true;
            stateTime = 0f; // Reinicia el tiempo de estado para la animación de golpe
            tiempoGolpeActivo = 0f; // Reinicia el temporizador de golpe activo

            // Determina la dirección del impacto basada en las teclas de movimiento presionadas
            if (izq && arr) direccionImpacto = 5;
            else if (der && arr) direccionImpacto = 6;
            else if (izq && abj) direccionImpacto = 7;
            else if (der && abj) direccionImpacto = 8;
            else if (izq) direccionImpacto = 1;
            else if (der) direccionImpacto = 2;
            else if (arr) direccionImpacto = 3;
            else if (abj) direccionImpacto = 4;
            else direccionImpacto = 0; // Sin dirección específica (golpe en el lugar)
        }
    }

    /**
     * Actualiza el estado de Knuckles en cada frame, incluyendo su movimiento,
     * la lógica de golpe y la detección de entrada del usuario.
     *
     * @param alpha El tiempo transcurrido desde el último frame en segundos.
     */
    @Override
    public void actualizar(float alpha) {
        if (ko) { // Si Knuckles está KO, desactiva los golpes y no procesa más lógica
            desactivarGolpeFixtures();
            return;
        }

        if (golpeando) {
            tiempoGolpeActivo += alpha;
            stateTime += alpha;

            // Activa el golpe durante un corto período de tiempo en la animación
            if (tiempoGolpeActivo >= TIEMPO_GOLPE_COMIENZO &&
                tiempoGolpeActivo < (TIEMPO_GOLPE_COMIENZO + DURACION_GOLPE_ACTIVO)) {

                moverGolpeSegunDireccion(); // Actualiza la posición del fixture de golpe
                activarGolpeFixture();      // Activa el fixture para colisionar

                if (!mostrarImpacto) { // Inicia la animación de impacto si no está activa
                    mostrarImpacto = true;
                    estadoImpacto = 0f;
                }
            } else {
                desactivarGolpeFixtures(); // Desactiva el golpe fuera de la ventana activa
            }

            frameActual = habilidad.getKeyFrame(stateTime); // Obtiene el frame actual de la animación de golpe

            if (habilidad.isAnimationFinished(stateTime)) { // Si la animación de golpe ha terminado
                golpeando = false; // Restablece el estado de golpeo
                desactivarGolpeFixtures(); // Asegura que el golpe esté desactivado
            }
        } else { // Lógica de movimiento normal si no está golpeando
            boolean presionando = false;
            izq = der = abj = arr = false; // Reinicia las banderas de dirección

            // Detección de entrada para movimiento de Knuckles (teclas I, K, J, L)
            if (Gdx.input.isKeyPressed(Input.Keys.I)) { // Arriba
                body.setLinearVelocity(0, velocidad.y);
                arr = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.K)) { // Abajo
                body.setLinearVelocity(0, -velocidad.y);
                abj = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.J)) { // Izquierda
                body.setLinearVelocity(-velocidad.x, body.getLinearVelocity().y);
                izq = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.L)) { // Derecha
                body.setLinearVelocity(velocidad.x, body.getLinearVelocity().y);
                der = true;
                presionando = true;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) { // Tecla P para golpear
                golpear();
                presionando = true;
            }

            if (presionando) {
                stateTime += alpha; // Actualiza el tiempo de estado si hay movimiento
            } else {
                body.setLinearVelocity(0, 0); // Detiene el movimiento si no hay teclas presionadas
                stateTime = 0f; // Reinicia el tiempo de estado
            }

            super.actualizar(alpha); // Llama al método actualizar de la clase padre (Amigas)
        }
        posicion = body.getPosition(); // Actualiza la posición del personaje
    }

    /**
     * Dibuja a Knuckles y, si está golpeando, la animación de impacto.
     *
     * @param batch El SpriteBatch utilizado para dibujar.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch); // Dibuja el sprite principal de Knuckles
        if (mostrarImpacto) { // Si la animación de impacto debe mostrarse
            estadoImpacto += Gdx.graphics.getDeltaTime(); // Actualiza el tiempo de estado de la animación de impacto
            frameImpacto = explosion.getKeyFrame(estadoImpacto, false); // Obtiene el frame actual (no en bucle)

            float distancia = 0.8f;
            float offsetX = 0, offsetY = 0;

            // Calcula el offset para dibujar la explosión en la dirección del golpe
            switch (direccionImpacto) {
                case 1: offsetX = -distancia; break;
                case 2: offsetX = distancia; break;
                case 3: offsetY = distancia; break;
                case 4: offsetY = -distancia; break;
                case 5: offsetX = -distancia; offsetY = distancia; break;
                case 6: offsetX = distancia;  offsetY = distancia; break;
                case 7: offsetX = -distancia; offsetY = -distancia; break;
                case 8: offsetX = distancia;  offsetY = -distancia; break;
            }

            float x = body.getPosition().x + offsetX; // Posición X de la explosión
            float y = body.getPosition().y + offsetY; // Posición Y de la explosión

            if (frameImpacto != null) {
                batch.draw(frameImpacto,
                    x - frameImpacto.getRegionWidth() / 2f / Constantes.PPM,
                    y - frameImpacto.getRegionHeight() / 2f / Constantes.PPM,
                    frameImpacto.getRegionWidth() / Constantes.PPM,
                    frameImpacto.getRegionHeight() / Constantes.PPM
                );
            }

            // Cuando termina la animación de explosión, desactívala
            if (explosion.isAnimationFinished(estadoImpacto)) {
                mostrarImpacto = false;
            }
        }
    }

    /**
     * Libera los recursos utilizados por Knuckles, como el atlas de texturas.
     */
    @Override
    public void dispose() {
        atlas.dispose(); // Libera el atlas principal de texturas
        // Nota: el atlas de explosión (atlas2) también debería ser dispuesto si no se comparte.
        // En este caso, como se crea localmente, se asume que se gestiona adecuadamente.
    }
}

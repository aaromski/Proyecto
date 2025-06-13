
package io.github.Sonic_Test;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private float posX = 50;
    private float posY = 50;
    private float velocidad = 200;
    private boolean salta = false;
    private float velocidadSalto = 300; // Velocidad inicial del salto
    private float gravedad = -900; // Gravedad que afecta el salto
    private float velocidadY = 0; // Velocidad vertical
    private SpriteBatch batch;
    private TextureRegion[] animacionCorriendo;
    private TextureAtlas frames;
    private Animation<TextureRegion> corriendo;
    private Animation<TextureRegion> saltando;
    private float stateTime = 0f;
    private TextureRegion[] animacionSaltando;
    private TextureRegion frameActual;
    private OrthographicCamera camara;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    @Override
    public void create() {

        // Cargar el mapa TMX
        TmxMapLoader cargarMapa = new TmxMapLoader();
        map = cargarMapa.load("Escenario2.tmx");

        // Crear el renderizador del mapa
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        camara = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camara.position.set(posX, posY, 0); // Centrar la cámara en el personaje
        camara.update();
        batch = new SpriteBatch();
        animacionCorriendo = new TextureRegion[7];
        animacionCorriendo[0] = new TextureRegion(new Texture("spritesonic/spritesonic0.png"));
        animacionCorriendo[1] = new TextureRegion(new Texture("spritesonic/spritesonic1.png"));
        animacionCorriendo[2] = new TextureRegion(new Texture("spritesonic/spritesonic2.png"));
        animacionCorriendo[3] = new TextureRegion(new Texture("spritesonic/spritesonic3.png"));
        animacionCorriendo[4] = new TextureRegion(new Texture("spritesonic/spritesonic4.png"));
        animacionCorriendo[5] = new TextureRegion(new Texture("spritesonic/spritesonic5.png"));
        animacionCorriendo[6] = new TextureRegion(new Texture("spritesonic/spritesonic6.png"));

        corriendo = new Animation<>(0.1f, animacionCorriendo);
        corriendo.setPlayMode(Animation.PlayMode.LOOP); // 🔄 Activa el bucle

        animacionSaltando = new TextureRegion[8];
        animacionSaltando[0] = new TextureRegion(new Texture("spritesonic/salto1.png"));
        animacionSaltando[1] = new TextureRegion(new Texture("spritesonic/salto2.png"));
        animacionSaltando[2] = new TextureRegion(new Texture("spritesonic/salto3.png"));
        animacionSaltando[3] = new TextureRegion(new Texture("spritesonic/salto4.png"));
        animacionSaltando[4] = new TextureRegion(new Texture("spritesonic/salto5.png"));
        animacionSaltando[5] = new TextureRegion(new Texture("spritesonic/salto6.png"));
        animacionSaltando[6] = new TextureRegion(new Texture("spritesonic/salto7.png"));
        animacionSaltando[7] = new TextureRegion(new Texture("spritesonic/salto8.png"));

        saltando = new Animation<>(0.2f, animacionSaltando);
        saltando.setPlayMode(Animation.PlayMode.LOOP); // 🔄 Activa el bucle

        frameActual = new TextureRegion();
        for (TextureRegion frame : animacionCorriendo) {
            if (frame == null) {
                System.out.println("Error: Un frame en animacionCorriendo es NULL");
            }
        }
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime(); // Tiempo entre frames
        frameActual = animacionCorriendo[0];
        boolean moviendoIzquierda = false;
        boolean moviendoDerecha = false;

        // Movimiento con teclado
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            posX -= velocidad * delta;
            moviendoIzquierda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            posX += velocidad * delta;
            moviendoDerecha = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && !salta) {
            salta = true;
            velocidadY = velocidadSalto; // Aplicar impulso inicial
        }

        camara.position.x = Math.max(Gdx.graphics.getWidth() / 2, Math.min(posX, 2048 - Gdx.graphics.getWidth() / 2));
        camara.position.y = Math.max(Gdx.graphics.getHeight() / 2, Math.min(posY, 2048 - Gdx.graphics.getHeight() / 2));
        camara.position.set(posX, posY, 0);
        camara.update();

        if (salta) {
            velocidadY += gravedad * delta; // Aplicar gravedad
            posY += velocidadY * delta; // Actualizar posición vertical

            // Si toca el suelo, detener el salto
            if (posY <= 50) { // Ajusta según el suelo de tu juego
                posY = 50;
                salta = false;
                velocidadY = 0;
            }
        }

        // Seleccionar animación según el estado
        if (salta) {
            stateTime += delta;
            frameActual = saltando.getKeyFrame(stateTime, false); // No en bucle
            if (frameActual == null) {
                System.out.println("Error: getKeyFrame() devolvió NULL");
            }
        } else if (moviendoIzquierda || moviendoDerecha) {
            stateTime += delta;
            frameActual = corriendo.getKeyFrame(stateTime, true);
            if (corriendo.getKeyFrames().length == 0) {
                System.out.println("Error: corriendo no tiene frames cargados");
            }
        } else {
            frameActual = corriendo.getKeyFrame(0, false);
            if (frameActual == null) {
                System.out.println("Error: getKeyFrame3() devolvió NULL");
            }
        }


        // Invertir el sprite si se mueve a la izquierda
        if (frameActual != null && moviendoIzquierda && !frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }
        if (frameActual != null && moviendoDerecha && frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }

        // Dibujar en nueva posición
        ScreenUtils.clear(0, 0, 0, 1);

        // 📌 Renderizar el mapa antes del personaje
        mapRenderer.setView(camara);
        mapRenderer.render();


        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(frameActual, posX, posY); // Personaje
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (TextureRegion frame : animacionCorriendo) {
            frame.getTexture().dispose();
        }
    }

}

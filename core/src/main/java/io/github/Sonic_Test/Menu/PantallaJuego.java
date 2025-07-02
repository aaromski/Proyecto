package io.github.Sonic_Test.Menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_Test.Main;
import io.github.Sonic_Test.Sonic;
import io.github.Sonic_Test.crearObjetos;

public class PantallaJuego implements Screen {

    private final Main game;
    private final OrthographicCamera camara;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final crearObjetos obj;
    private final Sonic sonic;

    private SpriteBatch batch;
    private ShapeRenderer shape;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture texturaBoton;


    // Pausa
    private boolean enPausa = false;
    private float alphaPausa = 0f;

    public PantallaJuego(Main game) {
        this.game = game;

        TmxMapLoader cargarMapa = new TmxMapLoader();
        map = cargarMapa.load("Escenario2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 0.05f);

        camara = new OrthographicCamera();
        camara.setToOrtho(false, 32, 18);
        camara.update();

        obj = new crearObjetos();

        BodyDef bodySonic = new BodyDef();
        bodySonic.position.set(20, 10);
        bodySonic.type = BodyDef.BodyType.DynamicBody;

        CircleShape box = new CircleShape();
        box.setRadius(0.5f);
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = box;
        fixDef.friction = 1f;

        Body oBody = obj.world.createBody(bodySonic);
        oBody.setLinearDamping(5f);
        oBody.createFixture(fixDef);

        sonic = new Sonic(oBody);

        obj.crearPlataforma(2f, 1f);
        obj.crearPlataforma(8f, 2f);
        obj.crearPlataforma(14f, 3f);
        obj.crearPlataforma(20f, 4f);
        obj.objetosMapa(map);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        font = new BitmapFont();
        font = new BitmapFont();  // Usa la fuente por defecto
        // Ajusta el tamaño a tu escala de cámara
        layout = new GlyphLayout();
        texturaBoton = new Texture("Menu/button.png"); // Usa la misma textura que en el menú

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            enPausa = !enPausa;
            if (enPausa) alphaPausa = 0f;
        }

        if (!enPausa) {
            sonic.actualizar(delta);
            obj.actualizar(delta);
        }

        Vector2 sonicPos = sonic.body.getPosition();
        camara.position.set(sonicPos.x, sonicPos.y, 0);
        camara.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camara);
        mapRenderer.render(new int[]{ map.getLayers().getIndex("capa") });

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        sonic.render(batch);
        batch.end();

        if (enPausa) {
            if (alphaPausa < 1f) {
                alphaPausa += delta * 2f;
                if (alphaPausa > 1f) alphaPausa = 1f;
            }

            shape.setProjectionMatrix(camara.combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(0, 0, 0, 0.6f * alphaPausa);
            shape.rect(camara.position.x - camara.viewportWidth / 2,
                camara.position.y - camara.viewportHeight / 2,
                camara.viewportWidth,
                camara.viewportHeight);
            shape.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            if (alphaPausa >= 0.8f) {
                // Declaraciones
                float botonW = 6f;
                float cx = camara.position.x - botonW / 2f;
                float cy1 = camara.position.y + 3f;  // SEGUIR
                float cy3 = camara.position.y;      // MENÚ
                float cy2 = camara.position.y - 3f; // SALIR

                // Convertir a coordenadas de pantalla
                Vector3 screenPos1 = camara.project(new Vector3(cx, cy1, 0));
                Vector3 screenPos3 = camara.project(new Vector3(cx, cy3, 0));
                Vector3 screenPos2 = camara.project(new Vector3(cx, cy2, 0));
                int botonPixelW = (int)(botonW * (Gdx.graphics.getWidth() / camara.viewportWidth));
                float botonH = 2f;
                int botonPixelH = (int)(botonH * (Gdx.graphics.getHeight() / camara.viewportHeight));

                batch.setProjectionMatrix(batch.getProjectionMatrix().idt().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                batch.begin();

                // Dibujar botones
                batch.draw(texturaBoton, screenPos1.x, screenPos1.y, botonPixelW, botonPixelH); // SEGUIR
                batch.draw(texturaBoton, screenPos3.x, screenPos3.y, botonPixelW, botonPixelH); // MENÚ
                batch.draw(texturaBoton, screenPos2.x, screenPos2.y, botonPixelW, botonPixelH); // SALIR

                font.setColor(1, 1, 1, 1);

                layout.setText(font, "SEGUIR");
                font.draw(batch, "SEGUIR",
                    screenPos1.x + botonPixelW / 2f - layout.width / 2f,
                    screenPos1.y + botonPixelH / 2f + layout.height / 2f);

                layout.setText(font, "MENÚ");
                font.draw(batch, "MENÚ",
                    screenPos3.x + botonPixelW / 2f - layout.width / 2f,
                    screenPos3.y + botonPixelH / 2f + layout.height / 2f);

                layout.setText(font, "SALIR");
                font.draw(batch, "SALIR",
                    screenPos2.x + botonPixelW / 2f - layout.width / 2f,
                    screenPos2.y + botonPixelH / 2f + layout.height / 2f);

                batch.end();

                if (Gdx.input.justTouched()) {
                    Vector3 click = camara.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                    float mx = click.x;
                    float my = click.y;

                    if (mx >= cx && mx <= cx + botonW) {
                        if (my >= cy1 && my <= cy1 + botonH) {
                            enPausa = false;
                        } else if (my >= cy3 && my <= cy3 + botonH) {
                            game.setScreen(new MenuPrincipal(game)); // ← Regresa al menú
                            dispose();
                        } else if (my >= cy2 && my <= cy2 + botonH) {
                            Gdx.app.exit();
                        }
                    }
                }
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        camara.viewportWidth = width / 32f;
        camara.viewportHeight = height / 32f;
        camara.update();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();
        font.dispose();
        layout.reset();
        sonic.dispose();
        map.dispose();
        obj.dispose();
        texturaBoton.dispose();

    }
}

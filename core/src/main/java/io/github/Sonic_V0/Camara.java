package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;



public class Camara {
   // private final Viewport viewport;
    private final OrthographicCamera camara;
    public Camara() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 50f, 40f); // Ajusta según tu escala y resolución
        //viewport = new FitViewport(50f, 40f, camara); // O ExtendViewport si lo prefieres
        camara.update();
    }

    public OrthographicCamera getCamara() {
        return camara;
    }

    public Vector3 getProject (float cx, float cy, float cz) {
        return  camara.project(new Vector3(cx,cy,cz));
    }

    public Rectangle getVistaRectangular() {
        return new Rectangle(camara.position.x - camara.viewportWidth / 2f,
            camara.position.y - camara.viewportHeight / 2f, camara.viewportWidth, camara.viewportHeight);
    }

    /*public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /*public Viewport getViewport() {
        return viewport;
    }

    public void aplicarYCentrar() {
        viewport.apply();
        camara.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        camara.update();
    }*/
}

package io.github.Sonic_V0;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Camara {
    private final OrthographicCamera camara;
    public Camara() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 50f, 41.1f); // Ajusta según tu escala y resolución
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
}

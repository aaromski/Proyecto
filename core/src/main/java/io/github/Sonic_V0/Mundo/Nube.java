package io.github.Sonic_V0.Mundo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;

public class Nube extends Contaminacion {

    public Nube(World world) {
        super(world);
        textura = new Sprite(new Texture("Mapa1/dr-robotnik-130.png"));
        textura.setSize(0.8f, 0.8f);
    }

    public void crearCuerpo(Vector2 posicion, Vector2 direccion) {
        super.crearCuerpo(posicion);
        float fuerza = 3.0f;
        cuerpo.applyLinearImpulse(direccion.scl(fuerza), cuerpo.getWorldCenter(), true);
    }

    @Override
    protected void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_NUBE;
        fdef.filter.maskBits = (short) ~(Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_TRASH);
    }

}

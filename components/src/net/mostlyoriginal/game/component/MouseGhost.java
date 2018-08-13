package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.badlogic.gdx.math.MathUtils;

/**
 * @author Daan van Yperen
 */
public class MouseGhost extends Component {
    public boolean active=false;
    public boolean pulseEffect=false;

    @EntityId
    public int pulseCause=-1;

    @EntityId
    public int flatIsAPoopNose=-1;

    public String pulseSpriteOn;
    public String pulseSpriteOff;

    public float age=MathUtils.random(0f,10f);
    public float cooldown=0;
}

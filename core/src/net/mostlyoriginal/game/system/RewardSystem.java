package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.JamOperationFactory;
import net.mostlyoriginal.game.component.Cashable;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;

import static net.mostlyoriginal.api.operation.OperationFactory.*;
import static net.mostlyoriginal.api.utils.Duration.milliseconds;

/**
 * @author Daan van Yperen
 */
public class RewardSystem extends FluidIteratingSystem {

    private static final float FADEOUT_DURATION = 1f;
    private static final float GLOW_MAX_SCALE = 2f;
    private static final int SPACING_BETWEEN_BONUSES = 10;
    private static final int PITSTOP_LENGTH_BONUS = 2;
    private static final int MS_DELAY_PER_TEXTITEM = 40;

    public RewardSystem() {
        super(Aspect.all(Cashable.class));
    }

    private TowedSystem towedSystem;

    int chainLengthShacklePoints[] = {0, 10, 20, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
    int chainLengthChainBonus[] = {0, 0, 0, 200, 500, 1000, 1500, 2000, 4000, 8000, 10000, 10000, 10000, 10000, 10000, 10000};
    int chainMulticolorBonus[] = {0, 0, 0, 200, 500, 1000, 1500, 2000, 4000, 8000, 10000, 10000, 10000, 10000, 10000, 10000};

    int rewardCount = 0;
    float textDelay = MS_DELAY_PER_TEXTITEM;

    @Override
    protected void process(E shackle) {
        shackle.cashableCooldown(shackle.cashableCooldown() - world.delta);


        textDelay = 1 + shackle.cashableCooldown();

//        if ( shackle.cashableCooldown() <= 0 )
        {
            final int x = GridSnapSystem.gridX(shackle) * G.CELL_SIZE;
            final int y = GridSnapSystem.gridY(shackle) * G.CELL_SIZE;

            rewardCount = 0;
            int multiplier = Math.max(1,shackle.cashableMultiplier());
            final boolean isPitstop = shackle.cashableType() == Cashable.Type.PITSTOP;
            final int bonusLength = isPitstop ? PITSTOP_LENGTH_BONUS : 0;

            rewardPoints(multiplier*chainLengthShacklePoints[shackle.cashableChainLength() + bonusLength], x, y + G.CELL_SIZE);

            if ( multiplier > 1 ) {
                textDelay = shackle.cashableCooldown() + 200;
                createFloaterLabel("x" + multiplier, x, y + G.CELL_SIZE,  MathUtils.random(5, 10),   MathUtils.random(60, 65), "ital");
            }

            if (shackle.cashableChainBonusPayout()) {
                textDelay = shackle.cashableCooldown() + 300;
                rewardBonus(multiplier*chainLengthChainBonus[shackle.cashableChainLength() + bonusLength], x, y + G.CELL_SIZE + (rewardCount * SPACING_BETWEEN_BONUSES), isPitstop ? " PITSTOP BONUS!" : " CHAIN BONUS!");
            }
            if (shackle.cashableChainMulticolorPayout()) {
                textDelay = shackle.cashableCooldown() + 500;
                rewardBonus(multiplier*chainMulticolorBonus[shackle.cashableChainLength()+ bonusLength], x, y + G.CELL_SIZE + (rewardCount * SPACING_BETWEEN_BONUSES), " MULTICOLOR BONUS!");
            }

            sfx(shackle, x, y);
            killTows(shackle);
            shackle.removeCashable();
            shackle.script(deleteFromWorld());
        }
    }

    private void rewardPoints(int chainLengthShacklePoint, int x, int y) {
        payout(chainLengthShacklePoint, "" + chainLengthShacklePoint, x, y, MathUtils.random(5, 10), MathUtils.random(60, 65), "italsmall");
    }

    private void rewardBonus(int chainLengthChainNonus, int x, int y, String suffix) {
        payout(chainLengthChainNonus, "+" + chainLengthChainNonus + suffix, x + 5, y, MathUtils.random(-10, 10), MathUtils.random(40, 45), "ital");
    }

    private static final Tint YELLOW = new Tint("ffff00ff");
    ;

    private void payout(int points, String label, int x, int y, int targetX, int targetY, String font) {
        if (points == 0) return;
        createFloaterLabel(label, x, y, targetX, targetY, font);
        rewardCount++;
    }

    private void createFloaterLabel(String label, int x, int y, int targetX, int targetY, String font) {
        E.E()
                .posX(x)
                .posY(y)
                .renderLayer(G.LAYER_PARTICLES + 1)
                .fontFontName(font)
                .renderLayer(50000)
                .physicsFriction(0)
                .labelText(label)
                .tint(1f, 1f, 1f, 0f)
                .script(sequence(
                        delay(milliseconds(textDelay)),
                        JamOperationFactory.tintBetween(Tint.TRANSPARENT,Tint.WHITE, 0.1f),
                        JamOperationFactory.moveBetween(x, y, x + targetX, y + targetY, 0.5f, Interpolation.pow2Out),
                        deleteFromWorld()
                ));
        textDelay += MS_DELAY_PER_TEXTITEM;
    }

    private void sfx(E shackle, float x, float y) {

        E.E()
                .posX(x)
                .posY(y)
                .renderLayer(G.LAYER_PLAYER-10)
                .anim(shackle.animId() + "-score")
                .script(sequence(
                        delay(milliseconds(250)),
                        deleteFromWorld()
                ));
    }

    private void killTows(E shackle) {
        if (shackle.hasTowing()) {
            // currenly cargo loaded.
            towedSystem.disconnectCargoFrom(shackle, true);
        }
        if (shackle.hasTowed()) {
            towedSystem.disconnectFromTowingCar(shackle, true);
        }
    }

}

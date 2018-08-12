package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.api.EBag;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.Goal;
import net.mostlyoriginal.game.component.Producing;
import net.mostlyoriginal.game.component.ProductType;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.system.IsometricConversionService;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.render.TransitionSystem;
import net.mostlyoriginal.game.util.ScriptUtils;

/**
 * @author Daan van Yperen
 */
public class GoalReachedSystem extends FluidIteratingSystem {


    private EntitySubscription producerSubscription;
    private boolean goalReached;
    private boolean done = false;
    private TransitionSystem transitionSystem;

    public GoalReachedSystem() {
        super(Aspect.all(Goal.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        producerSubscription = world.getAspectSubscriptionManager().get(Aspect.all(Producing.class));
    }

    @Override
    protected void begin() {
        super.begin();
        goalReached = true;
    }

    @Override
    protected void end() {
        super.end();
        if (goalReached && !done) {
            done = true;
            G.level += 1;
            transitionSystem.transition(GameScreen.class, 2);
        }
    }

    @Override
    protected void process(E e) {
        if (done) return;
        E producer = findProducer(e.goalType());
        if (producer != null) {
            int max = producer.producingCount();
            int reservedIndex = producer.producingReserved();
            producer.producingReserved(reservedIndex + 1);
            if (!e.hasScript()) {
                ScriptUtils.graduallyMoveTowards(e, producer.posX() + IsometricConversionService.ISO_X - max * 10 + reservedIndex * 20, producer.posY() + 128 + 32, Duration.milliseconds(1000));
            }
        } else {
            goalReached = false;
            if (!e.hasScript()) {
                ScriptUtils.graduallyMoveTowards(e, e.goalStartX(), e.goalStartY(), Duration.milliseconds(1000));
            }
        }

    }

    private E findProducer(ProductType productType) {
        IntBag entities = producerSubscription.getEntities();
        for (int i = 0, s = entities.size(); i < s; i++) {
            final E producer = E.E(entities.get(i));
            if (producer.producingProduct() == productType && (producer.producingCount() - producer.producingReserved()) > 0) {
                return producer;
            }
        }
        return null;
    }
}

package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import net.mostlyoriginal.api.utils.Duration;
import net.mostlyoriginal.game.api.EBag;
import net.mostlyoriginal.game.component.Goal;
import net.mostlyoriginal.game.component.Producing;
import net.mostlyoriginal.game.component.ProductType;
import net.mostlyoriginal.game.system.IsometricConversionService;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.util.ScriptUtils;

/**
 * @author Daan van Yperen
 */
public class GoalReachedSystem extends FluidIteratingSystem {


    private EntitySubscription producerSubscription;

    public GoalReachedSystem() {
        super(Aspect.all(Goal.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        producerSubscription = world.getAspectSubscriptionManager().get(Aspect.all(Producing.class));
    }

    @Override
    protected void process(E e) {
        E producer = findProducer(e.goalType());
        if (producer != null) {
            producer.producingReserved(producer.producingReserved() + 1);
            if ( !e.hasScript()) {
                ScriptUtils.graduallyMoveTowards(e, producer.posX() + IsometricConversionService.ISO_X - e.getBounds().cx(), producer.posY() + 128 + 32, Duration.milliseconds(1000));
            }
        } else {
            if ( !e.hasScript()) {
                ScriptUtils.graduallyMoveTowards(e, e.goalStartX(), e.goalStartY(), Duration.milliseconds(1000));
            }
        }

    }

    private E findProducer(ProductType productType) {
        IntBag entities = producerSubscription.getEntities();
        for (int i = 0, s = entities.size(); i < s; i++) {
            final E producer = E.E(entities.get(i));
            if (producer.producingProduct() == productType && (producer.producingCount()-producer.producingReserved()) > 0) {
                return producer;
            }
        }
        return null;
    }
}

package botwithus.script;

import net.botwithus.scripts.Script;
import java.util.function.BooleanSupplier;

public abstract class TickBasedScript extends Script {
    private int tickDelay = 0;
    private BooleanSupplier delayCondition = null;
    private boolean delayWhileMode = false;
    private int maxWaitTicks = 0;
    private int waitedTicks = 0;

    @Override
    public final void run() {
        if (handleDelays()) {
            return;
        }
        onTick();
    }

    private boolean handleDelays() {
        if (tickDelay > 0) {
            tickDelay--;
            return true;
        }
        if (delayCondition != null) {
            boolean condition = delayCondition.getAsBoolean();
            waitedTicks++;
            if ((delayWhileMode && condition) || (!delayWhileMode && !condition)) {
                if (maxWaitTicks > 0 && waitedTicks >= maxWaitTicks) {
                    resetDelay();
                    return false;
                }
                return true;
            }
            resetDelay();
        }
        return false;
    }

    private void resetDelay() {
        delayCondition = null;
        delayWhileMode = false;
        maxWaitTicks = 0;
        waitedTicks = 0;
    }

    protected void delayTicks(int ticks) {
        this.tickDelay = Math.max(0, ticks);
    }

    protected void delayUntil(BooleanSupplier condition, int maxWaitTicks) {
        this.delayCondition = condition;
        this.delayWhileMode = false;
        this.maxWaitTicks = maxWaitTicks;
        this.waitedTicks = 0;
    }

    protected void delayWhile(BooleanSupplier condition, int maxWaitTicks) {
        this.delayCondition = condition;
        this.delayWhileMode = true;
        this.maxWaitTicks = maxWaitTicks;
        this.waitedTicks = 0;
    }

    protected abstract void onTick();
}


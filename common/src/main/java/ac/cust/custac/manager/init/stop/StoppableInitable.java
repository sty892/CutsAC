package ac.cust.custac.manager.init.stop;

import ac.cust.custac.manager.init.Initable;

public interface StoppableInitable extends Initable {
    void stop();
}

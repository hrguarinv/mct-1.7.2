package gov.nasa.arc.mct.platform;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

public final class RootComponent extends AbstractComponent {
    
    public RootComponent() {
        setDisplayName("All");
        ComponentInitializer capability = getCapability(ComponentInitializer.class);
        capability.setOwner("admin");
        capability.setCreator("admin");
    }

}

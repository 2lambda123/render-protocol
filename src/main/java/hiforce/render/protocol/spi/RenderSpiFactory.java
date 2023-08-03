package hiforce.render.protocol.spi;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Rocky
 * @since 2020/8/13
 */
@SuppressWarnings("all")
public class RenderSpiFactory {

    private static RenderSpiFactory instance;

    private List<ComponentBuilderSpi> componentBuilders;

    private RenderSpiFactory() {

    }

    public List<ComponentBuilderSpi> getComponentBuilderByTag(String tag) {
        return getComponentBuilders().stream()
                .filter(p -> p.supportTag(tag))
                .collect(Collectors.toList());
    }

    public List<ComponentBuilderSpi> getComponentBuilders() {
        if (CollectionUtils.isEmpty(componentBuilders)) {
            synchronized (RenderSpiFactory.class) {
                if (null == componentBuilders) {
                    ServiceLoader<ComponentBuilderSpi> serializers = ServiceLoader.load(ComponentBuilderSpi.class);
                    componentBuilders = StreamSupport.stream(serializers.spliterator(), false)
                            .collect(Collectors.toList());
                }
            }
        }
        return componentBuilders;
    }

    public static RenderSpiFactory getInstance() {
        if (null == instance) {
            synchronized (RenderSpiFactory.class) {
                if (null == instance) {
                    instance = new RenderSpiFactory();
                }
            }
        }
        return instance;
    }
}

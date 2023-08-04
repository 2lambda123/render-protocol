package hiforce.render.protocol.builder;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import hiforce.render.protocol.model.BaseCO;
import hiforce.render.protocol.model.OrderCO;
import hiforce.render.protocol.session.result.ShoppingResult;
import hiforce.render.protocol.spi.ComponentBuilderSpi;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Rocky
 * @since 2020/8/13
 */
@AutoService(ComponentBuilderSpi.class)
public class OrderCoBuilder implements ComponentBuilderSpi<ShoppingResult, ShoppingResult, OrderCO> {
    @Override
    public boolean supportTag(String tag) {
        return StringUtils.equals(tag, "order");
    }

    @Override
    public List<ShoppingResult> extract(String tag, ShoppingResult result, BaseCO parent) {
        return Lists.newArrayList(result);
    }

    @Override
    public OrderCO build(String tag, ShoppingResult result, ShoppingResult model, BaseCO parent) {
        OrderCO orderCO = new OrderCO();
        orderCO.setId(result.getRenderParam().getSeller());
        orderCO.setSeller(result.getRenderParam().getSeller());
        return orderCO;
    }
}

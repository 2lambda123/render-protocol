package hiforce.render.protocol.builder;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import hiforce.render.protocol.model.BaseCO;
import hiforce.render.protocol.model.ItemCO;
import hiforce.render.protocol.session.result.ShoppingResult;
import hiforce.render.protocol.spi.ComponentBuilderSpi;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Rocky
 * @since 2020/8/13
 */
@AutoService(ComponentBuilderSpi.class)
public class ItemCoBuilder implements ComponentBuilderSpi<ShoppingResult, ShoppingResult, ItemCO> {
    @Override
    public boolean supportTag(String tag) {
        return StringUtils.equals(tag, "item");
    }

    @Override
    public List<ShoppingResult> extract(String tag, ShoppingResult result, BaseCO parent) {
        return Lists.newArrayList(result);
    }

    @Override
    public ItemCO build(String tag, ShoppingResult result, ShoppingResult model, BaseCO parent) {
        ItemCO itemCO = new ItemCO();
        itemCO.setId(result.getRenderParam().getItemId());
        itemCO.setParentId(result.getRenderParam().getSeller());
        itemCO.setBuyQuantity(result.getRenderParam().getBuyQuantity());
        itemCO.setUnitPrice(10000);
        return itemCO;
    }
}

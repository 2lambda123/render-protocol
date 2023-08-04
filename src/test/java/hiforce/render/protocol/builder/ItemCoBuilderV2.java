package hiforce.render.protocol.builder;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import hiforce.render.protocol.model.BaseCO;
import hiforce.render.protocol.model.ItemCO;
import hiforce.render.protocol.session.result.ViewItemDetailResult;
import hiforce.render.protocol.spi.ComponentBuilderSpi;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 这个类用于测试，对ItemCO组件的构建，在结果模型不匹配情况下，是否会出错
 * @author Rocky
 * @since 2020/8/13
 */
@AutoService(ComponentBuilderSpi.class)
public class ItemCoBuilderV2 implements ComponentBuilderSpi<ViewItemDetailResult, ViewItemDetailResult, ItemCO> {
    @Override
    public boolean supportTag(String tag) {
        return StringUtils.equals(tag, "item");
    }

    @Override
    public List<ViewItemDetailResult> extract(String tag, ViewItemDetailResult result, BaseCO parent) {
        return Lists.newArrayList(result);
    }

    @Override
    public ItemCO build(String tag, ViewItemDetailResult result, ViewItemDetailResult model, BaseCO parent) {
        ItemCO itemCO = new ItemCO();
        itemCO.setId(result.getRenderParam().getItemId());
        itemCO.setParentId(result.getRenderParam().getSeller());
        itemCO.setBuyQuantity(result.getRenderParam().getBuyQuantity());
        itemCO.setUnitPrice(10000);
        return itemCO;
    }
}

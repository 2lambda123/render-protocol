package hiforce.render.protocol.session.result;

import com.google.common.collect.Lists;
import hiforce.render.protocol.model.BaseCO;
import hiforce.render.protocol.model.IPageRenderResult;
import hiforce.render.protocol.model.config.StructureConfig;
import hiforce.render.protocol.model.config.StructureItem;
import hiforce.render.protocol.session.param.BuyRenderParam;
import hiforce.render.protocol.session.param.SubmitParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Rocky Yu
 * @since 2021/7/31
 */
public class ShoppingResult implements IPageRenderResult {

    @Getter
    @Setter
    private StructureConfig structureConfig;

    @Getter
    private final List<BaseCO> components = Lists.newArrayList();

    @Getter
    @Setter
    private BuyRenderParam renderParam;

    @Getter
    @Setter
    private boolean reloadPage = true;

    @Override
    public SubmitParam createSubmitParam() {
        return new SubmitParam();
    }


    public static ShoppingResult init(BuyRenderParam renderParam) {
        ShoppingResult result = new ShoppingResult();
        result.setRenderParam(renderParam);
        result.setStructureConfig(buildStructure());
        return result;
    }

    private static StructureConfig buildStructure() {
        StructureConfig config = new StructureConfig();
        config.setRoot(
                StructureItem.of("order").addItem(
                        StructureItem.of("item")
                )
        );
        return config;
    }
}

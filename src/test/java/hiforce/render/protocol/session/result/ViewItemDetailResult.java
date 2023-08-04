package hiforce.render.protocol.session.result;

import hiforce.render.protocol.model.BaseCO;
import hiforce.render.protocol.model.IPageRenderResult;
import hiforce.render.protocol.model.config.StructureConfig;
import hiforce.render.protocol.session.param.BuyRenderParam;
import hiforce.render.protocol.session.param.SubmitParam;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * @author Rocky Yu
 * @since 2021/7/31
 */
public class ViewItemDetailResult implements IPageRenderResult {

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
}

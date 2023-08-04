package hiforce.render.protocol;


import hiforce.render.protocol.engine.RenderEngine;
import hiforce.render.protocol.engine.impl.RenderEngineImpl;
import hiforce.render.protocol.engine.request.RenderPageReqDTO;
import hiforce.render.protocol.engine.response.RenderPageRespDTO;
import hiforce.render.protocol.model.Data;
import hiforce.render.protocol.model.ItemCO;
import hiforce.render.protocol.model.RenderData;
import hiforce.render.protocol.model.RenderFunction;
import hiforce.render.protocol.session.param.BuyRenderParam;
import hiforce.render.protocol.session.result.ShoppingResult;
import hiforce.render.protocol.utils.JacksonUtils;

/**
 * @author Rocky Yu
 * @since 2021/7/31
 */
public class AdjustRenderTester {

    private static final RenderEngine renderEngine = new RenderEngineImpl();

    public static void main(String[] args) {
        BuyRenderParam renderParam = buildOriginRenderParam();
        RenderPageRespDTO respDTO =
                renderEngine.renderPage(render -> buildShoppingRenderResult(true, renderParam, render));
        RenderData renderData = respDTO.getRenderData();

         System.out.println("===> 第一次同步渲染的数据：" + JacksonUtils.serializeWithoutException(respDTO.getRenderData()));
         System.out.println("-----------------------------------------");
         System.out.println("===> 模拟对其中一个组件进行操作，并触发异步请求");

        //模拟一次异步渲染的过程，模拟Item控件中的数量发生变化
        RenderPageReqDTO reqDTO = new RenderPageReqDTO();
        reqDTO.setOperator("item");
        reqDTO.setHierarchy(JacksonUtils.serializeWithoutException(renderData.getHierarchy()));
        reqDTO.setLinkage(JacksonUtils.serializeWithoutException(renderData.getLinkage()));
        Data data = new Data();
        ItemCO itemCO = buildChangedItemCO(renderParam);
        data.put(itemCO.getUniqueId(), itemCO);
        reqDTO.setData(JacksonUtils.serializeWithoutException(data));

        respDTO = renderEngine.adjustRenderPage(reqDTO, render ->
                buildShoppingRenderResult(false, renderParam, render));
         System.out.println("===> 异步请求渲染的数据：" + JacksonUtils.serializeWithoutException(respDTO.getRenderData()));
         System.out.println("===> 可以观察上面数据，只返回了变化的组件数据。前段只需要对变化的组件数据做重新渲染。");
         System.out.println("-----------------------------------------");
    }

    private static ItemCO buildChangedItemCO(BuyRenderParam renderParam) {
        ItemCO itemCO = new ItemCO();
        itemCO.setId(renderParam.getItemId());
        itemCO.setParentId(renderParam.getSeller());
        itemCO.setBuyQuantity(renderParam.getBuyQuantity() + 1);
        itemCO.setUnitPrice(10000);
        return itemCO;
    }

    private static ShoppingResult buildShoppingRenderResult(
            boolean reloadPage, BuyRenderParam origin, RenderFunction function) {
        BuyRenderParam renderParam = null == function.getRenderParam() ?
                origin : (BuyRenderParam) function.getRenderParam();

        ShoppingResult result = ShoppingResult.init(renderParam);
        result.setReloadPage(reloadPage);
        return result;
    }

    private static BuyRenderParam buildOriginRenderParam() {
        BuyRenderParam param = new BuyRenderParam();
        param.setSeller("Rocky");
        param.setItemId("item-20210731-001");
        param.setBuyQuantity(1);
        return param;
    }
}
